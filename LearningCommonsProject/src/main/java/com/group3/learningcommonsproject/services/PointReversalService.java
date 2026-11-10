package com.group3.learningcommonsproject.services;


import com.group3.learningcommonsproject.dto.PointReversalResponse;
import com.group3.learningcommonsproject.entities.StaffInfo;
import com.group3.learningcommonsproject.entity.EventLog;
import com.group3.learningcommonsproject.entity.PointReversal;
import com.group3.learningcommonsproject.repositories.StaffInfoRepository;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import com.group3.learningcommonsproject.repository.PointReversalRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PointReversalService {

    private final EventLogRepository eventLogs;
    private final StaffInfoRepository staff;
    private final PointReversalRepository reversals;
    private final EntityManager entityManager;

    public PointReversalService(
            EventLogRepository eventLogs,
            StaffInfoRepository staff,
            PointReversalRepository reversals,
            EntityManager entityManager
    ) {
        this.eventLogs = eventLogs;
        this.staff = staff;
        this.reversals = reversals;
        this.entityManager = entityManager;
    }

    @Transactional
    public PointReversalResponse reverse(Long eventLogId, String reason) {
        if (eventLogId == null || eventLogId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event log ID must be positive."
            );
        }

        if (reason == null || reason.isBlank()
                || reason.strip().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A reversal reason of 1–255 characters is required."
            );
        }

        EventLog event = eventLogs.findByIdForUpdate(eventLogId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event log not found."
                ));

        if (reversals.existsByEventLog_Id(eventLogId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This entry has already been reversed."
            );
        }

        StaffInfo employee = staff.findByIdForUpdate(event.getStaff().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Staff member not found."
                ));

        // Reload the balance after acquiring the staff lock.
        entityManager.refresh(employee, LockModeType.PESSIMISTIC_WRITE);

        int adjustment;
        int updatedPoints;

        try {
            adjustment = Math.negateExact(event.getPointsApplied());
            updatedPoints = Math.addExact(
                    employee.getCurrentPoints(), adjustment
            );
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The point adjustment exceeds the supported range."
            );
        }

        employee.setCurrentPoints(updatedPoints);

        PointReversal reversal = new PointReversal();
        reversal.setEventLog(event);
        reversal.setPointsAdjustment(adjustment);
        reversal.setReason(reason.strip());

        PointReversal saved = reversals.saveAndFlush(reversal);

        return new PointReversalResponse(
                saved.getId(),
                event.getId(),
                employee.getId(),
                adjustment,
                updatedPoints,
                saved.getReason(),
                saved.getReversedAt()
        );
    }
}