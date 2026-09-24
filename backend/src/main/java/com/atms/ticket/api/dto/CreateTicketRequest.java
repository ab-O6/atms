package com.atms.ticket.api.dto;

import com.atms.ticket.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String description,
        @NotNull Priority priority,
        @NotBlank @Size(max = 120) String assignee,
        @Size(max = 80) String category) {}
