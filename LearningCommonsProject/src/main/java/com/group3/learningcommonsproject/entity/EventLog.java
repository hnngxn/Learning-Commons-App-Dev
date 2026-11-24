package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.group3.learningcommonsproject.entities.StaffInfo;
import com.group3.learningcommonsproject.entity.PointEvent;

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

    @Column(name = "points_applied")
    private int pointsApplied;

    @Column(name = "event_date")
    private LocalDate eventDate;
    private String comments;
}