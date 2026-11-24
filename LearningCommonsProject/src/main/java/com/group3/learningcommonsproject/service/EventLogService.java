package com.group3.learningcommonsproject.service;

import com.group3.learningcommonsproject.dto.EventLogDto;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EventLogService {

    private final EventLogRepository eventLogRepository;

    public EventLogService(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    public List<EventLogDto> getAllEventLogs() {
        return eventLogRepository.findAllByOrderByEventDateDescIdDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private EventLogDto toDto(EventLog log) {
        Long staffId = log.getStaff() != null ? log.getStaff().getId() : null;
        Long pointEventId = log.getPointEvent() != null ? log.getPointEvent().getId() : null;
        return new EventLogDto(
                log.getId(),
                staffId,
                pointEventId,
                log.getPointsApplied(),
                log.getEventDate(),
                log.getComments()
        );
    }
}