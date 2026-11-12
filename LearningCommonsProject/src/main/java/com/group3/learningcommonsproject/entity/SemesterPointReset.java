package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "semester_point_resets",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_reset_semester",
                columnNames = {"semester_year", "semester_term"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class SemesterPointReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_year", nullable = false)
    private int semesterYear;

    @Column(name = "semester_term", nullable = false, length = 6)
    private String semesterTerm;

    @Column(name = "reset_at", nullable = false)
    private Instant resetAt;

    @PrePersist
    protected void setResetTime() {
        if (resetAt == null) {
            resetAt = Instant.now();
        }
    }
}