package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.entity.PointReversal;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import com.group3.learningcommonsproject.repository.PointReversalRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Stream;

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
    void rejectsMissingBalanceWithoutSavingAReversal() {
        StaffInfo employee = new StaffInfo();
        employee.setId(2L);
        employee.setCurrentPoints(null);
        EventLog event = new EventLog();
        event.setId(1L);
        event.setStaff(employee);
        event.setPointsApplied(5);
        when(events.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        when(staff.findByIdForUpdate(2L)).thenReturn(Optional.of(employee));

        var error = assertThrows(ResponseStatusException.class,
                () -> service.reverse(1L, "Correction"));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertNull(employee.getCurrentPoints());
        verify(reversals, never()).saveAndFlush(any(PointReversal.class));
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

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void rejectsInvalidEventIdBeforeAccessingPersistence(Long id) {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.reverse(id, "Incorrect entry"));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(events, staff, reversals, manager);
    }

    @ParameterizedTest
    @MethodSource("invalidReasons")
    void rejectsInvalidReasonBeforeAccessingPersistence(String reason) {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.reverse(1L, reason));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(events, staff, reversals, manager);
    }

    static Stream<String> invalidReasons() {
        return Stream.of(null, "", " \t\n", "x".repeat(256));
    }

    @Test
    void missingEventReturns404WithoutChangingStaffOrAuditHistory() {
        when(events.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.reverse(1L, "Incorrect entry"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(staff, reversals, manager);
    }

    @Test
    void missingStaffReturns404WithoutSavingAReversal() {
        StaffInfo employee = new StaffInfo();
        employee.setId(2L);
        EventLog event = new EventLog();
        event.setStaff(employee);
        when(events.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        when(staff.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.reverse(1L, "Incorrect entry"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(manager);
        verify(reversals, never()).saveAndFlush(any(PointReversal.class));
    }

    @ParameterizedTest
    @CsvSource({
            "-2147483648, 100",
            "-1, 2147483647",
            "1, -2147483648"
    })
    void rejectsOverflowWithoutChangingBalanceOrSavingAudit(int originalPoints, int balance) {
        StaffInfo employee = prepareEvent(originalPoints, balance);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.reverse(1L, "Incorrect entry"));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertEquals(balance, employee.getCurrentPoints());
        verify(reversals, never()).saveAndFlush(any(PointReversal.class));
    }

    @Test
    void calculatesFromRefreshedBalanceInsteadOfPreviouslyLoadedStaff() {
        StaffInfo employee = prepareEvent(10, 100);
        doAnswer(invocation -> {
            employee.setCurrentPoints(150);
            return null;
        }).when(manager).refresh(employee, LockModeType.PESSIMISTIC_WRITE);
        when(reversals.saveAndFlush(any(PointReversal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.reverse(1L, "Incorrect entry");

        assertEquals(140, response.currentPoints());
        assertEquals(140, employee.getCurrentPoints());
        verify(manager).refresh(employee, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    void acceptsMaximumReasonLengthAndStripsSurroundingWhitespace() {
        prepareEvent(10, 100);
        when(reversals.saveAndFlush(any(PointReversal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        String reason = "x".repeat(255);

        var response = service.reverse(1L, "  " + reason + "  ");

        assertEquals(reason, response.reason());
        var saved = org.mockito.ArgumentCaptor.forClass(PointReversal.class);
        verify(reversals).saveAndFlush(saved.capture());
        assertEquals(reason, saved.getValue().getReason());
    }

    private StaffInfo prepareEvent(int originalPoints, int balance) {
        StaffInfo employee = new StaffInfo();
        employee.setId(2L);
        employee.setCurrentPoints(balance);
        EventLog event = new EventLog();
        event.setId(1L);
        event.setStaff(employee);
        event.setPointsApplied(originalPoints);
        when(events.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        when(staff.findByIdForUpdate(2L)).thenReturn(Optional.of(employee));
        return employee;
    }
}
