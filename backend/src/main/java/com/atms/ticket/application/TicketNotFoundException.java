package com.atms.ticket.application;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String displayId) {
        super("Ticket not found: " + displayId);
    }
}
