package com.atms.ticket.application;

import com.atms.ticket.domain.TicketStatus;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import com.atms.ticket.infrastructure.persistence.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResolutionNotesService {

    private final TicketRepository ticketRepository;
    private final TicketKnowledgeIndexPort knowledgeIndexPort;

    public ResolutionNotesService(TicketRepository ticketRepository, TicketKnowledgeIndexPort knowledgeIndexPort) {
        this.ticketRepository = ticketRepository;
        this.knowledgeIndexPort = knowledgeIndexPort;
    }

    @Transactional
    public TicketEntity update(String displayId, String resolutionNotes) {
        TicketEntity ticket = ticketRepository
                .findByDisplayId(displayId)
                .orElseThrow(() -> new TicketNotFoundException(displayId));

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new ResolutionNotesNotEditableException();
        }

        ticket.setResolutionNotes(resolutionNotes.trim());
        TicketEntity saved = ticketRepository.save(ticket);
        knowledgeIndexPort.reindex(TicketSnapshotMapper.fromEntity(saved));
        return saved;
    }
}
