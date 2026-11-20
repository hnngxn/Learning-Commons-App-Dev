package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.PointEventDto;
import com.group3.learningcommonsproject.service.PointEventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// JSON API. Validation happens in PointEventService, same as the HTML pages.
@RestController
@RequestMapping("/api/point-events")
public class PointEventController {

    private final PointEventService pointEventService;

    public PointEventController(PointEventService pointEventService) {
        this.pointEventService = pointEventService;
    }

    @GetMapping
    public List<PointEventDto> getAllPointEvents() {
        return pointEventService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PointEventDto createPointEvent(@RequestBody PointEventDto request) {
        return pointEventService.create(request);
    }

    @PutMapping("/{id}")
    public PointEventDto updatePointEvent(@PathVariable Long id, @RequestBody PointEventDto request) {
        return pointEventService.update(id, request);
    }
}
