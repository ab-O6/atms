package com.atms.ticket.api;

import com.atms.ticket.api.dto.AddCommentRequest;
import com.atms.ticket.api.dto.CommentResponse;
import com.atms.ticket.api.dto.CreateTicketRequest;
import com.atms.ticket.api.dto.TicketResponse;
import com.atms.ticket.api.dto.TicketSummaryResponse;
import com.atms.ticket.api.dto.TransitionStatusRequest;
import com.atms.ticket.api.dto.UpdateResolutionNotesRequest;
import com.atms.ticket.api.dto.UpdateTicketRequest;
import com.atms.ticket.api.mapper.TicketMapper;
import com.atms.ticket.application.CommentService;
import com.atms.ticket.application.ResolutionNotesService;
import com.atms.ticket.application.TransitionService;
import com.atms.ticket.application.TicketService;
import com.atms.ticket.domain.TicketStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CommentService commentService;
    private final TransitionService transitionService;
    private final ResolutionNotesService resolutionNotesService;
    private final TicketMapper ticketMapper;

    public TicketController(
            TicketService ticketService,
            CommentService commentService,
            TransitionService transitionService,
            ResolutionNotesService resolutionNotesService,
            TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.commentService = commentService;
        this.transitionService = transitionService;
        this.resolutionNotesService = resolutionNotesService;
        this.ticketMapper = ticketMapper;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        TicketEntity created = ticketService.create(request);
        TicketResponse body = ticketMapper.toDetail(created);
        return ResponseEntity.created(URI.create("/api/tickets/" + created.getDisplayId())).body(body);
    }

    @GetMapping
    public List<TicketSummaryResponse> list(
            @RequestParam(required = false) String q, @RequestParam(required = false) TicketStatus status) {
        return ticketService.list(q, status).stream().map(ticketMapper::toSummary).toList();
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

    @PatchMapping("/{displayId}/status")
    public TicketResponse transitionStatus(
            @PathVariable String displayId, @Valid @RequestBody TransitionStatusRequest request) {
        TicketEntity entity =
                transitionService.transition(displayId, request.status(), request.resolutionNotes());
        return ticketMapper.toDetail(entity);
    }

    @PatchMapping("/{displayId}/resolution-notes")
    public TicketResponse updateResolutionNotes(
            @PathVariable String displayId, @Valid @RequestBody UpdateResolutionNotesRequest request) {
        TicketEntity entity = resolutionNotesService.update(displayId, request.resolutionNotes());
        return ticketMapper.toDetail(entity);
    }
}
