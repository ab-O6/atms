package com.atms.ticket.api;

import com.atms.ticket.api.dto.AddCommentRequest;
import com.atms.ticket.api.dto.CommentResponse;
import com.atms.ticket.api.dto.CreateTicketRequest;
import com.atms.ticket.api.dto.TicketResponse;
import com.atms.ticket.api.dto.TicketSummaryResponse;
import com.atms.ticket.api.dto.UpdateTicketRequest;
import com.atms.ticket.api.mapper.TicketMapper;
import com.atms.ticket.application.CommentService;
import com.atms.ticket.application.TicketService;
import com.atms.ticket.infrastructure.persistence.CommentEntity;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CommentService commentService;
    private final TicketMapper ticketMapper;

    public TicketController(TicketService ticketService, CommentService commentService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.commentService = commentService;
        this.ticketMapper = ticketMapper;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        TicketEntity created = ticketService.create(request);
        TicketResponse body = ticketMapper.toDetail(created);
        return ResponseEntity.created(URI.create("/api/tickets/" + created.getDisplayId())).body(body);
    }

    @GetMapping
    public List<TicketSummaryResponse> list() {
        return ticketService.listAll().stream().map(ticketMapper::toSummary).toList();
    }

    @GetMapping("/{displayId}")
    public TicketResponse get(@PathVariable String displayId) {
        TicketEntity entity = ticketService.getByDisplayId(displayId);
        return ticketMapper.toDetail(entity);
    }

    @PatchMapping("/{displayId}")
    public TicketResponse update(@PathVariable String displayId, @Valid @RequestBody UpdateTicketRequest request) {
        TicketEntity entity = ticketService.update(displayId, request);
        return ticketMapper.toDetail(entity);
    }

    @PostMapping("/{displayId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String displayId, @Valid @RequestBody AddCommentRequest request) {
        CommentEntity comment = commentService.addComment(displayId, request);
        return ResponseEntity.created(URI.create("/api/tickets/" + displayId + "/comments/" + comment.getId()))
                .body(ticketMapper.toComment(comment));
    }
}
