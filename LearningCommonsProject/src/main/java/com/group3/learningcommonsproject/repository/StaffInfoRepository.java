package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.StaffInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffInfoRepository extends JpaRepository<StaffInfo, Long> {
}
