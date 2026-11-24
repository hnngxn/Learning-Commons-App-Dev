package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "semester_point_reset_entries",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_reset_staff",
                columnNames = {"reset_id", "staff_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class SemesterPointResetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reset_id", nullable = false)
    private SemesterPointReset reset;

    @ManyToOne(optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffInfo staff;

    @Column(name = "employee_status", nullable = false, length = 9)
    private String employeeStatus;

    @Column(name = "previous_points", nullable = false)
    private int previousPoints;

    @Column(name = "starting_points", nullable = false)
    private int startingPoints;
}