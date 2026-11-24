package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.RaiseEligibilityResponse;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RaiseEligibilityServiceTest {
    private final StaffInfoRepository repository = mock(StaffInfoRepository.class);
    private final RaiseEligibilityService service = new RaiseEligibilityService(repository);

    @Test
    void mapsEligibleStaffWithoutLosingRepositoryOrder() {
        StaffInfo highest = employee(3L, "Above", 126);
        StaffInfo threshold = employee(2L, "At threshold", 125);
        when(repository.findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125))
                .thenReturn(List.of(highest, threshold));

        assertThat(service.getEligibleStaff()).containsExactly(
                new RaiseEligibilityResponse(3L, "Above", 126, 125, true),
                new RaiseEligibilityResponse(2L, "At threshold", 125, 125, true));
        verify(repository).findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void returnsEmptyWhenRepositoryHasNoEligibleStaff() {
        when(repository.findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125))
                .thenReturn(List.of());
        assertThat(service.getEligibleStaff()).isEmpty();
    }

    private StaffInfo employee(Long id, String name, int points) {
        StaffInfo employee = new StaffInfo();
        employee.setId(id);
        employee.setFullName(name);
        employee.setCurrentPoints(points);
        return employee;
    }
}
