package com.group3.learningcommonsproject.controllers;

import com.group3.learningcommonsproject.entities.StaffInfo;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import com.group3.learningcommonsproject.repositories.StaffInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/staff-information")
public class StaffInfoController {
    private final StaffInfoRepository staffInfoRepository;

    @GetMapping
    public List<StaffInfo> getAllStaffInfo(
            @RequestParam(required = false, defaultValue = "FullName") String sort
    ) {
        return staffInfoRepository.findAll(Sort.by(sort));
    }

    @GetMapping("/filter")
    public java.util.List<StaffInfo> getStaffByProgram(@RequestParam Long programId) {
        return staffInfoRepository.findByPrograms_Id(programId);
    }
}