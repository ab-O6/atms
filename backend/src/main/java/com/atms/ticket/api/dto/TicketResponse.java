package com.atms.ticket.api.dto;

import com.atms.ticket.domain.Priority;
import com.atms.ticket.domain.TicketStatus;
import java.time.Instant;
import java.util.List;

public record TicketResponse(
        String displayId,
        String title,
        TicketStatus status,
        Priority priority,
        String assignee,
        Instant updatedAt,
        String description,
        String category,
        String resolutionNotes,
        Instant createdAt,
        List<CommentResponse> comments) {}
