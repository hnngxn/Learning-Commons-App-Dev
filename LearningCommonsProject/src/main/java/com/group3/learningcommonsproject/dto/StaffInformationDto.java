package com.group3.learningcommonsproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffInformationDto {
    //standard staff/class information
    private Long id;
    private String fullName;
    private String email;
    private String position;
    private int points;
    private boolean isSupervisor;
    private boolean isReturning;
    private boolean active;


    //specifically for the info from the supervisor+staff join table
    private Set<Long> supervisorIds = new HashSet<>();
    private Set<String> supervisorNames = new HashSet<>();
}
