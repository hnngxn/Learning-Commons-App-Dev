package com.group3.learningcommonsproject.repository;

import java.util.Optional;
import com.group3.learningcommonsproject.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    Optional<EventLog> findBySubmissionToken(String submissionToken);
}