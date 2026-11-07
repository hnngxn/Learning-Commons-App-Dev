package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.SemesterPointResetRequest;
import com.group3.learningcommonsproject.dto.SemesterPointResetResponse;
import com.group3.learningcommonsproject.entity.SemesterPointReset;
import com.group3.learningcommonsproject.entity.SemesterPointResetEntry;
import com.group3.learningcommonsproject.repository.SemesterPointResetRepository;
import com.group3.learningcommonsproject.repository.SemesterPointResetEntryRepository;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

@Service
public class SemesterPointResetService {

    private final SemesterPointResetRepository resets;
    private final SemesterPointResetEntryRepository entries;
    private final StaffInfoRepository staff;

    public SemesterPointResetService(
            SemesterPointResetRepository resets,
            SemesterPointResetEntryRepository entries,
            StaffInfoRepository staff
    ) {
        this.resets = resets;
        this.entries = entries;
        this.staff = staff;
    }

    @Transactional
    public SemesterPointResetResponse reset(
            SemesterPointResetRequest request
    ) {
        if (request == null || request.year() == null
                || request.year() < 2000 || request.year() > 2100
                || request.term() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide a year from 2000 to 2100 and a valid semester."
            );
        }

        if (request.employees() == null || request.employees().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Select at least one employee."
            );
        }

        var ids = new HashSet<Long>();

        for (var employee : request.employees()) {
            if (employee == null || employee.staffId() == null
                    || employee.staffId() <= 0 || employee.status() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Each employee needs a positive staff ID and status."
                );
            }

            if (!ids.add(employee.staffId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "An employee cannot appear more than once."
                );
            }
        }

        String term = request.term().name();

        if (resets.existsBySemesterYearAndSemesterTerm(request.year(), term)) {
            throw alreadyReset();
        }

        SemesterPointReset reset = new SemesterPointReset();
        reset.setSemesterYear(request.year());
        reset.setSemesterTerm(term);

        // The unique semester constraint also handles simultaneous requests.
        try {
            reset = resets.saveAndFlush(reset);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to create the reset; the semester may already be reset.",
                    exception
            );
        }

        var employees = new ArrayList<>(request.employees());
        employees.sort(Comparator.comparing(
                SemesterPointResetRequest.EmployeeSelection::staffId
        ));

        var results =
                new ArrayList<SemesterPointResetResponse.EmployeeResult>();

        for (var selection : employees) {
            var employee = staff.findByIdForUpdate(selection.staffId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Staff member not found: " + selection.staffId()
                    ));

            if (employee.getCurrentPoints() == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Staff member has no current balance: " + employee.getId()
                );
            }

            int previousPoints = employee.getCurrentPoints();
            int startingPoints =
                    selection.status()
                            == SemesterPointResetRequest.EmployeeStatus.NEW
                            ? 65 : 70;

            SemesterPointResetEntry entry = new SemesterPointResetEntry();
            entry.setReset(reset);
            entry.setStaff(employee);
            entry.setEmployeeStatus(selection.status().name());
            entry.setPreviousPoints(previousPoints);
            entry.setStartingPoints(startingPoints);

            entries.save(entry);
            employee.setCurrentPoints(startingPoints);

            results.add(new SemesterPointResetResponse.EmployeeResult(
                    employee.getId(),
                    selection.status().name(),
                    previousPoints,
                    startingPoints
            ));
        }

        entries.flush();

        return new SemesterPointResetResponse(
                reset.getId(),
                reset.getSemesterYear(),
                reset.getSemesterTerm(),
                reset.getResetAt(),
                results.size(),
                results
        );
    }

    private ResponseStatusException alreadyReset() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This semester has already been reset."
        );
    }
}