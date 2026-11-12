package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.DashboardRowDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//totals and status for every staff member
@Controller
@RequestMapping("/admin/dashboard")
public class EligibilityDashboardController {

    private static final List<String> STATUSES =
            List.of("Raise eligible", "Good standing", "Probation", "Termination");

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "") String name,
                            @RequestParam(defaultValue = "") String position,
                            @RequestParam(defaultValue = "") String status,
                            Model model) {
        List<DashboardRowDto> allStaff = sampleStaff();

        List<DashboardRowDto> rows = allStaff.stream()
                .filter(r -> name.isBlank() || r.getFullName().toLowerCase().contains(name.trim().toLowerCase()))
                .filter(r -> position.isBlank() || position.equals(r.getPosition1()) || position.equals(r.getPosition2()))
                .filter(r -> status.isBlank() || status.equals(r.getStatus()))
                .sorted(Comparator.comparingInt(DashboardRowDto::getCurrentPoints).reversed())
                .toList();

        List<String> positions = allStaff.stream()
                .flatMap(r -> Stream.of(r.getPosition1(), r.getPosition2()))
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .sorted()
                .toList();

        Map<String, Long> counts = allStaff.stream()
                .collect(Collectors.groupingBy(DashboardRowDto::getStatus, Collectors.counting()));

        model.addAttribute("rows", rows);
        model.addAttribute("totalStaff", allStaff.size());
        model.addAttribute("counts", counts);
        model.addAttribute("statuses", STATUSES);
        model.addAttribute("positions", positions);
        model.addAttribute("name", name);
        model.addAttribute("position", position);
        model.addAttribute("status", status);
        return "eligibility-dashboard";
    }

    // Sample data until staff_information is on main
    private List<DashboardRowDto> sampleStaff() {
        return List.of(
                new DashboardRowDto(1L, "Jordan Wells", "Tutor", "A. Ramirez", null, null, 115),
                new DashboardRowDto(2L, "Devon Clark", "SI Leader", "M. Chen", null, null, 135),
                new DashboardRowDto(3L, "Sam Rivera", "Tutor", "A. Ramirez", "Front Desk", "L. Brooks", 103),
                new DashboardRowDto(4L, "Ari Lopez", "Tutor", "A. Ramirez", null, null, 38),
                new DashboardRowDto(5L, "Robin Fields", "Front Desk", "L. Brooks", null, null, 68),
                new DashboardRowDto(6L, "Jordan Price", "Academic Coach", "M. Chen", null, null, 72),
                new DashboardRowDto(7L, "Casey Nguyen", "Writing Consultant", "L. Brooks", "Tutor", "A. Ramirez", 128),
                new DashboardRowDto(8L, "Taylor Brooks", "SI Leader", "M. Chen", null, null, 0),
                new DashboardRowDto(9L, "Morgan Lee", "Front Desk", "L. Brooks", null, null, 22),
                new DashboardRowDto(10L, "Avery Patel", "Academic Coach", "M. Chen", "Front Desk", "L. Brooks", 90)
        );
    }
}