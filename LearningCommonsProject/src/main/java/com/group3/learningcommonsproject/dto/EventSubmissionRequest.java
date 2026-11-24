package com.group3.learningcommonsproject.dto;

import java.time.LocalDate;

public record EventSubmissionRequest(
        Long staffId,
        Long pointEventId,
        Integer points,
        LocalDate eventDate,
        String comments,
        String enteredBy
) {
}