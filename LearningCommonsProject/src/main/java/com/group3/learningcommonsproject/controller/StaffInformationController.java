package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.StaffInformationDto;
import com.group3.learningcommonsproject.entity.StaffInformation;
import com.group3.learningcommonsproject.errorResponses.BadRequest;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import com.group3.learningcommonsproject.repository.StaffInformationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/staff")
public class StaffInformationController {

    private final StaffInformationRepository staffRepository;

    public StaffInformationController(StaffInformationRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    //Get all staff (and their supervisors from the join table)
    @GetMapping
    public List<StaffInformationDto> getAllStaff() {
        List<StaffInformationDto> result = new ArrayList<>();
        for (StaffInformation staff : staffRepository.findAllWithSupervisors()) {
            result.add(toDto(staff));
        }
        return result;
    }

    //Get only active supervisors (useful for frontend dropdowns)
    @GetMapping("/supervisors")
    public List<StaffInformationDto> getAllSupervisors() {
        List<StaffInformationDto> result = new ArrayList<>();
        for (StaffInformation supervisor : staffRepository.findByIsSupervisorTrueAndActiveTrueOrderByFullNameAsc()) {
            result.add(toDto(supervisor));
        }
        return result;
    }

    //Create a new staff member and link supervisors in staff_supervisors
    @PostMapping
    public StaffInformationDto createStaff(@RequestBody StaffInformationDto request) {
        StaffInformation staff = new StaffInformation();
        updateFieldsFromRequest(staff, request);

        StaffInformation saved = staffRepository.save(staff);
        return toDto(saved);
    }

    //Update an existing staff member and sync their supervisors
    @PutMapping("/{id}")
    public StaffInformationDto updateStaff(@PathVariable Long id, @RequestBody StaffInformationDto request) {
        StaffInformation staff = staffRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        // Prevent a staff member from being assigned as their own supervisor
        if (request.getSupervisorIds() != null && request.getSupervisorIds().contains(id)) {
            throw new BadRequest();
        }

        updateFieldsFromRequest(staff, request);

        StaffInformation saved = staffRepository.save(staff);
        return toDto(saved);
    }

    // --- Helper Methods ---

    private void updateFieldsFromRequest(StaffInformation staff, StaffInformationDto request) {
        staff.setFullName(request.getFullName());
        staff.setEmail(request.getEmail());
        staff.setPosition(request.getPosition());
        staff.setPoints(request.getPoints());
        staff.setSupervisor(request.isSupervisor());
        staff.setReturning(request.isReturning());
        staff.setActive(request.isActive());

        // Sync the staff_supervisors join table
        if (request.getSupervisorIds() != null) {
            List<StaffInformation> foundSupervisors = staffRepository.findAllById(request.getSupervisorIds());

            // Optional safety check: ensure every selected ID is actually flagged as a supervisor
            for (StaffInformation supervisor : foundSupervisors) {
                if (!supervisor.isSupervisor()) {
                    throw new BadRequest();
                }
            }
            staff.setSupervisors(new HashSet<>(foundSupervisors));
        } else {
            staff.getSupervisors().clear();
        }
    }

    private StaffInformationDto toDto(StaffInformation entity) {
        Set<Long> supervisorIds = new HashSet<>();
        Set<String> supervisorNames = new HashSet<>();

        if (entity.getSupervisors() != null) {
            for (StaffInformation supervisor : entity.getSupervisors()) {
                supervisorIds.add(supervisor.getId());
                supervisorNames.add(supervisor.getFullName());
            }
        }

        return new StaffInformationDto(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPosition(),
                entity.getPoints(),
                entity.isSupervisor(),
                entity.isReturning(),
                entity.isActive(),
                supervisorIds,
                supervisorNames
        );
    }
}
