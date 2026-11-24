package com.group3.learningcommonsproject.dto;

import java.time.Instant;
import java.util.List;

public record SemesterPointResetResponse(
        Long resetId,
        int year,
        String term,
        Instant resetAt,
        int employeesUpdated,
        List<EmployeeResult> employees
) {
    public record EmployeeResult(
            Long staffId,
            String status,
            int previousPoints,
            int startingPoints
    ) {
    }
}