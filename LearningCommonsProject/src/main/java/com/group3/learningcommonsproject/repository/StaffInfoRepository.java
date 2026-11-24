package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.StaffInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffInfoRepository extends JpaRepository<StaffInfo, Long> {

    List<StaffInfo> findByPrograms_Id(Long programId);
}