package com.atms.ticket.application;

public class ResolutionNotesNotEditableException extends RuntimeException {

    public ResolutionNotesNotEditableException() {
        super("Resolution notes can only be edited while the ticket is RESOLVED");
    }
}
