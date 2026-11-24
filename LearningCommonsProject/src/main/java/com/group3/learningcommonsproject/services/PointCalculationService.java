package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.entity.PointEvent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PointCalculationService {

    public int calculatePointsApplied(PointEvent event, Integer enteredPoints) {
        if (event == null || enteredPoints == null || enteredPoints <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Select an event and enter a positive point amount."
            );
        }

        String type = event.getEventType();
        int min = event.getMinPoints();
        int max = event.getMaxPoints();

        boolean infraction = "Infraction".equalsIgnoreCase(type);
        boolean bonus = "Bonus".equalsIgnoreCase(type);

        if (!bonus && !infraction) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Unsupported event type."
            );
        }

        if (min > max
                || (bonus && min <= 0)
                || (infraction && max >= 0)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The event has an invalid point range."
            );
        }

        int appliedPoints = infraction ? -enteredPoints : enteredPoints;

        if (appliedPoints < min || appliedPoints > max) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The point amount is outside the event's allowed range."
            );
        }

        return appliedPoints;
    }
}