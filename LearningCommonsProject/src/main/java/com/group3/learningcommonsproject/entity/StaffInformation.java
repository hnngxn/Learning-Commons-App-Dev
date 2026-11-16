package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Staff_Information")
@Getter
@Setter
@NoArgsConstructor
public class StaffInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//FIXME: this may not be the correct ID setup
    private String fullName;
    private String email;
    private String position;
    private String supervisorName;//FIXME: I'm assuming this is the supervisor's name
    //private Long supervisorId; //Uncomment this line if you want to use supervisorId instead of supervisorName
    private int points;
}
