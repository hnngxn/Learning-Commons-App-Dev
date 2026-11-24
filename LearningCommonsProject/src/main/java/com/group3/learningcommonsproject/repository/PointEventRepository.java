package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.PointEvent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PointEventRepository extends JpaRepository<PointEvent, Long>{
    java.util.List<PointEvent> findByActiveTrue(org.springframework.data.domain.Sort sort);
}
