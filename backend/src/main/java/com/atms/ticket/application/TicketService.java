package com.atms.ticket.application;

import com.atms.ticket.api.dto.CreateTicketRequest;
import com.atms.ticket.api.dto.UpdateTicketRequest;
import com.atms.ticket.domain.TicketStatus;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import com.atms.ticket.infrastructure.persistence.TicketRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final DisplayIdGenerator displayIdGenerator;
    private final TicketKnowledgeIndexPort knowledgeIndexPort;

    public TicketService(
            TicketRepository ticketRepository,
            DisplayIdGenerator displayIdGenerator,
            TicketKnowledgeIndexPort knowledgeIndexPort) {
        this.ticketRepository = ticketRepository;
        this.displayIdGenerator = displayIdGenerator;
        this.knowledgeIndexPort = knowledgeIndexPort;
    }

    @Transactional
    public TicketEntity create(CreateTicketRequest request) {
        TicketEntity entity = new TicketEntity();
        entity.setDisplayId(displayIdGenerator.nextDisplayId());
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setPriority(request.priority());
        entity.setAssignee(request.assignee());
        entity.setCategory(request.category() != null ? request.category() : "");
        entity.setStatus(TicketStatus.OPEN);
        TicketEntity saved = ticketRepository.save(entity);
        knowledgeIndexPort.reindex(TicketSnapshotMapper.fromEntity(saved));
        return saved;
    }

    @Transactional(readOnly = true)
    public TicketEntity getByDisplayId(String displayId) {
        return ticketRepository
                .findByDisplayId(displayId)
                .orElseThrow(() -> new TicketNotFoundException(displayId));
    }

    @Transactional(readOnly = true)
    public List<TicketEntity> listAll() {
        return ticketRepository.findAllOrderByUpdatedAtDesc();
    }

    @Transactional
    public TicketEntity update(String displayId, UpdateTicketRequest request) {
        TicketEntity entity = getByDisplayId(displayId);
        if (request.title() != null) {
            entity.setTitle(request.title());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.priority() != null) {
            entity.setPriority(request.priority());
        }
        if (request.assignee() != null) {
            entity.setAssignee(request.assignee());
        }
        TicketEntity saved = ticketRepository.save(entity);
        knowledgeIndexPort.reindex(TicketSnapshotMapper.fromEntity(saved));
        return saved;
    }
}
