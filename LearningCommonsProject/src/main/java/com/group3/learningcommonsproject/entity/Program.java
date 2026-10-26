package com.group3.learningcommonsproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Programs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String programName;
}