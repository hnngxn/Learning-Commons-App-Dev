package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.EventSubmissionRequest;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.repositories.StaffInfoRepository;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class EventSubmissionService {

    private final StaffInfoRepository staff;
    private final PointEventRepository pointEvents;
    private final EventLogRepository eventLogs;
    private final PointCalculationService calculator;

    public EventSubmissionService(
            StaffInfoRepository staff,
            PointEventRepository pointEvents,
            EventLogRepository eventLogs,
            PointCalculationService calculator
    ) {
        this.staff = staff;
        this.pointEvents = pointEvents;
        this.eventLogs = eventLogs;
        this.calculator = calculator;
    }

    @Transactional
    public Long submit(EventSubmissionRequest request, String token) {
        if (request == null
                || request.staffId() == null || request.staffId() <= 0
                || request.pointEventId() == null || request.pointEventId() <= 0
                || request.eventDate() == null
                || request.enteredBy() == null
                || request.enteredBy().isBlank()
                || request.enteredBy().length() > 255
                || (request.comments() != null
                && request.comments().length() > 255)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide valid staff, event, date, entered-by name, and details."
            );
        }

        try {
            if (token == null || !UUID.fromString(token).toString().equals(token)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid submission token."
            );
        }

        var employee = staff.findByIdForUpdate(request.staffId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Staff member not found."
                ));

        // Check after locking, so requests for this staff member are serialized.
        if (eventLogs.findBySubmissionToken(token).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This submission has already been saved."
            );
        }

        var event = pointEvents.findById(request.pointEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Point event not found."
                ));

        if ("Infraction".equalsIgnoreCase(event.getEventType())
                && (request.comments() == null || request.comments().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Additional details are required for infractions."
            );
        }

        int applied = calculator.calculatePointsApplied(event, request.points());
        int balanceAfter;

        try {
            balanceAfter = Math.addExact(employee.getCurrentPoints(), applied);
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "The resulting balance is out of range."
            );
        }

        EventLog log = new EventLog();
        log.setStaff(employee);
        log.setPointEvent(event);
        log.setPointsApplied(applied);
        log.setEventDate(request.eventDate());
        log.setComments(
                request.comments() == null ? null : request.comments().strip()
        );
        log.setSubmissionToken(token);
        log.setBalanceAfter(balanceAfter);
        log.setPositionAtEntry(employee.getPosition1());
        log.setSupervisorAtEntry(employee.getSupervisor1());
        log.setEnteredBy(request.enteredBy().strip());

        employee.setCurrentPoints(balanceAfter);

        return eventLogs.saveAndFlush(log).getId();
    }
}