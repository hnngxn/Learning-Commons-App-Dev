package com.group3.learningcommonsproject.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Wrapper types (Integer, Boolean) instead of int/boolean so a missing JSON field arrives as
// null and PointEventService can answer with a clear 400 message. With primitives, Jackson 3
// (Spring Boot 4) rejects the whole request before our code runs.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PointEventDto {
    private Long id;
    private String eventName;
    private String eventType;
    private Integer minPoints;
    private Integer maxPoints;
    private Boolean active; // null on update = leave unchanged
}
