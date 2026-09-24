package com.atms.ticket.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DisplayIdGenerator {

    private final JdbcTemplate jdbcTemplate;

    public DisplayIdGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @return next display id using {@code ticket_display_id_seq} formatted as {@code TKT-%04d}
     */
    public String nextDisplayId() {
        Long seq = jdbcTemplate.queryForObject("SELECT nextval('ticket_display_id_seq')", Long.class);
        return String.format("TKT-%04d", seq);
    }
}
