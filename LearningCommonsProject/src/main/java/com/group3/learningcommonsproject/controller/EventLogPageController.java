package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.service.EventLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventLogPageController {

    private final EventLogService eventLogService;

    public EventLogPageController(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    @GetMapping("/history")
    public String showEventHistory(Model model) {
        model.addAttribute("eventLogs", eventLogService.getAllEventLogs());
        return "event-history";
    }
}