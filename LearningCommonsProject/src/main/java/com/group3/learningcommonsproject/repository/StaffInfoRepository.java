package com.group3.learningcommonsproject.repository;

import java.util.List;
import com.group3.learningcommonsproject.entity.StaffInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffInfoRepository extends JpaRepository<StaffInfo, Long> {
    List<StaffInfo> findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(
            Integer minimumPoints
    );
}
