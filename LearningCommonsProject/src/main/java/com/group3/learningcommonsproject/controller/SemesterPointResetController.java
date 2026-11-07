package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.SemesterPointResetRequest;
import com.group3.learningcommonsproject.dto.SemesterPointResetResponse;
import com.group3.learningcommonsproject.services.SemesterPointResetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/semester-point-resets")
public class SemesterPointResetController {

    private final SemesterPointResetService service;

    public SemesterPointResetController(SemesterPointResetService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SemesterPointResetResponse reset(
            @RequestBody SemesterPointResetRequest request
    ) {
        return service.reset(request);
    }
}