package com.atms.ticket.infrastructure.persistence;

import com.atms.ticket.domain.TicketStatus;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketRepository
        extends JpaRepository<TicketEntity, UUID>, JpaSpecificationExecutor<TicketEntity> {

    @EntityGraph(attributePaths = {"comments"})
    Optional<TicketEntity> findByDisplayId(String displayId);

    default List<TicketEntity> search(String q, TicketStatus status) {
        Specification<TicketEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("displayId")), pattern)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return findAll(spec, Sort.by(Sort.Direction.DESC, "updatedAt"));
    }
}
