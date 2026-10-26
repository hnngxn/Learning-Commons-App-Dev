package com.group3.learningcommonsproject.repositories;

import com.group3.learningcommonsproject.entities.StaffInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffInfoRepository extends JpaRepository<StaffInfo, Long> {
    List<StaffInfo> findByPrograms_Id(Long programId);
}
