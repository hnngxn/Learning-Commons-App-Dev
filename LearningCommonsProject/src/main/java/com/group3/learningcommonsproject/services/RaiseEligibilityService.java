package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.RaiseEligibilityResponse;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RaiseEligibilityService {

    private static final int ELIGIBILITY_THRESHOLD = 125;

    private final StaffInfoRepository staffRepository;

    public RaiseEligibilityService(StaffInfoRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Transactional(readOnly = true)
    public List<RaiseEligibilityResponse> getEligibleStaff() {
        return staffRepository
                .findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(
                        ELIGIBILITY_THRESHOLD
                )
                .stream()
                .map(employee -> new RaiseEligibilityResponse(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getCurrentPoints(),
                        ELIGIBILITY_THRESHOLD,
                        true
                ))
                .toList();
    }
}