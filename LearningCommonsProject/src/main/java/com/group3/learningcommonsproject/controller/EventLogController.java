package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.EventLogDto;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/event-logs")
public class EventLogController {

    private final EventLogRepository repository;

    public EventLogController(EventLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EventLogDto> getAllEventLogs() {
        return repository.findAll().stream().map(log -> new EventLogDto(
                log.getId(),
                log.getStaff().getId(),
                log.getPointEvent().getId(),
                log.getPointsApplied(),
                log.getEventDate(),
                log.getComments()
        )).collect(Collectors.toList());
    }
}