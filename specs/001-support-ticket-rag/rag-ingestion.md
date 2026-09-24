# RAG Ingestion

**Date**: 2026-09-25  
**Architecture**: [architecture.md](./architecture.md)

## Pipeline

```text
Ticket (+ comments) → CanonicalKnowledgeDocumentBuilder → Chunker → EmbeddingModel → PGVector rows
```

## Canonical document content (embeddable)

Include:

- Description
- All comments (chronological), with author/timestamp labels in text
- Resolution notes (when present)

Exclude from embeddable text:

- Title (list search only per FR-006a; not in TR-001 embed set)

## Metadata (per chunk, JSONB)

| Key | Source |
|-----|--------|
| ticketId | `ticket.display_id` |
| status | `ticket.status` |
| priority | `ticket.priority` |
| assignee | `ticket.assignee` |
| category | `ticket.category` |

## Triggers

| Event | Action |
|-------|--------|
| Ticket created | Full re-index |
| Comment added | Full re-index |
| Description updated | Full re-index |
| Title / priority / assignee updated | Metadata update on all chunks; re-embed if description/comments/resolution unchanged (v1: full re-index acceptable) |
| Status → RESOLVED | Full re-index (resolution notes required) |
| Resolution notes updated (RESOLVED) | Full re-index |
| Status → CLOSED | Full re-index per TR-003 |
| Status → CANCELLED or IN_PROGRESS | Metadata status update minimum |

All triggered synchronously in the **same transaction** as the ticket write ([research.md](./research.md)#4).

## Chunking

- Paragraph split on `\n\n+`
- Segments longer than ~800 characters: sliding window with ~100 character overlap
- Store `chunk_index` monotonic per ticket

## Failure handling

- Embedding or vector insert failure → transaction rollback; ticket comment/create/update returns 500/409 as appropriate; no partial index state committed.

## Deletion

- Ticket delete (if implemented) → delete all `TicketVectorChunk` for `ticket_id`. **Out of scope** if spec has no delete—omit delete API in v1.
