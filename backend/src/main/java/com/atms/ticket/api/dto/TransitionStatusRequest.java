package com.atms.ticket.api.dto;

import com.atms.ticket.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TransitionStatusRequest(@NotNull TicketStatus status, String resolutionNotes) {}
