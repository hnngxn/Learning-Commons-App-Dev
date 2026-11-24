package com.group3.learningcommonsproject.repository;

import com.group3.learningcommonsproject.entity.StaffInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RaiseEligibilityRepositoryTest {
    @Autowired
    private StaffInfoRepository repository;

    @Test
    void includesThresholdAndSortsByPointsThenId() {
        save("Below", 124);
        StaffInfo first = save("First at threshold", 125);
        StaffInfo second = save("Second at threshold", 125);
        StaffInfo highest = save("Above", 126);
        save("Unknown balance", null);

        assertThat(repository.findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125))
                .extracting(StaffInfo::getId)
                .containsExactly(highest.getId(), first.getId(), second.getId());
    }

    @Test
    void reflectsBalanceChangesWithoutKeepingStaleEligibility() {
        StaffInfo employee = save("Employee", 125);
        assertThat(repository.findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125))
                .hasSize(1);

        employee.setCurrentPoints(124);
        repository.saveAndFlush(employee);
        assertThat(repository.findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125))
                .isEmpty();

        employee.setCurrentPoints(130);
        repository.saveAndFlush(employee);
        assertThat(repository.findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125))
                .extracting(StaffInfo::getId).containsExactly(employee.getId());
    }

    @Test
    void returnsEmptyWhenNobodyQualifies() {
        save("Below", 124);
        assertThat(repository.findByCurrentPointsGreaterThanEqualOrderByCurrentPointsDescIdAsc(125))
                .isEmpty();
    }

    private StaffInfo save(String name, Integer points) {
        StaffInfo employee = new StaffInfo();
        employee.setFullName(name);
        employee.setEmail(name.replace(' ', '-') + "@example.com");
        employee.setPosition1("Tutor");
        employee.setPosition2("Coach");
        employee.setSupervisor1("Test Supervisor");
        employee.setCurrentPoints(points);
        return repository.saveAndFlush(employee);
    }
}
