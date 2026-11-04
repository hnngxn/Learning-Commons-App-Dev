package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.PointEventDto;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/point-events")
public class PointEventController {

    private final PointEventRepository pointEventRepository;

    public PointEventController(PointEventRepository pointEventRepository) {
        this.pointEventRepository = pointEventRepository;
        }

    @GetMapping
    public List<PointEventDto> getAllPointEvents() {
        List<PointEventDto> result = new ArrayList<>();

        for (PointEvent event : pointEventRepository.findAll(Sort.by("eventType", "eventName"))) {
            result.add(new PointEventDto(event.getId(), event.getEventName(), event.getEventType(), event.getMinPoints(), event.getMaxPoints(), event.isActive()));
            }
        return result;
        }
    @PostMapping
    public PointEventDto createPointEvent(@RequestBody PointEventDto request) {
        PointEvent event = new PointEvent();
        event.setEventName(request.getEventName());
        event.setEventType(request.getEventType());
        event.setMinPoints(request.getMinPoints());
        event.setMaxPoints(request.getMaxPoints());

        PointEvent saved = pointEventRepository.save(event);
        return new PointEventDto(saved.getId(), saved.getEventName(), saved.getEventType(), saved.getMinPoints(), saved.getMaxPoints(), event.isActive());
        }
    @PutMapping("/{id}")
    public PointEventDto updatePointEvent(@PathVariable Long id, @RequestBody PointEventDto request) {
        PointEvent event = pointEventRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        event.setEventName(request.getEventName());
        event.setMinPoints(request.getMinPoints());
        event.setMaxPoints(request.getMaxPoints());
        event.setActive(request.isActive()); //deactivate instead of delete

        PointEvent saved = pointEventRepository.save(event);
        return new PointEventDto(saved.getId(), saved.getEventName(), saved.getEventType(), saved.getMinPoints(), saved.getMaxPoints(), event.isActive());
        }
    }


