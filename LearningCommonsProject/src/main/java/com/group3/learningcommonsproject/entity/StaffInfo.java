package com.group3.learningcommonsproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Staff_Information")
public class StaffInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Full_Name", nullable = false)
    private String fullName;

    @Column(name = "Email", nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "staff_programs",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private Set<Program> programs = new HashSet<>();

    public StaffInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(Set<Program> programs) {
        this.programs = programs;
    }
}