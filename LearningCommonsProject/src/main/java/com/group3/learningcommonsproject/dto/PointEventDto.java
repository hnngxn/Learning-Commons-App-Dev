package com.group3.learningcommonsproject.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PointEventDto {
    private Long id;
    private String eventName ;
    private String eventType;
    private int minPoints;
    private int maxPoints;
}
