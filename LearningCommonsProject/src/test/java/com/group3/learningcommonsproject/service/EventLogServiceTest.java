package com.group3.learningcommonsproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.group3.learningcommonsproject.dto.EventLogDto;
import com.group3.learningcommonsproject.entities.StaffInfo;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventLogServiceTest {

    @Mock
    private EventLogRepository eventLogRepository;

    @InjectMocks
    private EventLogService eventLogService;

    @Test
    void getAllEventLogsMapsEntityToDto() {
        StaffInfo staff = new StaffInfo();
        staff.setId(4L);

        PointEvent event = new PointEvent();
        event.setId(2L);

        LocalDate date = LocalDate.of(2026, 11, 20);
        EventLog log = new EventLog(1L, staff, event, 15, date, "Monthly check-in");

        when(eventLogRepository.findAllByOrderByEventDateDescIdDesc()).thenReturn(List.of(log));

        List<EventLogDto> result = eventLogService.getAllEventLogs();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(4L, result.get(0).staffId());
        assertEquals(2L, result.get(0).pointEventId());
        assertEquals(15, result.get(0).pointsApplied());
        assertEquals(date, result.get(0).eventDate());
        assertEquals("Monthly check-in", result.get(0).comments());
    }
}