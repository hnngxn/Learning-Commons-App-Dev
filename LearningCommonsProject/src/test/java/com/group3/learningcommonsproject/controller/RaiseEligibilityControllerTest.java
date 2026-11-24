package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.dto.RaiseEligibilityResponse;
import com.group3.learningcommonsproject.services.RaiseEligibilityService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RaiseEligibilityControllerTest {
    private final RaiseEligibilityService service = mock(RaiseEligibilityService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new RaiseEligibilityController(service)).build();

    @Test
    void returnsEligibleStaffAsJson() throws Exception {
        when(service.getEligibleStaff()).thenReturn(List.of(
                new RaiseEligibilityResponse(2L, "At threshold", 125, 125, true)));

        String body = mvc.perform(get("/api/raise-eligibility"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var row = JsonMapper.builder().build().readTree(body).get(0);
        assertThat(row.get("staffId").longValue()).isEqualTo(2L);
        assertThat(row.get("fullName").stringValue()).isEqualTo("At threshold");
        assertThat(row.get("currentPoints").intValue()).isEqualTo(125);
        assertThat(row.get("eligibilityThreshold").intValue()).isEqualTo(125);
        assertThat(row.get("eligible").booleanValue()).isTrue();
        verify(service).getEligibleStaff();
    }

    @Test
    void returnsEmptyJsonArrayWhenNobodyQualifies() throws Exception {
        when(service.getEligibleStaff()).thenReturn(List.of());
        String body = mvc.perform(get("/api/raise-eligibility"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(body).isEqualTo("[]");
    }
}
