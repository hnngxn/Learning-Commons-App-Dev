package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.PointReversalRequest;
import com.group3.learningcommonsproject.dto.PointReversalResponse;
import com.group3.learningcommonsproject.services.PointReversalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event-logs")
public class PointReversalController {

    private final PointReversalService service;

    public PointReversalController(PointReversalService service) {
        this.service = service;
    }

    @PostMapping("/{eventLogId}/reversal")
    @ResponseStatus(HttpStatus.CREATED)
    public PointReversalResponse reverse(
            @PathVariable("eventLogId") Long eventLogId,
            @RequestBody PointReversalRequest request
    ) {
        return service.reverse(
                eventLogId,
                request == null ? null : request.reason()
        );
    }
}