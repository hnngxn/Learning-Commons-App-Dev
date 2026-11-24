package com.group3.learningcommonsproject.dto;

import java.time.LocalDate;

public record EventLogDto(
        Long id,
        Long staffId,
        Long pointEventId,
        int pointsApplied,
        LocalDate eventDate,
        String comments
) {
}