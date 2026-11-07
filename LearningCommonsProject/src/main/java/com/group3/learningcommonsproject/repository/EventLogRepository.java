package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.EventLog;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventLog e where e.id = :id")
    Optional<EventLog> findByIdForUpdate(@Param("id") Long id);
}