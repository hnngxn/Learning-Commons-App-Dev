package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.PointEventDto;
import com.group3.learningcommonsproject.errorResponses.BadRequest;
import com.group3.learningcommonsproject.service.PointEventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Returns HTML pages (Thymeleaf templates), not JSON like PointEventController.
// Validation and saving live in PointEventService so both controllers follow the same rules.
@Controller
@RequestMapping("/admin/event-types")
public class PointEventPageController {

    private final PointEventService pointEventService;

    public PointEventPageController(PointEventService pointEventService) {
        this.pointEventService = pointEventService;
    }

    // Screen 12: list of all event types
    @GetMapping
    public String listEventTypes(Model model) {
        model.addAttribute("eventTypes", pointEventService.findAll());
        return "event-types";
    }

    // Screen 12a in Add mode: empty form, Bonus picked by default
    @GetMapping("/new")
    public String newEventTypeForm(Model model) {
        PointEventDto form = new PointEventDto();
        form.setEventType(PointEventService.BONUS);
        model.addAttribute("form", form);
        return "event-type-form";
    }

    // Screen 12a in Edit mode: form filled with the existing event type
    @GetMapping("/{id}/edit")
    public String editEventTypeForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", pointEventService.findById(id));
        return "event-type-form";
    }

    // One save for both Add and Edit: no id = new, has id = update
    @PostMapping("/save")
    public String saveEventType(@ModelAttribute("form") PointEventDto form, BindingResult bindingResult,
                                Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Min and max points must be whole numbers.");
            return "event-type-form";
        }

        PointEventDto saved;
        try {
            saved = form.getId() == null
                    ? pointEventService.create(form)
                    : pointEventService.update(form.getId(), form);
        } catch (BadRequest e) {
            model.addAttribute("error", e.getMessage());
            return "event-type-form";
        }

        redirectAttributes.addFlashAttribute("message", "Saved " + saved.getEventName() + ".");
        return "redirect:/admin/event-types";
    }
}
