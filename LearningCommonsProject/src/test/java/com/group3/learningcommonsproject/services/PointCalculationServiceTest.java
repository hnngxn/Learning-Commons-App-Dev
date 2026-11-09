package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.entity.PointEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class PointCalculationServiceTest {

    private final PointCalculationService service =
            new PointCalculationService();

    @ParameterizedTest
    @CsvSource({
            "Bonus, 1, 10, 1, 1",
            "Bonus, 1, 10, 10, 10",
            "Bonus, 5, 5, 5, 5",
            "Infraction, -20, -1, 1, -1",
            "Infraction, -20, -1, 20, -20",
            "Infraction, -20, -5, 5, -5",
            "Infraction, -20, -5, 20, -20"
    })
    void appliesCorrectSignedPoints(
            String type, int min, int max, int entered, int expected
    ) {
        PointEvent event = event(type, min, max);

        assertEquals(
                expected,
                service.calculatePointsApplied(event, entered)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "Bonus, 1, 10, 0",
            "Bonus, 1, 10, -1",
            "Bonus, 1, 10, 11",
            "Infraction, -20, -5, 4",
            "Infraction, -20, -5, 21",
            "Infraction, -20, -5, -10"
    })
    void rejectsInvalidInput(String type, int min, int max, int entered) {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.calculatePointsApplied(
                        event(type, min, max), entered
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    private PointEvent event(String type, int min, int max) {
        PointEvent event = new PointEvent();
        event.setEventType(type);
        event.setMinPoints(min);
        event.setMaxPoints(max);
        return event;
    }
}