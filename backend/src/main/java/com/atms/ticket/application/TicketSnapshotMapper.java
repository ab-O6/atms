package com.atms.ticket.application;

import com.atms.ticket.infrastructure.persistence.TicketEntity;

final class TicketSnapshotMapper {

    private TicketSnapshotMapper() {}

    static TicketSnapshot fromEntity(TicketEntity entity) {
        return new TicketSnapshot(
                entity.getId(),
                entity.getDisplayId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPriority().name(),
                entity.getAssignee(),
                entity.getCategory(),
                entity.getStatus().name(),
                entity.getResolutionNotes());
    }
}
