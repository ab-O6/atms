package com.atms.ticket.application;

import com.atms.ticket.domain.TicketStatus;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import com.atms.ticket.infrastructure.persistence.TicketRepository;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransitionService {

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED = Map.of(
            TicketStatus.OPEN, EnumSet.of(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED),
            TicketStatus.IN_PROGRESS, EnumSet.of(TicketStatus.RESOLVED, TicketStatus.CANCELLED),
            TicketStatus.RESOLVED, EnumSet.of(TicketStatus.CLOSED),
            TicketStatus.CLOSED, EnumSet.noneOf(TicketStatus.class),
            TicketStatus.CANCELLED, EnumSet.noneOf(TicketStatus.class));

    private final TicketRepository ticketRepository;
    private final TicketKnowledgeIndexPort knowledgeIndexPort;

    public TransitionService(TicketRepository ticketRepository, TicketKnowledgeIndexPort knowledgeIndexPort) {
        this.ticketRepository = ticketRepository;
        this.knowledgeIndexPort = knowledgeIndexPort;
    }

    @Transactional
    public TicketEntity transition(String displayId, TicketStatus targetStatus, String resolutionNotes) {
        TicketEntity ticket = ticketRepository
                .findByDisplayId(displayId)
                .orElseThrow(() -> new TicketNotFoundException(displayId));

        TicketStatus current = ticket.getStatus();
        if (current == targetStatus) {
            return ticket;
        }

        Set<TicketStatus> allowedTargets = ALLOWED.getOrDefault(current, EnumSet.noneOf(TicketStatus.class));
        if (!allowedTargets.contains(targetStatus)) {
            throw new InvalidTicketTransitionException(
                    "Cannot transition from " + current + " to " + targetStatus);
        }

        if (targetStatus == TicketStatus.RESOLVED) {
            if (resolutionNotes == null || resolutionNotes.isBlank()) {
                throw new ResolutionNotesRequiredException();
            }
            ticket.setResolutionNotes(resolutionNotes.trim());
        }

        ticket.setStatus(targetStatus);
        TicketEntity saved = ticketRepository.save(ticket);
        knowledgeIndexPort.reindex(TicketSnapshotMapper.fromEntity(saved));
        return saved;
    }
}
