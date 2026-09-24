package com.atms.ticket.application;

public class ResolutionNotesRequiredException extends RuntimeException {

    public ResolutionNotesRequiredException() {
        super("Resolution notes are required when transitioning to RESOLVED");
    }
}
