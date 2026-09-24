# State Machine: Ticket Lifecycle

**Date**: 2026-09-25  
**Authority**: Server-only (`TransitionService` in `ticket` module)

## States

| State | Terminal | Notes |
|-------|----------|--------|
| OPEN | No | Initial state on create |
| IN_PROGRESS | No | |
| RESOLVED | No | Requires non-empty resolution notes on entry |
| CLOSED | Yes | |
| CANCELLED | Yes | |

## Allowed transitions

| From | To | Preconditions |
|------|-----|----------------|
| OPEN | IN_PROGRESS | — |
| IN_PROGRESS | RESOLVED | `resolutionNotes` non-blank |
| RESOLVED | CLOSED | — |
| OPEN | CANCELLED | — |
| IN_PROGRESS | CANCELLED | — |

## Explicitly forbidden (examples)

- `CLOSED` → `OPEN`
- `RESOLVED` → `OPEN`
- `CANCELLED` → `OPEN`
- Any transition from `CLOSED` or `CANCELLED`
- `OPEN` → `RESOLVED` (must pass through `IN_PROGRESS` unless product later allows—**not in spec**)
- `RESOLVED` → `CANCELLED`
- Transition to `RESOLVED` without resolution notes

## HTTP mapping

- Status changes via `PATCH /api/tickets/{displayId}/status` (or dedicated transition endpoint) with body `{ "status": "...", "resolutionNotes": "..." }` when target is `RESOLVED`.
- Illegal transition → **409** `ProblemDetail` type `ticket/invalid-transition`.
- Resolution notes-only update while `RESOLVED` → `PATCH /api/tickets/{displayId}/resolution-notes`.

## Side effects on transition

| Transition | Knowledge index |
|------------|-----------------|
| → RESOLVED | Full re-index (resolution notes appear) |
| → CLOSED | Re-index / metadata refresh per [rag-ingestion.md](./rag-ingestion.md) |
| → CANCELLED | Metadata update on chunks (status) |
| Other allowed | Metadata update if status field in JSONB metadata |

## Test matrix

Integration tests must cover:

- All **5 allowed** transitions (happy path).
- Representative **forbidden** transitions (at minimum spec examples plus one from each terminal state).
- `IN_PROGRESS` → `RESOLVED` without notes → reject.
- Resolution notes edit only in `RESOLVED`.

See [test-strategy.md](./test-strategy.md).
