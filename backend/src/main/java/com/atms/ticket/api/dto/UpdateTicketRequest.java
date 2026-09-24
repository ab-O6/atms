package com.atms.ticket.api.dto;

import com.atms.ticket.domain.Priority;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @Size(max = 200) String title,
        String description,
        Priority priority,
        @Size(max = 120) String assignee) {}
