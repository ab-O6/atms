package com.atms.ticket.application;

/**
 * Port for refreshing searchable ticket knowledge after persistence writes.
 *
 * <p>Implemented in the RAG module; ticket module depends only on this interface.
 */
public interface TicketKnowledgeIndexPort {

    /**
     * Rebuild or update vector index state for the given ticket snapshot.
     *
     * @param snapshot current ticket fields needed for canonical document and metadata
     */
    void reindex(TicketSnapshot snapshot);
}
