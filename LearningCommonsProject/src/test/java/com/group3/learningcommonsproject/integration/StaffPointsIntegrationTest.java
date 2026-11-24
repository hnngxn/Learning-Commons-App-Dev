package com.group3.learningcommonsproject.integration;

import com.group3.learningcommonsproject.controller.EventEntryController;
import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.entity.Program;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class StaffPointsIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired StaffInfoRepository staff;
    @Autowired ProgramRepository programs;
    @Autowired PointEventRepository events;
    @Autowired EventLogRepository logs;
    @Autowired SemesterPointResetEntryRepository resetEntries;
    @Autowired PointReversalRepository reversals;

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry properties) {
        String mysql = System.getProperty("integration.mysql.url");
        // Optional MySQL runs require the explicitly named disposable review database.
        if (mysql != null && !mysql.matches("jdbc:mysql://127\\.0\\.0\\.1:[0-9]+/lc_points_review(?:\\?.*)?")) {
            throw new IllegalArgumentException("Use the disposable local lc_points_review database.");
        }
        properties.add("spring.datasource.url", () -> mysql == null
                ? "jdbc:h2:mem:combined-points;DB_CLOSE_DELAY=-1" : mysql);
        properties.add("spring.datasource.driver-class-name", () -> mysql == null
                ? "org.h2.Driver" : "com.mysql.cj.jdbc.Driver");
        properties.add("spring.datasource.username", () -> mysql == null ? "sa" : "root");
        properties.add("spring.datasource.password", () -> mysql == null ? ""
                : System.getProperty("integration.mysql.password", ""));
        properties.add("spring.flyway.enabled", () -> "false");
        // Flyway must already have prepared the disposable MySQL schema.
        properties.add("spring.jpa.hibernate.ddl-auto", () -> mysql == null ? "create-drop" : "none");
        properties.add("spring.jpa.database-platform", () -> mysql == null
                ? "org.hibernate.dialect.H2Dialect" : "org.hibernate.dialect.MySQLDialect");
    }

    @Test
    void programMembershipSurvivesResetSubmissionEligibilityAndReversal() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
        JsonMapper json = JsonMapper.builder().build();
        Program program = new Program();
        program.setProgramName("Integration review program");
        program = programs.saveAndFlush(program);
        StaffInfo person = new StaffInfo();
        person.setFullName("Integration Review");
        person.setEmail("integration@example.test");
        person.setPosition1("Tutor");
        person.setSupervisor1("Review Supervisor");
        person.setCurrentPoints(120);
        person.getPrograms().add(program);
        person = staff.saveAndFlush(person);
        Long id = person.getId();

        var filter = mvc.perform(get("/api/staff-information/filter")
                        .param("programId", program.getId().toString()))
                .andExpect(status().isOk()).andReturn();
        var filtered = json.readTree(filter.getResponse().getContentAsString());
        assertThat(filtered.size()).isEqualTo(1);
        assertThat(filtered.get(0).get("id").asLong()).isEqualTo(id);
        assertThat(filtered.get(0).get("position1").asText()).isEqualTo("Tutor");

        String roster = "{\"year\":2027,\"term\":\"SPRING\",\"employees\":[{\"staffId\":"
                + id + ",\"status\":\"NEW\"}]}";
        var reset = mvc.perform(post("/api/semester-point-resets").contentType(MediaType.APPLICATION_JSON)
                .content(roster)).andExpect(status().isCreated()).andReturn();
        Long resetId = json.readTree(reset.getResponse().getContentAsString()).get("resetId").asLong();
        var history = resetEntries.findByReset_IdOrderByStaff_Id(resetId);
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getPreviousPoints()).isEqualTo(120);
        assertThat(history.get(0).getStartingPoints()).isEqualTo(65);
        assertBalance(id, 65);

        PointEvent event = new PointEvent();
        event.setEventName("Integration bonus");
        event.setEventType("Bonus");
        event.setMinPoints(1);
        event.setMaxPoints(100);
        event = events.saveAndFlush(event);
        MockHttpSession session = new MockHttpSession();
        mvc.perform(post("/entries/review").session(session)
                        .param("staffId", id.toString()).param("pointEventId", event.getId().toString())
                        .param("points", "60").param("eventDate", "2026-11-23")
                        .param("comments", "Integrated review").param("enteredBy", "Reviewer"))
                .andExpect(status().isOk()).andExpect(model().attribute("projectedBalance", 125));
        var draft = (EventEntryController.SubmissionDraft) session.getAttribute("submissionDraft");
        var submit = mvc.perform(post("/entries").session(session).param("token", draft.token()))
                .andExpect(status().is3xxRedirection()).andReturn();
        mvc.perform(get(submit.getResponse().getRedirectedUrl()))
                .andExpect(status().isOk()).andExpect(view().name("submission-confirmation"));
        assertBalance(id, 125);
        var eligibility = mvc.perform(get("/api/raise-eligibility"))
                .andExpect(status().isOk()).andReturn();
        boolean eligible = false;
        for (var row : json.readTree(eligibility.getResponse().getContentAsString())) {
            if (row.get("staffId").asLong() == id) eligible = row.get("eligible").asBoolean();
        }
        assertThat(eligible).isTrue();

        var entry = logs.findBySubmissionToken(draft.token()).orElseThrow();
        mvc.perform(post("/api/event-logs/" + entry.getId() + "/reversal")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"Review correction\"}"))
                .andExpect(status().isCreated());
        assertBalance(id, 65);
        assertThat(reversals.existsByEventLog_Id(entry.getId())).isTrue();
        assertThat(logs.findById(entry.getId()).orElseThrow().getBalanceAfter()).isEqualTo(125);
        mvc.perform(post("/api/event-logs/" + entry.getId() + "/reversal")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"Duplicate\"}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/semester-point-resets").contentType(MediaType.APPLICATION_JSON)
                .content(roster)).andExpect(status().isConflict());
        assertThat(staff.findByPrograms_Id(program.getId())).extracting(StaffInfo::getId).contains(id);

        // The existing staff DTO update must not erase the program association.
        String update = "{\"fullName\":\"Integration Updated\",\"email\":\"integration@example.test\","
                + "\"position1\":\"Tutor\",\"supervisor1\":\"Review Supervisor\",\"currentPoints\":65}";
        mvc.perform(put("/api/staff-information/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(update)).andExpect(status().isOk());
        assertThat(staff.findByPrograms_Id(program.getId())).extracting(StaffInfo::getId).contains(id);
        assertBalance(id, 65);
    }

    private void assertBalance(Long id, int expected) {
        assertThat(staff.findById(id).orElseThrow().getCurrentPoints()).isEqualTo(expected);
    }
}
