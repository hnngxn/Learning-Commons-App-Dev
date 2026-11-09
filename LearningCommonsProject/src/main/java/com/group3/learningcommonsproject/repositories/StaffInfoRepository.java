package com.group3.learningcommonsproject.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.group3.learningcommonsproject.entities.StaffInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StaffInfoRepository extends JpaRepository<StaffInfo, Long> {
    List<StaffInfo> findByPrograms_Id(Long programId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StaffInfo s where s.id = :id")
    Optional<StaffInfo> findByIdForUpdate(@Param("id") Long id);
}
