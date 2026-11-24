package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.entity.PointEvent;
import org.junit.jupiter.api.Test;
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
            "Infraction, -20, -5, 20, -20",
            "bonus, 1, 10, 5, 5",
            "INFRACTION, -20, -1, 5, -5",
            "Bonus, 1, 2147483647, 2147483647, 2147483647",
            "Infraction, -2147483648, -1, 2147483647, -2147483647"
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

    @Test
    void rejectsMissingEvent() {
        var error = assertThrows(ResponseStatusException.class,
                () -> service.calculatePointsApplied(null, 5));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void rejectsMissingPointAmount() {
        var error = assertThrows(ResponseStatusException.class,
                () -> service.calculatePointsApplied(event("Bonus", 1, 10), null));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({
            ", 1, 10", "'', 1, 10", "Other, 1, 10",
            "Bonus, 10, 1", "Bonus, 0, 10", "Bonus, -1, 10",
            "Infraction, -1, -20", "Infraction, -20, 0", "Infraction, -20, 1"
    })
    void rejectsInvalidEventConfiguration(String type, int min, int max) {
        var error = assertThrows(ResponseStatusException.class,
                () -> service.calculatePointsApplied(event(type, min, max), 5));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    private PointEvent event(String type, int min, int max) {
        PointEvent event = new PointEvent();
        event.setEventType(type);
        event.setMinPoints(min);
        event.setMaxPoints(max);
        return event;
    }
}
