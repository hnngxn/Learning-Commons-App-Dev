package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.EventLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    List<EventLog> findAllByOrderByEventDateDescIdDesc();
}