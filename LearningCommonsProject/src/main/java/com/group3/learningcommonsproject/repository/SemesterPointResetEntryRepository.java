package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.SemesterPointResetEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterPointResetEntryRepository
        extends JpaRepository<SemesterPointResetEntry, Long> {

    List<SemesterPointResetEntry> findByReset_IdOrderByStaff_Id(Long resetId);
}