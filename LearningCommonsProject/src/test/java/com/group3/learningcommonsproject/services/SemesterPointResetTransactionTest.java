package com.group3.learningcommonsproject.services;

import com.group3.learningcommonsproject.dto.SemesterPointResetRequest;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.repository.SemesterPointResetEntryRepository;
import com.group3.learningcommonsproject.repository.SemesterPointResetRepository;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.group3.learningcommonsproject.dto.SemesterPointResetRequest.EmployeeStatus.*;
import static com.group3.learningcommonsproject.dto.SemesterPointResetRequest.SemesterTerm.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@Import(SemesterPointResetService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SemesterPointResetTransactionTest {
    @Autowired private SemesterPointResetService service;
    @Autowired private SemesterPointResetRepository resets;
    @Autowired private SemesterPointResetEntryRepository entries;
    @Autowired private StaffInfoRepository staff;

    @BeforeEach
    void clearTestDatabase() {
        entries.deleteAllInBatch();
        resets.deleteAllInBatch();
        staff.deleteAllInBatch();
    }

    @Test
    void commitsStartingBalancesAndPreviousBalanceHistoryForSelectedStaffOnly() {
        StaffInfo newcomer = save("New employee", 95);
        StaffInfo returning = save("Returning employee", 110);
        StaffInfo excluded = save("Not continuing", 101);

        var response = service.reset(request(List.of(
                selection(returning.getId(), RETURNING), selection(newcomer.getId(), NEW))));

        assertThat(response.resetId()).isNotNull();
        assertThat(response.resetAt()).isNotNull();
        assertThat(response.year()).isEqualTo(2027);
        assertThat(response.term()).isEqualTo("SPRING");
        assertThat(response.employeesUpdated()).isEqualTo(2);
        assertThat(response.employees()).extracting(r -> r.staffId())
                .containsExactly(newcomer.getId(), returning.getId());
        assertThat(balance(newcomer)).isEqualTo(65);
        assertThat(balance(returning)).isEqualTo(70);
        assertThat(balance(excluded)).isEqualTo(101);
        var history = entries.findByReset_IdOrderByStaff_Id(response.resetId());
        assertThat(history).extracting(e -> e.getPreviousPoints()).containsExactly(95, 110);
        assertThat(history).extracting(e -> e.getStartingPoints()).containsExactly(65, 70);
        assertThat(history).extracting(e -> e.getEmployeeStatus()).containsExactly("NEW", "RETURNING");
        assertThat(resets.count()).isEqualTo(1);
    }

    @Test
    void missingLaterEmployeeRollsBackEarlierBalanceAndBothHistoryTables() {
        StaffInfo employee = save("Existing employee", 95);

        assertStatus(request(List.of(selection(employee.getId(), NEW),
                selection(Long.MAX_VALUE, RETURNING))), HttpStatus.NOT_FOUND);

        assertThat(balance(employee)).isEqualTo(95);
        assertThat(entries.count()).isZero();
        assertThat(resets.count()).isZero();
    }

    @Test
    void missingLaterBalanceRollsBackEarlierBalanceAndBothHistoryTables() {
        StaffInfo employee = save("Existing employee", 95);
        StaffInfo unknown = save("Unknown balance", null);

        assertStatus(request(List.of(selection(employee.getId(), NEW),
                selection(unknown.getId(), RETURNING))), HttpStatus.CONFLICT);

        assertThat(balance(employee)).isEqualTo(95);
        assertThat(balance(unknown)).isNull();
        assertThat(entries.count()).isZero();
        assertThat(resets.count()).isZero();
    }

    @Test
    void repeatedSemesterDoesNotResetBalancesOrAddHistoryAgain() {
        StaffInfo employee = save("Employee", 95);
        var request = request(List.of(selection(employee.getId(), NEW)));
        service.reset(request);
        StaffInfo updated = staff.findById(employee.getId()).orElseThrow();
        updated.setCurrentPoints(83);
        staff.saveAndFlush(updated);

        assertStatus(request, HttpStatus.CONFLICT);

        assertThat(balance(employee)).isEqualTo(83);
        assertThat(entries.count()).isEqualTo(1);
        assertThat(resets.count()).isEqualTo(1);
    }

    @Test
    void differentTermInSameYearCanBeResetSeparately() {
        StaffInfo employee = save("Employee", 95);
        var roster = List.of(selection(employee.getId(), RETURNING));
        service.reset(request(roster));
        service.reset(new SemesterPointResetRequest(2027, FALL, roster));

        assertThat(resets.count()).isEqualTo(2);
        assertThat(entries.count()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void rejectsInvalidRequestWithoutWritingAnything(SemesterPointResetRequest request) {
        assertStatus(request, HttpStatus.BAD_REQUEST);
        assertThat(resets.count()).isZero();
        assertThat(entries.count()).isZero();
        assertThat(staff.count()).isZero();
    }

    static Stream<SemesterPointResetRequest> invalidRequests() {
        var valid = List.of(selection(1L, NEW));
        return Stream.of(
                null,
                new SemesterPointResetRequest(null, SPRING, valid),
                new SemesterPointResetRequest(1999, SPRING, valid),
                new SemesterPointResetRequest(2101, SPRING, valid),
                new SemesterPointResetRequest(2027, null, valid),
                request(null), request(List.of()), request(Arrays.asList((SemesterPointResetRequest.EmployeeSelection) null)),
                request(List.of(selection(null, NEW))), request(List.of(selection(0L, NEW))),
                request(List.of(selection(-1L, NEW))), request(List.of(selection(1L, null))),
                request(List.of(selection(1L, NEW), selection(1L, RETURNING))));
    }

    private void assertStatus(SemesterPointResetRequest request, HttpStatus status) {
        assertThatThrownBy(() -> service.reset(request)).isInstanceOfSatisfying(
                ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(status));
    }

    private Integer balance(StaffInfo employee) {
        return staff.findById(employee.getId()).orElseThrow().getCurrentPoints();
    }

    private StaffInfo save(String name, Integer points) {
        StaffInfo employee = new StaffInfo();
        employee.setFullName(name);
        employee.setEmail(name.replace(' ', '-') + "@example.com");
        employee.setPosition1("Tutor");
        employee.setPosition2("Coach");
        employee.setSupervisor1("Test supervisor");
        employee.setCurrentPoints(points);
        return staff.saveAndFlush(employee);
    }

    private static SemesterPointResetRequest request(List<SemesterPointResetRequest.EmployeeSelection> employees) {
        return new SemesterPointResetRequest(2027, SPRING, employees);
    }

    private static SemesterPointResetRequest.EmployeeSelection selection(
            Long id, SemesterPointResetRequest.EmployeeStatus status) {
        return new SemesterPointResetRequest.EmployeeSelection(id, status);
    }
}
