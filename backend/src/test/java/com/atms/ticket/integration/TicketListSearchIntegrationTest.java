package com.atms.ticket.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atms.AtmsApplication;
import com.atms.support.PostgresTestcontainerExtension;
import com.atms.ticket.domain.TicketStatus;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import com.atms.ticket.infrastructure.persistence.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class TicketListSearchIntegrationTest {

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

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void cleanTickets() {
        ticketRepository.deleteAll();
    }

    @Test
    void keywordMatchesTitleDescriptionAndDisplayIdCaseInsensitive() throws Exception {
        String displayId = createTicket("CamelCase Title", "plain description", "networking", null);
        assertThat(displayId).isNotBlank();

        assertDisplayIds(getList("?q=camelcase"), displayId);
        assertDisplayIds(getList("?q=PLAIN"), displayId);
        assertDisplayIds(getList("?q=" + displayId.toLowerCase()), displayId);
    }

    @Test
    void keywordExcludesCommentsResolutionNotesAndCategory() throws Exception {
        String displayId = createTicket("Visible title", "Visible body", "billing-only-here", null);

        mockMvc.perform(
                        post("/api/tickets/{id}/comments", displayId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {"body":"comment-only-needle-xyz","author":"tester"}
                                        """))
                .andExpect(status().isCreated());

        TicketEntity entity = ticketRepository.findByDisplayId(displayId).orElseThrow();
        entity.setResolutionNotes("resolution-only-needle-abc");
        ticketRepository.save(entity);

        assertDisplayIds(getList("?q=comment-only-needle-xyz"), null);
        assertDisplayIds(getList("?q=resolution-only-needle-abc"), null);
        assertDisplayIds(getList("?q=billing-only-here"), null);
        assertDisplayIds(getList("?q=Visible"), displayId);
    }

    @Test
    void statusFilterAndEmptyResults() throws Exception {
        String openId = createTicket("Open ticket", "desc", "", null);
        String otherId = createTicket("Other ticket", "desc", "", null);

        TicketEntity other = ticketRepository.findByDisplayId(otherId).orElseThrow();
        other.setStatus(TicketStatus.IN_PROGRESS);
        ticketRepository.save(other);

        assertDisplayIds(getList("?status=OPEN"), openId);
        assertDisplayIds(getList("?status=IN_PROGRESS"), otherId);
        assertDisplayIds(getList("?q=no-such-ticket-text-ever"), null);
    }

    private String createTicket(String title, String description, String category, String priority)
            throws Exception {
        String cat = category == null || category.isEmpty() ? "" : ",\"category\":\"" + category + "\"";
        String prio = priority == null ? "LOW" : priority;
        MvcResult result = mockMvc.perform(
                        post("/api/tickets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "title": "%s",
                                          "description": "%s",
                                          "priority": "%s",
                                          "assignee": "agent1"%s
                                        }
                                        """
                                                .formatted(title, description, prio, cat.isEmpty() ? "" : cat)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("displayId").asText();
    }

    private JsonNode getList(String queryString) throws Exception {
        MvcResult result =
                mockMvc.perform(get("/api/tickets" + queryString)).andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private void assertDisplayIds(JsonNode list, String expectedDisplayId) {
        assertThat(list.isArray()).isTrue();
        if (expectedDisplayId == null) {
            assertThat(list).isEmpty();
            return;
        }
        assertThat(list.findValuesAsText("displayId")).contains(expectedDisplayId);
    }
}
