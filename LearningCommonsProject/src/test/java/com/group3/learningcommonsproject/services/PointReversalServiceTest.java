package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.entities.StaffInfo;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.entity.PointReversal;
import com.group3.learningcommonsproject.repositories.StaffInfoRepository;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import com.group3.learningcommonsproject.repository.PointReversalRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PointReversalServiceTest {

    private final EventLogRepository events =
            mock(EventLogRepository.class);
    private final StaffInfoRepository staff =
            mock(StaffInfoRepository.class);
    private final PointReversalRepository reversals =
            mock(PointReversalRepository.class);
    private final EntityManager manager =
            mock(EntityManager.class);

    private final PointReversalService service =
            new PointReversalService(events, staff, reversals, manager);

    @ParameterizedTest
    @CsvSource({
            "10, 90",
            "-10, 110"
    })
    void reversesBonusAndInfraction(int originalPoints, int expectedBalance) {
        StaffInfo employee = new StaffInfo();
        employee.setId(2L);
        employee.setCurrentPoints(100);

        EventLog event = new EventLog();
        event.setId(1L);
        event.setStaff(employee);
        event.setPointsApplied(originalPoints);

        when(events.findByIdForUpdate(1L))
                .thenReturn(Optional.of(event));
        when(staff.findByIdForUpdate(2L))
                .thenReturn(Optional.of(employee));
        when(reversals.saveAndFlush(any(PointReversal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.reverse(1L, "Incorrect entry");

        assertEquals(expectedBalance, employee.getCurrentPoints());
        assertEquals(expectedBalance, response.currentPoints());
        assertEquals(-originalPoints, response.pointsAdjustment());
        assertEquals(originalPoints, event.getPointsApplied());

        verify(reversals).saveAndFlush(any(PointReversal.class));
        verify(events, never()).delete(any(EventLog.class));
    }

    @Test
    void rejectsDuplicateReversal() {
        EventLog event = new EventLog();
        event.setId(1L);

        when(events.findByIdForUpdate(1L))
                .thenReturn(Optional.of(event));
        when(reversals.existsByEventLog_Id(1L)).thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.reverse(1L, "Incorrect entry")
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verifyNoInteractions(staff, manager);
        verify(reversals, never())
                .saveAndFlush(any(PointReversal.class));
    }
}