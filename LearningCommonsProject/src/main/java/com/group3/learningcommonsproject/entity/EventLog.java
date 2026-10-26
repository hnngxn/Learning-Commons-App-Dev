package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "Event_Log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffInfo staff;

    @ManyToOne
    @JoinColumn(name = "point_event_id", nullable = false)
    private PointEvent pointEvent;

    private int pointsApplied;
    private LocalDate eventDate;
    private String comments;
}