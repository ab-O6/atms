# Architecture: 001-support-ticket-rag

**Date**: 2026-09-25  
**Plan**: [plan.md](./plan.md) | **Research**: [research.md](./research.md)

## System context

```text
┌─────────────┐     HTTPS/JSON      ┌──────────────────────────────────────┐
│ React SPA   │ ◄──────────────────►│ Spring Boot API (Java 25, Boot 4.1.x) │
│ TypeScript  │                     │  ┌────────────┐    ┌───────────────┐  │
└─────────────┘                     │  │ ticket     │    │ rag           │  │
                                    │  │ module     │───►│ module        │  │
                                    │  └─────┬──────┘    └───────┬───────┘  │
                                    │        │                   │          │
                                    │        ▼                   ▼          │
                                    │   PostgreSQL (relational)  PGVector     │
                                    └──────────────────────────────────────┘
```

## Layering (package-by-feature)

| Module | Responsibility | Must NOT |
|--------|----------------|----------|
| `ticket` | CRUD, search, filter, FSM, comments, display id | Import Spring AI chat/embedding APIs |
| `rag` | Canonical doc, chunk, embed, store, retrieve, ask, grounding | Own ticket transition rules |
| `shared` | ProblemDetail, global exception handler, security placeholders | Business rules |

**Integration**: `TicketKnowledgeIndexPort` (interface in `ticket.application` or `shared.contracts`) implemented by `rag.infrastructure.TicketKnowledgeIndexAdapter`. Ticket write services invoke the port **inside the same transaction** after entity persist.

## Request flows

### Ticket write + re-index

1. Controller validates DTO → application service.
2. Domain transition / validation (FSM).
3. JPA save ticket and/or comment.
4. `indexPort.reindex(snapshot)` deletes old vectors, embeds new chunks, inserts rows.
5. Commit; on any failure, full rollback.

### Ask (read-mostly)

1. `POST /api/ai/ask` → `AskService`.
2. Embed question; PGVector top-k with threshold filter.
3. **Grounding guard**: empty → `noMatch` response without LLM.
4. Else prompt LLM with retrieved chunks only; map response + `sources` from retrieval metadata.
5. Validate citations ⊆ retrieved display ids.

## Configuration (externalized)

```yaml
atms:
  rag:
    top-k: 5
    similarity-threshold: 0.75
spring:
  ai:
    # provider-specific: ollama or openai via env
```

Document defaults and safe ranges in ops README; justify chunking and embedding model here and in [rag-ingestion.md](./rag-ingestion.md).

## Chunking and embedding (summary)

See [research.md](./research.md): paragraph-first + fixed window fallback; embedding model via Spring AI configuration. Dimension in Liquibase must match active model or use migration when model changes.

## Security (v1)

Assessment does not mandate auth; plan assumes **no auth** for local/assessment demo with **CORS** restricted in dev. Do not commit API keys; use env vars for cloud models.

## Observability

Log retrieval: k, threshold, hit count, display ids (not embedding vectors). Structured logs for re-index duration and chunk counts.

## Documentation map

| Topic | Document |
|-------|----------|
| HTTP tickets | [api-contract.md](./api-contract.md) |
| HTTP ask | [rag-api-contract.md](./rag-api-contract.md) |
| FSM | [state-machine.md](./state-machine.md) |
| Ingestion | [rag-ingestion.md](./rag-ingestion.md) |
| Tests | [test-strategy.md](./test-strategy.md) |
| UI | [ui-flow.md](./ui-flow.md) |
| RAG quality | [evaluation-strategy.md](./evaluation-strategy.md) |
