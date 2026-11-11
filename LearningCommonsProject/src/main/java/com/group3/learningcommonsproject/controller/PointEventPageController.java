package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.PointEventDto;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.errorResponses.ResourceNotFoundException;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Returns HTML pages (Thymeleaf templates), not JSON like PointEventController
@Controller
@RequestMapping("/admin/event-types")
public class PointEventPageController {

    private final PointEventRepository pointEventRepository;

    public PointEventPageController(PointEventRepository pointEventRepository) {
        this.pointEventRepository = pointEventRepository;
    }

    // Screen 12: list of all event types
    @GetMapping
    public String listEventTypes(Model model) {
        model.addAttribute("eventTypes", pointEventRepository.findAll(Sort.by("eventType", "eventName")));
        return "event-types";
    }

    // Screen 12a in Add mode: empty form, Bonus picked by default
    @GetMapping("/new")
    public String newEventTypeForm(Model model) {
        PointEventDto form = new PointEventDto();
        form.setEventType("Bonus");
        model.addAttribute("form", form);
        return "event-type-form";
    }

    // Screen 12a in Edit mode: form filled with the existing event type
    @GetMapping("/{id}/edit")
    public String editEventTypeForm(@PathVariable Long id, Model model) {
        PointEvent event = pointEventRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        model.addAttribute("form", new PointEventDto(event.getId(), event.getEventName(), event.getEventType(),
                event.getMinPoints(), event.getMaxPoints(), event.isActive()));
        return "event-type-form";
    }

    // One save for both Add and Edit: no id = new, has id = update
    @PostMapping("/save")
    public String saveEventType(@ModelAttribute("form") PointEventDto form, BindingResult bindingResult,
                                Model model, RedirectAttributes redirectAttributes) {
        String error = bindingResult.hasErrors() ? "Min and max points must be whole numbers." : validate(form);
        if (error != null) {
            model.addAttribute("error", error);
            return "event-type-form";
        }

        PointEvent event;
        if (form.getId() == null) {
            event = new PointEvent();
            event.setEventType(form.getEventType()); // category is only set when created, never changed
        } else {
            event = pointEventRepository.findById(form.getId()).orElseThrow(ResourceNotFoundException::new);
            event.setActive(form.isActive()); // deactivate instead of delete
        }
        event.setEventName(form.getEventName().trim());
        event.setMinPoints(form.getMinPoints());
        event.setMaxPoints(form.getMaxPoints());
        pointEventRepository.save(event);

        redirectAttributes.addFlashAttribute("message", "Saved " + event.getEventName() + ".");
        return "redirect:/admin/event-types";
    }

    // Returns an error message, or null if the form is valid
    private String validate(PointEventDto form) {
        if (form.getEventName() == null || form.getEventName().isBlank()) {
            return "Name is required.";
        }
        if (!"Bonus".equals(form.getEventType()) && !"Infraction".equals(form.getEventType())) {
            return "Pick Bonus or Infraction.";
        }
        if (form.getMinPoints() > form.getMaxPoints()) {
            return "Min points can't be more than max points.";
        }
        if ("Bonus".equals(form.getEventType()) && form.getMinPoints() < 1) {
            return "Bonus points must be positive.";
        }
        if ("Infraction".equals(form.getEventType()) && form.getMaxPoints() > -1) {
            return "Infraction points must be negative, for example -20 to -1.";
        }
        return null;
    }
}