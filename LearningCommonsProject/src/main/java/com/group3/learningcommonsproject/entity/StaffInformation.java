package com.group3.learningcommonsproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "staff_information")
@Getter
@Setter
@NoArgsConstructor
public class StaffInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@column(name ="") allows us to use camelCase standard java names while still connecting to database column names
    @Column(name = "Full_Name")
    private String fullName;

    @Column(name = "Email")
    private String email;

    @Column(name = "Position_1")
    private String position;

    @Column(name = "Current_Points")
    private int points;

    @Column(name = "is_supervisor", nullable = false)
    private boolean isSupervisor = false;

    @Column(name = "is_returning", nullable = false)
    private boolean isReturning = false;

    @Column(name = "active")
    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "staff_supervisors",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "supervisor_id")
    )
    private Set<StaffInformation> supervisors = new HashSet<>();
}
