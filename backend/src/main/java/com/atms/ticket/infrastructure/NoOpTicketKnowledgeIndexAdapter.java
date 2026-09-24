package com.atms.ticket.infrastructure;

import com.atms.ticket.application.TicketKnowledgeIndexPort;
import com.atms.ticket.application.TicketSnapshot;
import org.springframework.stereotype.Component;

/**
 * No-op index adapter until US4 provides a real PGVector implementation.
 */
@Component
public class NoOpTicketKnowledgeIndexAdapter implements TicketKnowledgeIndexPort {

    @Override
    public void reindex(TicketSnapshot snapshot) {
        // replaced by TicketKnowledgeIndexAdapter in US4
    }
}
