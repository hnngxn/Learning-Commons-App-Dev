package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.StaffInfoDto;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.mapper.StaffInfoMapper;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import com.group3.learningcommonsproject.services.StaffInfoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
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
            @RequestParam(required = false, defaultValue = "FullName") String sort
    ) {
        return staffInfoRepository.findAll(Sort.by(sort));
    }

    @PostMapping
    public StaffInfoDto createStaffInfo(@RequestBody StaffInfoDto data) {
        return staffInfoService.createStaffInfo(data);
    }
}