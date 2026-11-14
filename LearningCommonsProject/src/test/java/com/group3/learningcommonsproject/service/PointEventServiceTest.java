package com.group3.learningcommonsproject.service;

import com.group3.learningcommonsproject.dto.PointEventDto;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.errorResponses.BadRequest;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Plain unit tests: the repository is mocked, so these run without MySQL.
class PointEventServiceTest {

    private PointEventRepository repository;
    private PointEventService service;

    @BeforeEach
    void setUp() {
        repository = mock(PointEventRepository.class);
        when(repository.save(any(PointEvent.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new PointEventService(repository);
    }

    private static PointEventDto dto(String name, String type, Integer min, Integer max) {
        return new PointEventDto(null, name, type, min, max, true);
    }

    @Test
    void validBonusAndInfractionPass() {
        assertNull(PointEventService.validate(dto("Leadership", "Bonus", 5, 15), "Bonus"));
        assertNull(PointEventService.validate(dto("Timekeeping", "Infraction", -5, -1), "Infraction"));
    }

    @Test
    void blankNameFails() {
        assertEquals("Name is required.", PointEventService.validate(dto("  ", "Bonus", 1, 5), "Bonus"));
        assertEquals("Name is required.", PointEventService.validate(dto(null, "Bonus", 1, 5), "Bonus"));
    }

    @Test
    void unknownCategoryFails() {
        assertEquals("Pick Bonus or Infraction.", PointEventService.validate(dto("X", "Other", 1, 5), "Other"));
    }

    @Test
    void minGreaterThanMaxFails() {
        assertEquals("Min points can't be more than max points.",
                PointEventService.validate(dto("X", "Bonus", 10, 5), "Bonus"));
    }

    @Test
    void bonusMustBePositiveAndInfractionMustBeNegative() {
        assertEquals("Bonus points must be positive.",
                PointEventService.validate(dto("X", "Bonus", 0, 5), "Bonus"));
        assertNotNull(PointEventService.validate(dto("X", "Infraction", -5, 0), "Infraction"));
    }

    @Test
    void createRejectsInvalidRequestWithoutSaving() {
        BadRequest ex = assertThrows(BadRequest.class,
                () -> service.create(dto("Bad", "Infraction", 5, 10)));
        assertTrue(ex.getMessage().startsWith("Infraction points must be negative"));
        verify(repository, never()).save(any());
    }

    @Test
    void createTrimsNameAndKeepsCategory() {
        PointEventDto saved = service.create(dto("  Leadership  ", "Bonus", 5, 15));
        assertEquals("Leadership", saved.getEventName());
        assertEquals("Bonus", saved.getEventType());
    }

    // Regression test: the edit form sends the category back in a hidden field. Before this
    // change, validation trusted that field, so an Infraction could be saved with positive points.
    @Test
    void updateValidatesAgainstStoredCategoryNotRequest() {
        PointEvent stored = new PointEvent();
        stored.setId(1L);
        stored.setEventName("Timekeeping");
        stored.setEventType("Infraction");
        stored.setMinPoints(-5);
        stored.setMaxPoints(-1);
        when(repository.findById(1L)).thenReturn(Optional.of(stored));

        PointEventDto tampered = dto("Timekeeping", "Bonus", 5, 10);
        assertThrows(BadRequest.class, () -> service.update(1L, tampered));
        assertEquals(-1, stored.getMaxPoints());
        verify(repository, never()).save(any());
    }

    @Test
    void updateCanDeactivate() {
        PointEvent stored = new PointEvent();
        stored.setId(2L);
        stored.setEventType("Bonus");
        when(repository.findById(2L)).thenReturn(Optional.of(stored));

        PointEventDto request = new PointEventDto(2L, "Leadership", "Bonus", 5, 15, false);
        assertFalse(service.update(2L, request).getActive());
    }

    @Test
    void updateWithoutActiveLeavesItUnchanged() {
        PointEvent stored = new PointEvent();
        stored.setId(3L);
        stored.setEventType("Bonus");
        stored.setActive(false);
        when(repository.findById(3L)).thenReturn(Optional.of(stored));

        PointEventDto request = new PointEventDto(3L, "Leadership", "Bonus", 5, 15, null);
        assertFalse(service.update(3L, request).getActive());
    }

    @Test
    void missingPointsFails() {
        assertEquals("Min and max points are required.",
                PointEventService.validate(dto("X", "Bonus", null, 5), "Bonus"));
    }

    @Test
    void updateMissingIdThrowsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, dto("X", "Bonus", 1, 2)));
    }
}
