package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.EventSubmissionRequest;
import com.group3.learningcommonsproject.repositories.StaffInfoRepository;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import com.group3.learningcommonsproject.services.EventSubmissionService;
import com.group3.learningcommonsproject.services.PointCalculationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Controller
public class EventEntryController {

    private final StaffInfoRepository staff;
    private final PointEventRepository pointEvents;
    private final PointCalculationService calculator;
    private final EventSubmissionService submissionService;

    public EventEntryController(
            StaffInfoRepository staff,
            PointEventRepository pointEvents,
            PointCalculationService calculator,
            EventSubmissionService submissionService
    ) {
        this.staff = staff;
        this.pointEvents = pointEvents;
        this.calculator = calculator;
        this.submissionService = submissionService;
    }

    public record SubmissionDraft(
            EventSubmissionRequest request,
            String token
    ) {
    }

    public record CompletedSubmission(
            String token,
            Long eventLogId
    ) {
    }

    @GetMapping("/entries/new")
    public String newEntry(
            @RequestParam(name = "staff", required = false) Long staffId,
            Model model
    ) {
        loadOptions(model);
        model.addAttribute("selectedStaffId", staffId);

        return "entry-form";
    }

    @PostMapping("/entries/review")
    public String review(
            @RequestParam("staffId") Long staffId,
            @RequestParam("pointEventId") Long pointEventId,
            @RequestParam("points") Integer points,
            @RequestParam("eventDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
            @RequestParam(name = "comments", defaultValue = "") String comments,
            @RequestParam("enteredBy") String enteredBy,
            HttpSession session,
            Model model
    ) {
        if (staffId <= 0 || pointEventId <= 0
                || enteredBy.isBlank() || enteredBy.length() > 255
                || comments.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide valid staff, event, entered-by name, and details."
            );
        }

        var employee = staff.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Staff member not found."
                ));

        var event = pointEvents.findById(pointEventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Point event not found."
                ));

        if ("Infraction".equalsIgnoreCase(event.getEventType())
                && comments.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Additional details are required for infractions."
            );
        }

        int applied = calculator.calculatePointsApplied(event, points);
        int projectedBalance;

        try {
            projectedBalance = Math.addExact(
                    employee.getCurrentPoints(), applied
            );
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The resulting balance is out of range."
            );
        }

        var request = new EventSubmissionRequest(
                staffId,
                pointEventId,
                points,
                eventDate,
                comments.strip(),
                enteredBy.strip()
        );

        var draft = new SubmissionDraft(
                request,
                UUID.randomUUID().toString()
        );

        synchronized (session) {
            session.setAttribute("submissionDraft", draft);
        }

        model.addAttribute("draft", draft);
        model.addAttribute("staffName", employee.getFullName());
        model.addAttribute("position", employee.getPosition1());
        model.addAttribute("supervisor", employee.getSupervisor1());
        model.addAttribute("eventName", event.getEventName());
        model.addAttribute("eventType", event.getEventType());
        model.addAttribute("pointsApplied", applied);
        model.addAttribute("currentBalance", employee.getCurrentPoints());
        model.addAttribute("projectedBalance", projectedBalance);

        return "entry-review";
    }

    @GetMapping("/entries/edit")
    public String edit(HttpSession session, Model model) {
        var draft = (SubmissionDraft)
                session.getAttribute("submissionDraft");

        if (draft == null) {
            return "redirect:/entries/new";
        }

        loadOptions(model);
        model.addAttribute("selectedStaffId", draft.request().staffId());
        model.addAttribute("form", draft.request());

        return "entry-form";
    }

    @PostMapping("/entries")
    public String submit(
            @RequestParam("token") String token,
            HttpSession session
    ) {
        synchronized (session) {
            var completed = (CompletedSubmission)
                    session.getAttribute("completedSubmission");

            if (completed != null
                    && Objects.equals(completed.token(), token)) {
                return "redirect:/entries/" + completed.eventLogId();
            }

            var draft = (SubmissionDraft)
                    session.getAttribute("submissionDraft");

            if (draft == null || !Objects.equals(draft.token(), token)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This review has expired. Please review your entry again."
                );
            }

            Long id = submissionService.submit(draft.request(), token);

            session.setAttribute(
                    "completedSubmission",
                    new CompletedSubmission(token, id)
            );
            session.removeAttribute("submissionDraft");

            return "redirect:/entries/" + id;
        }
    }

    private void loadOptions(Model model) {
        model.addAttribute(
                "staffMembers",
                staff.findAll(Sort.by("fullName", "id"))
        );
        model.addAttribute(
                "pointEvents",
                pointEvents.findAll(Sort.by("eventType", "eventName"))
        );
    }
}