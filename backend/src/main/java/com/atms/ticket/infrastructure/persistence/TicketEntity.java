package com.atms.ticket.infrastructure.persistence;

import com.atms.ticket.domain.Priority;
import com.atms.ticket.domain.TicketStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ticket")
@Getter
@Setter
public class TicketEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "display_id", nullable = false, unique = true, length = 20)
    private String displayId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ticket_priority")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Priority priority;

    @Column(nullable = false, length = 120)
    private String assignee;

    @Column(nullable = false, length = 80)
    private String category = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ticket_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TicketStatus status;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> comments = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = TicketStatus.OPEN;
        }
        if (category == null) {
            category = "";
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
