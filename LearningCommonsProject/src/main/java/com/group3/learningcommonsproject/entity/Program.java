package com.group3.learningcommonsproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Programs")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "programName", nullable = false)
    private String programName;

    @JsonIgnore
    @ManyToMany(mappedBy = "programs")
    private Set<StaffInfo> staffMembers = new HashSet<>();

    public Program() {
    }

    public Program(Long id, String programName, Set<StaffInfo> staffMembers) {
        this.id = id;
        this.programName = programName;
        this.staffMembers = staffMembers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Set<StaffInfo> getStaffMembers() {
        return staffMembers;
    }

    public void setStaffMembers(Set<StaffInfo> staffMembers) {
        this.staffMembers = staffMembers;
    }
}