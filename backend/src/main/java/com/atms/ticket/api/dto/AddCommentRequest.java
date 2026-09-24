package com.atms.ticket.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(
        @NotBlank String body,
        @NotBlank @Size(max = 120) String author) {}
