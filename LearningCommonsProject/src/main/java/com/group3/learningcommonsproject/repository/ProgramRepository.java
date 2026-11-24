package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}