package com.group3.learningcommonsproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// One row on the eligibility dashboard (fields match staff_information)
@Getter
@AllArgsConstructor
public class DashboardRowDto {
    private Long id;
    private String fullName;
    private String position1;
    private String supervisor1;
    private String position2;
    private String supervisor2;
    private int currentPoints;

    // Status levels from the Learning Commons point system doc
    public String getStatus() {
        if (currentPoints <= 0) return "Termination";
        if (currentPoints <= 40) return "Probation";
        if (currentPoints < 125) return "Good standing";
        return "Raise eligible";
    }

    // Used for the badge color in the CSS
    public String getStatusKey() {
        if (currentPoints <= 0) return "termination";
        if (currentPoints <= 40) return "probation";
        if (currentPoints < 125) return "good";
        return "raise";
    }

    public String getPositions() {
        return hasSecondPosition() ? position1 + ", " + position2 : position1;
    }

    public String getSupervisors() {
        return hasSecondPosition() ? supervisor1 + ", " + supervisor2 : supervisor1;
    }

    private boolean hasSecondPosition() {
        return position2 != null && !position2.isBlank();
    }
}