package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.entity.PointEvent;
import com.group3.learningcommonsproject.entity.StaffInfo;
import com.group3.learningcommonsproject.repository.EventLogRepository;
import com.group3.learningcommonsproject.repository.PointEventRepository;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:submission-flow;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class EventSubmissionFlowTest {
    @Autowired WebApplicationContext context;
    @Autowired StaffInfoRepository staff;
    @Autowired PointEventRepository events;
    @Autowired EventLogRepository logs;
    MockMvc mvc;
    StaffInfo person;
    PointEvent event;
    MockHttpSession session;

    @BeforeEach
    void setUp() {
        // No enclosing test transaction: assertions observe committed service writes.
        logs.deleteAll();
        staff.deleteAll();
        events.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        person = new StaffInfo();
        person.setFullName("Submission Test");
        person.setEmail("submission@example.test");
        person.setPosition1("Tutor");
        person.setSupervisor1("Supervisor");
        person.setCurrentPoints(120);
        person = staff.saveAndFlush(person);
        event = new PointEvent();
        event.setEventName("Job Performance");
        event.setEventType("Bonus");
        event.setMinPoints(1);
        event.setMaxPoints(10);
        event = events.saveAndFlush(event);
    }

    @ParameterizedTest
    @CsvSource({"Bonus,1,10,5,125", "Infraction,-20,-1,-5,115"})
    void rendersFormReviewAndSavedReceiptWithoutDuplicateWrites(
            String type, int min, int max, int applied, int balance) throws Exception {
        event.setEventType(type);
        event.setMinPoints(min);
        event.setMaxPoints(max);
        events.saveAndFlush(event);
        mvc.perform(get("/entries/new").param("staff", person.getId().toString()))
                .andExpect(status().isOk()).andExpect(view().name("entry-form"))
                .andExpect(model().attribute("selectedStaffId", person.getId()));
        mvc.perform(review()).andExpect(status().isOk())
                .andExpect(view().name("entry-review"))
                .andExpect(model().attribute("projectedBalance", balance));
        assertThat(logs.count()).isZero();
        assertBalance(120);
        var draft = draft();
        MvcResult result = mvc.perform(post("/entries").session(session)
                        .param("token", draft.token()))
                .andExpect(status().is3xxRedirection()).andReturn();
        String receipt = result.getResponse().getRedirectedUrl();
        assertThat(logs.count()).isEqualTo(1);
        assertBalance(balance);
        var saved = logs.findAll().get(0);
        assertThat(saved.getPointsApplied()).isEqualTo(applied);
        assertThat(saved.getBalanceAfter()).isEqualTo(balance);
        assertThat(saved.getPositionAtEntry()).isEqualTo("Tutor");
        assertThat(saved.getSupervisorAtEntry()).isEqualTo("Supervisor");
        assertThat(saved.getComments()).isEqualTo("Reviewed details");
        assertThat(saved.getEnteredBy()).isEqualTo("Reviewer");
        for (int i = 0; i < 2; i++) {
            mvc.perform(get(receipt).session(session)).andExpect(status().isOk())
                    .andExpect(view().name("submission-confirmation"))
                    .andExpect(content().string(containsString("<h1>Entry submitted</h1>")));
        }
        mvc.perform(post("/entries").session(session).param("token", draft.token()))
                .andExpect(redirectedUrl(receipt));
        assertThat(logs.count()).isEqualTo(1);
        assertBalance(balance);
        mvc.perform(get("/entries/new").param("staff", person.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void editPreservesValuesAndOldReviewTokenCannotSubmit() throws Exception {
        mvc.perform(review()).andExpect(status().isOk());
        String oldToken = draft().token();
        mvc.perform(get("/entries/edit").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"5\"")))
                .andExpect(content().string(containsString("Reviewed details</textarea>")))
                .andExpect(content().string(containsString("value=\"Reviewer\"")));
        mvc.perform(review()).andExpect(status().isOk());
        mvc.perform(post("/entries").session(session).param("token", oldToken))
                .andExpect(status().isConflict());
        assertThat(logs.count()).isZero();
        assertBalance(120);
    }

    @Test
    void missingBalanceIsRejectedAtReviewAndRecheckedAtSubmit() throws Exception {
        mvc.perform(review()).andExpect(status().isOk());
        String token = draft().token();
        person.setCurrentPoints(null);
        staff.saveAndFlush(person);
        mvc.perform(post("/entries").session(session).param("token", token))
                .andExpect(status().isConflict());
        mvc.perform(review()).andExpect(status().isConflict());
        assertThat(logs.count()).isZero();
        assertThat(staff.findById(person.getId()).orElseThrow().getCurrentPoints()).isNull();
    }

    @Test
    void recalculatesBalanceChangedSinceReview() throws Exception {
        mvc.perform(review()).andExpect(status().isOk());
        String token = draft().token();
        person.setCurrentPoints(130);
        staff.saveAndFlush(person);
        mvc.perform(post("/entries").session(session).param("token", token))
                .andExpect(status().is3xxRedirection());
        assertBalance(135);
        assertThat(logs.findAll().get(0).getBalanceAfter()).isEqualTo(135);
    }

    @Test
    void overflowAtSubmitDoesNotSaveHistoryOrChangeBalance() throws Exception {
        mvc.perform(review()).andExpect(status().isOk());
        String token = draft().token();
        person.setCurrentPoints(Integer.MAX_VALUE);
        staff.saveAndFlush(person);
        mvc.perform(post("/entries").session(session).param("token", token))
                .andExpect(status().isConflict());
        assertBalance(Integer.MAX_VALUE);
        assertThat(logs.count()).isZero();
    }

    @Test
    void eventRemovedAfterReviewDoesNotChangeBalance() throws Exception {
        mvc.perform(review()).andExpect(status().isOk());
        String token = draft().token();
        events.deleteById(event.getId());
        mvc.perform(post("/entries").session(session).param("token", token))
                .andExpect(status().isNotFound());
        assertBalance(120);
        assertThat(logs.count()).isZero();
    }

    @Test
    void invalidInfractionDoesNotCreateDraftOrSave() throws Exception {
        event.setEventType("Infraction");
        event.setMinPoints(-20);
        event.setMaxPoints(-1);
        events.saveAndFlush(event);
        mvc.perform(review().with(request -> { request.setParameter("comments", ""); return request; }))
                .andExpect(status().isBadRequest());
        assertThat(session.getAttribute("submissionDraft")).isNull();
        assertThat(logs.count()).isZero();
        assertBalance(120);
    }

    @Test
    void expiredDraftAndMissingReceiptReturnControlledResponses() throws Exception {
        mvc.perform(get("/entries/edit").session(session))
                .andExpect(redirectedUrl("/entries/new"));
        mvc.perform(post("/entries").session(session).param("token", "expired"))
                .andExpect(status().isConflict());
        mvc.perform(get("/entries/999999")) .andExpect(status().isNotFound());
    }

    private MockHttpServletRequestBuilder review() {
        return post("/entries/review").session(session)
                .param("staffId", person.getId().toString())
                .param("pointEventId", event.getId().toString())
                .param("points", "5").param("eventDate", "2026-11-23")
                .param("comments", " Reviewed details ").param("enteredBy", " Reviewer ");
    }

    private EventEntryController.SubmissionDraft draft() {
        return (EventEntryController.SubmissionDraft) session.getAttribute("submissionDraft");
    }

    private void assertBalance(int expected) {
        assertThat(staff.findById(person.getId()).orElseThrow().getCurrentPoints()).isEqualTo(expected);
    }
}
