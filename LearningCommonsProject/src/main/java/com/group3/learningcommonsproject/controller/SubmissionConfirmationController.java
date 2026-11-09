package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.repository.EventLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SubmissionConfirmationController {

    private final EventLogRepository eventLogs;

    public SubmissionConfirmationController(EventLogRepository eventLogs) {
        this.eventLogs = eventLogs;
    }

    @GetMapping("/entries/{id}")
    @Transactional(readOnly = true)
    public String confirmation(
            @PathVariable("id") Long id,
            Model model
    ) {
        var entry = eventLogs.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entry not found."
                ));

        model.addAttribute("entry", entry);
        model.addAttribute("staffName", entry.getStaff().getFullName());
        model.addAttribute("staffId", entry.getStaff().getId());
        model.addAttribute("eventName", entry.getPointEvent().getEventName());
        model.addAttribute("eventType", entry.getPointEvent().getEventType());

        return "submission-confirmation";
    }
}