package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.EventLogDto;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-logs")
public class EventLogController {

    private final EventLogRepository repository;

    public EventLogController(EventLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EventLogDto> getAllEventLogs() {
        return repository.findAll().stream()
                .map(log -> new EventLogDto(
                        log.getId(),
                        log.getStaff() != null ? log.getStaff().getId() : null,
                        log.getPointEvent() != null ? log.getPointEvent().getId() : null,
                        log.getPointsApplied(),
                        log.getEventDate(),
                        log.getComments()
                ))
                .toList();
    }
}