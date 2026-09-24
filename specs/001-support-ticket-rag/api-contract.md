# API Contract (Tickets)

**Date**: 2026-09-25  
**OpenAPI source**: [contracts/openapi.yaml](./contracts/openapi.yaml)  
**Standards**: RFC 9457 `ProblemDetail`, Bean Validation, resource paths under `/api`

## Resources

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/tickets` | Create ticket |
| GET | `/api/tickets` | List with optional `status`, `q` (keyword) |
| GET | `/api/tickets/{displayId}` | Ticket detail + comments |
| PATCH | `/api/tickets/{displayId}` | Update title, description, priority, assignee |
| POST | `/api/tickets/{displayId}/comments` | Add comment |
| PATCH | `/api/tickets/{displayId}/status` | Status transition (FSM) |
| PATCH | `/api/tickets/{displayId}/resolution-notes` | Update notes while `RESOLVED` |

Paths use **display id** (`TKT-1001`) in URL for user-facing consistency.

## DTO highlights

**CreateTicketRequest**: `title`, `description`, `priority`, `assignee`, optional `category`

**TicketResponse**: `displayId`, `title`, `description`, `priority`, `assignee`, `category`, `status`, `resolutionNotes`, `createdAt`, `updatedAt`, `comments[]`

**TransitionStatusRequest**: `status`, optional `resolutionNotes` (required when status is `RESOLVED`)

**List query**:

- `status` — exact match filter
- `q` — case-insensitive substring on title, description, displayId (FR-006a)

## Error types (stable `type` slugs)

| type | Status | Use |
|------|--------|-----|
| `validation/failed` | 400 | Bean Validation |
| `ticket/not-found` | 404 | Unknown display id |
| `ticket/invalid-transition` | 409 | FSM violation |
| `ticket/resolution-notes-required` | 409 | Missing notes on resolve |
| `ticket/resolution-notes-not-editable` | 409 | Notes edit wrong status |

## AI endpoint

See [rag-api-contract.md](./rag-api-contract.md) for `POST /api/ai/ask`.
