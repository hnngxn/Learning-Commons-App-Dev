package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Point_Events")
@Getter @Setter @NoArgsConstructor
public class PointEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Event_Name")
    private String eventName;

    @Column(name = "Event_Type")
    private String eventType;

    @Column(name = "min_points")
    private int minPoints;

    @Column(name = "max_points")
    private int maxPoints;

    private boolean active = true;
}