package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.PointReversal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointReversalRepository
        extends JpaRepository<PointReversal, Long> {

    boolean existsByEventLog_Id(Long eventLogId);

    Optional<PointReversal> findByEventLog_Id(Long eventLogId);
}