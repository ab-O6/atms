package com.atms.ticket.api.mapper;

import com.atms.ticket.api.dto.CommentResponse;
import com.atms.ticket.api.dto.TicketResponse;
import com.atms.ticket.api.dto.TicketSummaryResponse;
import com.atms.ticket.infrastructure.persistence.CommentEntity;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketSummaryResponse toSummary(TicketEntity entity);

    @Mapping(target = "comments", expression = "java(mapComments(entity))")
    TicketResponse toDetail(TicketEntity entity);

    CommentResponse toComment(CommentEntity entity);

    default List<CommentResponse> mapComments(TicketEntity entity) {
        return entity.getComments().stream()
                .sorted(Comparator.comparing(CommentEntity::getCreatedAt))
                .map(this::toComment)
                .toList();
    }
}
