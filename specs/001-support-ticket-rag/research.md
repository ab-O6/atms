# Research: 001-support-ticket-rag

**Date**: 2026-09-25

Resolves technical choices for implementation planning. No open **NEEDS CLARIFICATION** items remain.

## 1. Embedding model and chat model

**Decision**: Configure **Spring AI** `EmbeddingModel` and `ChatModel` via `application.yml` / environment (e.g. `spring.ai.openai.*` or `spring.ai.ollama.*`). Default **local dev**: Ollama with a small embedding model (e.g. `nomic-embed-text`) and a compact chat model; **CI retrieval tests** use Testcontainers + **stub/fake embedding** (fixed-dimension deterministic vectors) to avoid network LLM calls.

**Rationale**: Constitution and assessment require documented cost/latency/quality tradeoffs; externalizing provider keeps secrets out of repo and matches Spring AI idioms.

**Alternatives considered**: Hard-coded OpenAI only (rejected—poor offline dev); always live LLM in CI (rejected—flaky, costly).

## 2. Chunking strategy for ticket knowledge

**Decision**: **Paragraph-first** splitting on blank lines within each canonical knowledge document section (description block, each comment, resolution notes); oversize paragraphs split by **fixed character window** (~800 chars) with **100 char overlap**; store chunk index and `ticketId` metadata on every vector row.

**Rationale**: Ticket text is mostly short prose and comments; paragraph boundaries preserve semantics better than arbitrary splits alone (TR-005).

**Alternatives considered**: Pure fixed-size only (rejected—splits mid-sentence); semantic chunking via extra LLM call (rejected—cost/latency for v1).

## 3. Canonical knowledge document

**Decision**: One **canonical text document per ticket per re-index version**, built from labeled sections:

```text
[DESCRIPTION]\n{description}\n[COMMENT author={assignee} at={createdAt}]\n{text}\n...
[RESOLUTION]\n{resolutionNotes}
```

Title is **not** embedded (TR-001); title changes still trigger re-index only if description/comments/resolution change—title-only updates re-index **metadata columns** on existing chunks without re-embedding if embed text unchanged (optimization allowed; full re-embed acceptable for v1 simplicity).

**Rationale**: Aligns with TR-001 and clarifications; structured labels help LLM grounding.

## 4. Re-index reliability

**Decision**: **Synchronous transactional re-index** in the same `@Transactional` application service method that commits ticket mutations affecting knowledge: (1) persist ticket/comment rows, (2) delete prior vectors for `ticket.id`, (3) rebuild canonical doc + chunks + embeddings, (4) insert vectors. Failure rolls back ticket write.

**Rationale**: User requested transactional/reliable refresh; guarantees next ask after successful API response sees new content (closes deferred clarify item).

**Alternatives considered**: Async queue (rejected for v1—stale window harder to test); eventual consistency (rejected—conflicts with SC-007).

## 5. Knowledge-bearing mutation triggers

**Decision**: Re-index on: create ticket (initial index), add comment, update description/title (metadata always; embed if text changed), update priority/assignee/category (metadata refresh), status → `RESOLVED` (resolution notes), edit resolution notes while `RESOLVED`, status → `CLOSED`. No re-index on pure status moves that do not change embed text except metadata (`IN_PROGRESS`, `CANCELLED`)—still update chunk **metadata** (status) in place.

**Rationale**: FR-019, TR-003, TR-014; keeps status filter in retrieval metadata accurate.

## 6. Retrieval and grounding guard

**Decision**: PGVector similarity search with externalized `atms.rag.top-k` and `atms.rag.similarity-threshold`. If **no chunk** exceeds threshold → skip LLM; return `noMatch: true` with template message. If chunks found → build prompt with **only** chunk text + metadata; system instruction forbids external knowledge; **sources** = distinct display ticket ids from chunk metadata; post-validate every cited id ∈ retrieved set.

**Rationale**: TR-007–TR-012, FR-015–FR-016.

## 7. Display ticket id generation

**Decision**: PostgreSQL sequence `ticket_display_id_seq`; format `TKT-%04d` (e.g. `TKT-1001`); unique constraint on `display_id`; internal `UUID` primary key.

**Rationale**: Clarified spec; human-readable citations.

## 8. Integration testing approach

**Decision**: **Testcontainers** `postgresql` image with `pgvector/pgvector:pg16` (or init script enabling extension); Spring Boot `@SpringBootTest` + `@Transactional` rollback for FSM matrix; RAG integration tests with **FakeEmbeddingModel** (hash-based vectors) for deterministic similarity; optional tagged manual suite with real Ollama.

**Rationale**: User preference; separates deterministic CI from probabilistic eval.

## 9. Module integration (ticket ↔ rag)

**Decision**: `ticket` defines port `TicketKnowledgeIndexPort` with `reindex(TicketSnapshot)` and `updateMetadata(TicketSnapshot)`; `rag` provides adapter. Ticket services call port after successful persistence—no direct JPA access from rag to ticket tables except read-only snapshot query in adapter.

**Rationale**: Package-by-feature separation while preserving transactional re-index orchestration in ticket application service (or shared orchestrator in ticket that invokes rag adapter within transaction).
