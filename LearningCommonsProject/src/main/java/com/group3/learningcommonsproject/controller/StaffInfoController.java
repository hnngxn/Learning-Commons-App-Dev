package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.StaffInfoDto;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import com.group3.learningcommonsproject.service.StaffInfoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/staff-information")
public class StaffInfoController {
    private final StaffInfoRepository staffInfoRepository;
    private final StaffInfoService staffInfoService;

    @GetMapping
    public List<StaffInfo> getAllStaffInfo(
            @RequestParam(required = false, defaultValue = "fullName") String sort
    ) {
        return staffInfoRepository.findAll(Sort.by(sort));
    }

    @PostMapping
    public StaffInfoDto createStaffInfo(@RequestBody StaffInfoDto data) {
        return staffInfoService.createStaffInfo(data);
    }

    @PutMapping("{id}")
    public StaffInfoDto updateStaffInfo(
            @PathVariable(name="id") Long id,
            @RequestBody StaffInfoDto data
    ) {
        return staffInfoService.updateStaffInfo(id, data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffInfo(@PathVariable Long id) {
        staffInfoService.deleteStaffInfo(id);

        return ResponseEntity.noContent().build();
    }

}