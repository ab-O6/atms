# Test Strategy

**Date**: 2026-09-25  
**Standards**: `.cursor/rules/testing.mdc`

## Pyramid

| Layer | Scope | Tools |
|-------|--------|-------|
| Unit | FSM rules, chunker, canonical doc builder, citation validator | JUnit 5, no Spring |
| Integration | Repositories, FSM HTTP, ingestion+retrieval | `@SpringBootTest`, Testcontainers PostgreSQL+pgvector |
| Frontend | Form validation UX, error display | React Testing Library (optional v1) |
| RAG eval | Probabilistic quality | Separate Maven profile |

## State machine (mandatory gate)

- Class: `TicketStateMachineIntegrationTest` (Testcontainers).
- Parameterized: all allowed transitions succeed (HTTP 200/204).
- Parameterized: forbidden transitions return 409.
- Case: resolve without notes → 409.
- Case: edit resolution notes in `RESOLVED` → success; in `OPEN` → 409.

## Retrieval & ingestion (mandatory gate)

- `RagIngestionIntegrationTest`: create ticket + comment → chunks exist with metadata `ticketId` = display id.
- `RagRetrievalIntegrationTest`: FakeEmbeddingModel + known vectors → ask returns expected `sources` or `noMatch`.
- Re-index: update description → old chunk text absent, new present in same transaction test.

## Testcontainers

- Image: PostgreSQL with pgvector (e.g. `pgvector/pgvector:pg16`).
- `@DynamicPropertySource` for JDBC URL.
- Reuse container per test class where possible.

## What not to test in main CI

- Live cloud LLM responses (use mock `ChatModel` except `rag-eval` profile).
- Exact embedding floats from production models.

## Coverage expectations

- Transition service and `TransitionService`/`TicketController` status paths: high branch coverage on FSM.
- Ask path: grounding guard branches (empty vs non-empty retrieval).

## Evidence

- CI runs `./mvnw test` on backend; document frontend test command in [quickstart.md](./quickstart.md).
