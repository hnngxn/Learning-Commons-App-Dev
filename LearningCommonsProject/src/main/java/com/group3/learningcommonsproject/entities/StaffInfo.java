package com.group3.learningcommonsproject.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff_information")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false, name = "Full_Name")
    private String fullName;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "Position_1")
    private String position1;

    @Column(nullable = false, name = "Supervisor_1")
    private String supervisor1;

    @Column(nullable = false, name = "Current_Points")
    private int currentPoints;

    @ManyToMany
    @JoinTable(
            name = "staff_programs",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private java.util.List<Program> programs;
}
