package com.atms.ticket.application;

import com.atms.ticket.api.dto.AddCommentRequest;
import com.atms.ticket.infrastructure.persistence.CommentEntity;
import com.atms.ticket.infrastructure.persistence.CommentRepository;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketService ticketService;
    private final TicketKnowledgeIndexPort knowledgeIndexPort;

    public CommentService(
            CommentRepository commentRepository,
            TicketService ticketService,
            TicketKnowledgeIndexPort knowledgeIndexPort) {
        this.commentRepository = commentRepository;
        this.ticketService = ticketService;
        this.knowledgeIndexPort = knowledgeIndexPort;
    }

    @Transactional
    public CommentEntity addComment(String displayId, AddCommentRequest request) {
        TicketEntity ticket = ticketService.getByDisplayId(displayId);
        CommentEntity comment = new CommentEntity();
        comment.setTicket(ticket);
        comment.setBody(request.body());
        comment.setAuthor(request.author());
        ticket.getComments().add(comment);
        CommentEntity saved = commentRepository.save(comment);
        knowledgeIndexPort.reindex(TicketSnapshotMapper.fromEntity(ticket));
        return saved;
    }
}
