package com.group3.learningcommonsproject.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EventLogDto {
    private Long id;
    private Long staffId;
    private Long pointEventId;
    private int pointsApplied;
    private LocalDate eventDate;
    private String comments;
}