package com.group3.learningcommonsproject.service;

import com.group3.learningcommonsproject.dto.PointEventDto;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.errorResponses.BadRequest;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Business rules for point event types live here so the HTML pages and the REST API
// can't drift apart. Controllers only translate HTTP in and out.
@Service
public class PointEventService {

    public static final String BONUS = "Bonus";
    public static final String INFRACTION = "Infraction";

    private final PointEventRepository pointEventRepository;

    public PointEventService(PointEventRepository pointEventRepository) {
        this.pointEventRepository = pointEventRepository;
    }

    public List<PointEventDto> findAll() {
        return pointEventRepository.findAll(Sort.by("eventType", "eventName"))
                .stream()
                .map(PointEventService::toDto)
                .toList();
    }

    public PointEventDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    // Category is only set when created, never changed afterwards
    @Transactional
    public PointEventDto create(PointEventDto request) {
        throwIfInvalid(request, request.getEventType());

        PointEvent event = new PointEvent();
        event.setEventType(request.getEventType());
        applyEditableFields(event, request);
        return toDto(pointEventRepository.save(event));
    }

    // Validates against the stored category, not the one in the request, so a tampered
    // form or API call can't save an Infraction with positive points.
    @Transactional
    public PointEventDto update(Long id, PointEventDto request) {
        PointEvent event = getOrThrow(id);
        throwIfInvalid(request, event.getEventType());

        applyEditableFields(event, request);
        if (request.getActive() != null) {
            event.setActive(request.getActive()); // deactivate instead of delete
        }
        return toDto(pointEventRepository.save(event));
    }

    // Returns an error message, or null if the request is valid.
    // eventType is passed separately so update() can check against the stored category.
    public static String validate(PointEventDto request, String eventType) {
        if (request.getEventName() == null || request.getEventName().isBlank()) {
            return "Name is required.";
        }
        if (!BONUS.equals(eventType) && !INFRACTION.equals(eventType)) {
            return "Pick Bonus or Infraction.";
        }
        if (request.getMinPoints() == null || request.getMaxPoints() == null) {
            return "Min and max points are required.";
        }
        if (request.getMinPoints() > request.getMaxPoints()) {
            return "Min points can't be more than max points.";
        }
        if (BONUS.equals(eventType) && request.getMinPoints() < 1) {
            return "Bonus points must be positive.";
        }
        if (INFRACTION.equals(eventType) && request.getMaxPoints() > -1) {
            return "Infraction points must be negative, for example -20 to -1.";
        }
        return null;
    }

    private static void throwIfInvalid(PointEventDto request, String eventType) {
        String error = validate(request, eventType);
        if (error != null) {
            throw new BadRequest(error);
        }
    }

    private static void applyEditableFields(PointEvent event, PointEventDto request) {
        event.setEventName(request.getEventName().trim());
        event.setMinPoints(request.getMinPoints());
        event.setMaxPoints(request.getMaxPoints());
    }

    private PointEvent getOrThrow(Long id) {
        return pointEventRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    private static PointEventDto toDto(PointEvent event) {
        return new PointEventDto(event.getId(), event.getEventName(), event.getEventType(),
                event.getMinPoints(), event.getMaxPoints(), event.isActive());
    }
}
