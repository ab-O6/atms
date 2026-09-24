package com.atms.ticket.application;

public class InvalidTicketTransitionException extends RuntimeException {

    public InvalidTicketTransitionException(String message) {
        super(message);
    }
}
