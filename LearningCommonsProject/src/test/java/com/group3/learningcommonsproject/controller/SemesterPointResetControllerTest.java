package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.SemesterPointResetResponse;
import com.group3.learningcommonsproject.services.SemesterPointResetService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SemesterPointResetControllerTest {
    private static final String BODY = """
            {"year":2027,"term":"SPRING","employees":[{"staffId":1,"status":"NEW"}]}
            """;
    private final SemesterPointResetService service = mock(SemesterPointResetService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new SemesterPointResetController(service)).build();

    @Test
    void returns201WithSavedResetAndEmployeeHistory() throws Exception {
        when(service.reset(any())).thenReturn(new SemesterPointResetResponse(
                4L, 2027, "SPRING", Instant.parse("2026-11-23T12:00:00Z"), 1,
                List.of(new SemesterPointResetResponse.EmployeeResult(1L, "NEW", 95, 65))));

        String body = mvc.perform(post("/api/semester-point-resets")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        var result = JsonMapper.builder().build().readTree(body);
        assertThat(result.get("resetId").longValue()).isEqualTo(4L);
        assertThat(result.get("employeesUpdated").intValue()).isEqualTo(1);
        assertThat(result.get("employees").get(0).get("previousPoints").intValue()).isEqualTo(95);
        assertThat(result.get("employees").get(0).get("startingPoints").intValue()).isEqualTo(65);
        var request = org.mockito.ArgumentCaptor.forClass(
                com.group3.learningcommonsproject.dto.SemesterPointResetRequest.class);
        verify(service).reset(request.capture());
        assertThat(request.getValue().year()).isEqualTo(2027);
        assertThat(request.getValue().employees().get(0).staffId()).isEqualTo(1L);
    }

    @Test
    void unknownTermReturns400BeforeCallingService() throws Exception {
        mvc.perform(post("/api/semester-point-resets").contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("SPRING", "WINTER")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void missingBodyReturns400BeforeCallingService() throws Exception {
        mvc.perform(post("/api/semester-point-resets").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void preservesServiceConflictStatus() throws Exception {
        when(service.reset(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT,
                "This semester has already been reset."));
        mvc.perform(post("/api/semester-point-resets").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict());
    }
}
