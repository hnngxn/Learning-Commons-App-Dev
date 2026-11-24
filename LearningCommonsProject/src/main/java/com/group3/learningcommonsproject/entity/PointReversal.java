package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "point_reversals")
@Getter
@Setter
@NoArgsConstructor
public class PointReversal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "event_log_id",
            nullable = false,
            unique = true
    )
    private EventLog eventLog;

    @Column(name = "points_adjustment", nullable = false)
    private int pointsAdjustment;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "reversed_at", nullable = false)
    private Instant reversedAt;

    @PrePersist
    protected void setReversalTime() {
        if (reversedAt == null) {
            reversedAt = Instant.now();
        }
    }
}