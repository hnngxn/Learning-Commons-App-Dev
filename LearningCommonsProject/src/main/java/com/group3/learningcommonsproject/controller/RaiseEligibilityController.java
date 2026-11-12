package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.RaiseEligibilityResponse;
import com.group3.learningcommonsproject.services.RaiseEligibilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/raise-eligibility")
public class RaiseEligibilityController {

    private final RaiseEligibilityService service;

    public RaiseEligibilityController(RaiseEligibilityService service) {
        this.service = service;
    }

    @GetMapping
    public List<RaiseEligibilityResponse> getEligibleStaff() {
        return service.getEligibleStaff();
    }
}