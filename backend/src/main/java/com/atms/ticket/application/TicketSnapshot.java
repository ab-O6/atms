package com.atms.ticket.application;

import java.util.UUID;

/**
 * Immutable ticket view passed to the knowledge index port after ticket or comment mutations.
 *
 * @param id internal surrogate key (never exposed in ask responses)
 * @param displayId user-facing ticket id (e.g. TKT-1001)
 */
public record TicketSnapshot(
        UUID id,
        String displayId,
        String title,
        String description,
        String priority,
        String assignee,
        String category,
        String status,
        String resolutionNotes) {}
