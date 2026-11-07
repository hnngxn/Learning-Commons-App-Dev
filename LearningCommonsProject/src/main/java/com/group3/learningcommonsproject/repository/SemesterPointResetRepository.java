package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.SemesterPointReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterPointResetRepository
        extends JpaRepository<SemesterPointReset, Long> {

    boolean existsBySemesterYearAndSemesterTerm(
            int semesterYear,
            String semesterTerm
    );
}