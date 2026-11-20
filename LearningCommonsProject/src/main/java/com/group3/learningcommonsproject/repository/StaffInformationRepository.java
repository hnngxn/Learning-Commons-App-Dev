package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.StaffInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffInformationRepository extends JpaRepository<StaffInformation, Long> {

    // Fetches all staff AND their assigned supervisors in a single query
    @Query("SELECT DISTINCT s FROM StaffInformation s LEFT JOIN FETCH s.supervisors ORDER BY s.fullName ASC")
    List<StaffInformation> findAllWithSupervisors();

    // Fetches only active supervisors (for populating dropdowns)
    List<StaffInformation> findByIsSupervisorTrueAndActiveTrueOrderByFullNameAsc();
}
