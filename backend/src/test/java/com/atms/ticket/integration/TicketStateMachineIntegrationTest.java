package com.atms.ticket.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atms.AtmsApplication;
import com.atms.support.PostgresTestcontainerExtension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(classes = AtmsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(PostgresTestcontainerExtension.class)
class TicketStateMachineIntegrationTest {

    @DynamicPropertySource
    static void registerDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresTestcontainerExtension::jdbcUrl);
        registry.add("spring.datasource.username", PostgresTestcontainerExtension::jdbcUser);
        registry.add("spring.datasource.password", PostgresTestcontainerExtension::jdbcPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clean() throws Exception {
        // isolation via fresh tickets per test; no global clean required
    }

    @Test
    void allowedTransitions_openInProgressResolvedClosed() throws Exception {
        String displayId = createTicket();
        transition(displayId, "IN_PROGRESS", null).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        transition(displayId, "RESOLVED", "Root cause fixed").andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.resolutionNotes").value("Root cause fixed"));
        transition(displayId, "CLOSED", null).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void allowedTransitions_openToCancelled() throws Exception {
        String displayId = createTicket();
        transition(displayId, "CANCELLED", null).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void allowedTransitions_inProgressToCancelled() throws Exception {
        String displayId = createTicket();
        transition(displayId, "IN_PROGRESS", null).andExpect(status().isOk());
        transition(displayId, "CANCELLED", null).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void resolveWithoutNotes_returns409() throws Exception {
        String displayId = createTicket();
        transition(displayId, "IN_PROGRESS", null).andExpect(status().isOk());
        transition(displayId, "RESOLVED", null).andExpect(status().isConflict());
        transition(displayId, "RESOLVED", "   ").andExpect(status().isConflict());
    }

    @ParameterizedTest
    @CsvSource({
        "CLOSED,OPEN",
        "RESOLVED,OPEN",
        "CANCELLED,OPEN",
        "OPEN,RESOLVED",
        "RESOLVED,CANCELLED"
    })
    void forbiddenTransitions_return409(String fromStatus, String toStatus) throws Exception {
        String displayId = createTicketInStatus(fromStatus);
        transition(displayId, toStatus, notesFor(toStatus)).andExpect(status().isConflict());
    }

    @Test
    void resolutionNotesEditableOnlyInResolved() throws Exception {
        String displayId = createTicket();
        patchResolutionNotes(displayId, "notes").andExpect(status().isConflict());

        transition(displayId, "IN_PROGRESS", null).andExpect(status().isOk());
        patchResolutionNotes(displayId, "notes").andExpect(status().isConflict());

        transition(displayId, "RESOLVED", "initial fix").andExpect(status().isOk());
        patchResolutionNotes(displayId, "updated fix details").andExpect(status().isOk())
                .andExpect(jsonPath("$.resolutionNotes").value("updated fix details"));
    }

    private String notesFor(String targetStatus) {
        return "RESOLVED".equals(targetStatus) ? "required notes" : null;
    }

    private String createTicket() throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/tickets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "title": "FSM test",
                                          "description": "desc",
                                          "priority": "LOW",
                                          "assignee": "agent"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("displayId").asText();
    }

    private String createTicketInStatus(String targetStatus) throws Exception {
        String displayId = createTicket();
        if ("OPEN".equals(targetStatus)) {
            return displayId;
        }
        if ("IN_PROGRESS".equals(targetStatus)) {
            transition(displayId, "IN_PROGRESS", null).andExpect(status().isOk());
            return displayId;
        }
        if ("RESOLVED".equals(targetStatus)) {
            transition(displayId, "IN_PROGRESS", null).andExpect(status().isOk());
            transition(displayId, "RESOLVED", "resolved notes").andExpect(status().isOk());
            return displayId;
        }
        if ("CLOSED".equals(targetStatus)) {
            transition(displayId, "IN_PROGRESS", null).andExpect(status().isOk());
            transition(displayId, "RESOLVED", "resolved notes").andExpect(status().isOk());
            transition(displayId, "CLOSED", null).andExpect(status().isOk());
            return displayId;
        }
        if ("CANCELLED".equals(targetStatus)) {
            transition(displayId, "CANCELLED", null).andExpect(status().isOk());
            return displayId;
        }
        throw new IllegalArgumentException("Unknown status: " + targetStatus);
    }

    private org.springframework.test.web.servlet.ResultActions transition(
            String displayId, String status, String resolutionNotes) throws Exception {
        String notesJson = resolutionNotes == null
                ? ""
                : ",\"resolutionNotes\":\"" + resolutionNotes.replace("\"", "\\\"") + "\"";
        return mockMvc.perform(
                patch("/api/tickets/{id}/status", displayId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"status":"%s"%s}
                                """
                                        .formatted(status, notesJson)));
    }

    private org.springframework.test.web.servlet.ResultActions patchResolutionNotes(
            String displayId, String notes) throws Exception {
        return mockMvc.perform(
                patch("/api/tickets/{id}/resolution-notes", displayId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resolutionNotes\":\"" + notes.replace("\"", "\\\"") + "\"}"));
    }
}
