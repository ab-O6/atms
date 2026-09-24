# Data Model: 001-support-ticket-rag

**Date**: 2026-09-25  
**Spec**: [spec.md](./spec.md)

## Entity-relationship overview

```text
Ticket (1) ──< Comment
Ticket (1) ──< TicketVectorChunk (rag index, by internal ticket UUID)
```

Internal `ticket.id` (UUID) is the FK everywhere. **Display id** (`TKT-####`) is unique, user-facing, and stored in RAG metadata as `ticketId`.

## Ticket

| Field | Type | Rules |
|-------|------|--------|
| id | UUID | PK, generated |
| display_id | VARCHAR | UNIQUE, NOT NULL, server-generated `TKT-%04d` |
| title | VARCHAR(200) | NOT NULL |
| description | TEXT | NOT NULL |
| priority | ENUM | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| assignee | VARCHAR(120) | NOT NULL (free-text assignee name for v1) |
| category | VARCHAR(80) | NOT NULL, default `''`; set at create only |
| status | ENUM | `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED` |
| resolution_notes | TEXT | NULL until `RESOLVED`; required non-empty on transition to `RESOLVED` |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

**Indexes**: `display_id` (unique), `status`, GIN/trigram or `ILIKE` support on `title`, `description`, `display_id` for list keyword search (implementation choice: `pg_trgm` or application-level filter for v1).

## Comment

| Field | Type | Rules |
|-------|------|--------|
| id | UUID | PK |
| ticket_id | UUID | FK → ticket.id, NOT NULL |
| body | TEXT | NOT NULL |
| author | VARCHAR(120) | NOT NULL |
| created_at | TIMESTAMPTZ | NOT NULL |

Ordered by `created_at ASC` when building canonical knowledge document.

## TicketVectorChunk (PGVector)

| Field | Type | Rules |
|-------|------|--------|
| id | UUID | PK |
| ticket_id | UUID | FK → ticket.id, NOT NULL |
| chunk_index | INT | NOT NULL, 0..n-1 per ticket |
| content | TEXT | Chunk embeddable text |
| embedding | VECTOR(d) | Dimension matches configured model |
| metadata | JSONB | `ticketId` (display), `status`, `priority`, `assignee`, `category` |
| indexed_at | TIMESTAMPTZ | NOT NULL |

**Uniqueness**: `(ticket_id, chunk_index)` per index generation. On re-index: delete all rows for `ticket_id` then insert fresh set.

## Enumerations

### TicketStatus

Terminal states: `CLOSED`, `CANCELLED` (no outbound transitions).

### Priority

`LOW`, `MEDIUM`, `HIGH`, `CRITICAL` — used in list UI and RAG metadata filtering (future); not required for v1 ask filtering unless eval fixtures need it.

## Validation summary (Bean Validation + domain)

- Create: title, description, priority, assignee required; category optional.
- Update: title, description, priority, assignee; not category.
- Transition to `RESOLVED`: `resolutionNotes` non-blank.
- Resolution notes patch: only when `status == RESOLVED`.
- Comments: body non-blank.

## Liquibase

- Changesets under `backend/src/main/resources/db/changelog/`
- Enable `pgvector` extension in first changeset
- Sequence `ticket_display_id_seq` for display id
