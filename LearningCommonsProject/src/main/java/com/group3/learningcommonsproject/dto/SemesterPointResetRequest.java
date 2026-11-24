package com.group3.learningcommonsproject.dto;

import java.util.List;

public record SemesterPointResetRequest(
        Integer year,
        SemesterTerm term,
        List<EmployeeSelection> employees
) {

    public enum SemesterTerm {
        SPRING, SUMMER, FALL
    }

    public enum EmployeeStatus {
        NEW, RETURNING
    }

    public record EmployeeSelection(
            Long staffId,
            EmployeeStatus status
    ) {
    }
}