package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.StaffInfo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StaffInfoRepository extends JpaRepository<StaffInfo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StaffInfo s where s.id = :id")
    Optional<StaffInfo> findByIdForUpdate(@Param("id") Long id);
}