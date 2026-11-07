package com.group3.learningcommonsproject.dto;

public record RaiseEligibilityResponse(
        Long staffId,
        String fullName,
        int currentPoints,
        int eligibilityThreshold,
        boolean eligible
) {
}