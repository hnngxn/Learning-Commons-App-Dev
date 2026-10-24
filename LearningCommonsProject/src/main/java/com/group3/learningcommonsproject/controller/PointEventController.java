package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.PointEventDto;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        for (PointEvent event : pointEventRepository.findAll()) {
            result.add(new PointEventDto(event.getId(), event.getEventName(), event.getEventType(), event.getMinPoints(), event.getMaxPoints()));
        }
        return result;   // hand the pile back
    }
}