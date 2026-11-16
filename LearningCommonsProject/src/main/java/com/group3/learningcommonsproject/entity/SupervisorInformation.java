package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Supervisor_Information")
@Getter
@Setter
@NoArgsConstructor
public class SupervisorInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//FIXME: this may not be the correct ID setup
    private String supervisorName;
    private String supervisorEmail;
    private String supervisorPosition;
}
