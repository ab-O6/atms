package com.atms.ticket.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateResolutionNotesRequest(@NotBlank String resolutionNotes) {}
