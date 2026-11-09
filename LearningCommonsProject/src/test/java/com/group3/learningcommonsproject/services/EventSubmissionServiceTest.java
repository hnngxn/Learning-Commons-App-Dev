package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.EventSubmissionRequest;
import com.group3.learningcommonsproject.entities.StaffInfo;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.repositories.StaffInfoRepository;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventSubmissionServiceTest {

    private final StaffInfoRepository staff = mock(StaffInfoRepository.class);
    private final PointEventRepository events = mock(PointEventRepository.class);
    private final EventLogRepository logs = mock(EventLogRepository.class);

    private final EventSubmissionService service = new EventSubmissionService(
            staff, events, logs, new PointCalculationService()
    );

    private final String token = UUID.randomUUID().toString();

    private StaffInfo employee() {
        StaffInfo employee = new StaffInfo();
        employee.setId(1L);
        employee.setCurrentPoints(120);
        employee.setPosition1("Tutor");
        employee.setSupervisor1("Test Supervisor");
        when(staff.findByIdForUpdate(1L)).thenReturn(Optional.of(employee));
        when(logs.findBySubmissionToken(token)).thenReturn(Optional.empty());
        return employee;
    }

    private void event(String type, int min, int max) {
        PointEvent event = new PointEvent();
        event.setId(2L);
        event.setEventType(type);
        event.setMinPoints(min);
        event.setMaxPoints(max);
        when(events.findById(2L)).thenReturn(Optional.of(event));
    }

    private EventSubmissionRequest request(String comments) {
        return new EventSubmissionRequest(
                1L, 2L, 5, LocalDate.of(2026, 11, 8),
                comments, "Test Supervisor"
        );
    }

    @ParameterizedTest
    @CsvSource({
            "Bonus, 1, 10, 5, 125",
            "Infraction, -20, -1, -5, 115"
    })
    void savesSignedPointsAndBalance(
            String type, int min, int max, int applied, int balance
    ) {
        StaffInfo employee = employee();
        event(type, min, max);

        when(logs.saveAndFlush(any(EventLog.class))).thenAnswer(invocation -> {
            EventLog saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        assertEquals(Long.valueOf(10L), service.submit(request("Test details"), token));
        assertEquals(balance, employee.getCurrentPoints());

        var captor = org.mockito.ArgumentCaptor.forClass(EventLog.class);
        verify(logs).saveAndFlush(captor.capture());

        EventLog saved = captor.getValue();
        assertEquals(applied, saved.getPointsApplied());
        assertEquals(Integer.valueOf(balance), saved.getBalanceAfter());
        assertEquals(token, saved.getSubmissionToken());
        assertEquals("Tutor", saved.getPositionAtEntry());
        assertEquals("Test Supervisor", saved.getSupervisorAtEntry());
    }

    @Test
    void rejectsDuplicateWithoutChangingBalance() {
        StaffInfo employee = employee();
        when(logs.findBySubmissionToken(token))
                .thenReturn(Optional.of(new EventLog()));

        var error = assertThrows(ResponseStatusException.class,
                () -> service.submit(request("Test details"), token));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertEquals(120, employee.getCurrentPoints());
        verify(logs, never()).saveAndFlush(any(EventLog.class));
    }

    @Test
    void rejectsInfractionWithoutDetails() {
        StaffInfo employee = employee();
        event("Infraction", -20, -1);

        var error = assertThrows(ResponseStatusException.class,
                () -> service.submit(request(" "), token));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertEquals(120, employee.getCurrentPoints());
        verify(logs, never()).saveAndFlush(any(EventLog.class));
    }
}