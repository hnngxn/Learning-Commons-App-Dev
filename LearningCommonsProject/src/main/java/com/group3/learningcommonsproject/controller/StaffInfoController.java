package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.StaffInfoDto;
import com.group3.learningcommonsproject.service.StaffInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/staff-information")
public class StaffInfoController {
    private final StaffInfoService staffInfoService;

    @GetMapping
    public List<StaffInfoDto> getAllStaffInfo(
            @RequestParam(required = false, defaultValue = "fullName") String sortBy
    ) {
        return staffInfoService.getAllStaffInfo(sortBy);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StaffInfoDto createStaffInfo(@Valid @RequestBody StaffInfoDto data) {
        return staffInfoService.createStaffInfo(data);
    }

    @PutMapping("/{id}")
    public StaffInfoDto updateStaffInfo(
            @PathVariable Long id,
            @Valid @RequestBody StaffInfoDto data
    ) {
        return staffInfoService.updateStaffInfo(id, data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffInfo(@PathVariable Long id) {
        staffInfoService.deleteStaffInfo(id);

        return ResponseEntity.noContent().build();
    }

}