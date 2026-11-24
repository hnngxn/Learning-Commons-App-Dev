package com.group3.learningcommonsproject.dto;

import java.time.Instant;

public record PointReversalResponse(
        Long reversalId,
        Long eventLogId,
        Long staffId,
        int pointsAdjustment,
        int currentPoints,
        String reason,
        Instant reversedAt
) {
}