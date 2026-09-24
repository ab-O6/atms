package com.atms.ticket.infrastructure.persistence;

import com.atms.ticket.domain.TicketStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {

    Optional<TicketEntity> findByDisplayId(String displayId);

    @Query(
            """
            SELECT t FROM TicketEntity t
            WHERE (:status IS NULL OR t.status = :status)
            AND (
              :q IS NULL OR :q = '' OR
              LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(t.displayId) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            ORDER BY t.updatedAt DESC
            """)
    List<TicketEntity> search(@Param("q") String q, @Param("status") TicketStatus status);
}
