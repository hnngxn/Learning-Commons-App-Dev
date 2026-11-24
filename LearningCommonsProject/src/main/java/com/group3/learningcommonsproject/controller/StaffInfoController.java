package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff-information")
public class StaffInfoController {

    private final StaffInfoRepository staffInfoRepository;

    public StaffInfoController(StaffInfoRepository staffInfoRepository) {
        this.staffInfoRepository = staffInfoRepository;
    }

    @GetMapping("/filter")
    public ResponseEntity<List<StaffInfo>> filterStaffByProgram(@RequestParam(required = false) Long programId) {
        if (programId == null) {
            return ResponseEntity.ok(staffInfoRepository.findAll());
        }
        return ResponseEntity.ok(staffInfoRepository.findByPrograms_Id(programId));
    }
}