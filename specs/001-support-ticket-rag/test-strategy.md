# Test Strategy

**Date**: 2026-09-25  
**Standards**: `.cursor/rules/testing.mdc`

## Pyramid

| Layer | Scope | Tools |
|-------|--------|-------|
| Unit | Chunker, canonical doc builder (mandatory v1); FSM rules unit tests optional | JUnit 5, no Spring |
| Integration | List search HTTP (**SC-002**), FSM HTTP (**SC-003**), ingestion+retrieval (**SC-010**) | `@SpringBootTest`, Testcontainers PostgreSQL+pgvector |
| Frontend | Form validation UX, error display (**SC-004**) | React Testing Library (optional v1; manual quickstart acceptable) |
| RAG eval | Probabilistic quality | Separate Maven profile; **optional**, not v1 CI gate (**SC-010**) |

## List search (mandatory gate, SC-002)

- Class: `TicketListSearchIntegrationTest` (Testcontainers).
- Keyword `q`: case-insensitive substring on title, description, display ticket id only (not comments, resolution notes, category).
- Status filter returns only matching status; empty `q`/filter combinations return empty set when no matches.

## State machine (mandatory gate, SC-003)

- Class: `TicketStateMachineIntegrationTest` (Testcontainers).
- Optional: pure unit tests on transition rules; **merge gate** is this integration suite (constitution Development Workflow §2).
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

### Remote Docker (`docker context` over SSH)

Testcontainers uses **docker-java**, which supports `unix://` and `tcp://` only — not `ssh://` from the Docker CLI context. Options:

1. **Tunnel** (see [quickstart.md](./quickstart.md) §6): `./scripts/backend-integration-test.sh` maps the remote daemon to `tcp://127.0.0.1:2375` and sets `DOCKER_API_VERSION=1.44` for current engines.
2. **External JDBC**: set `ATMS_IT_JDBC_URL` (and optional `ATMS_IT_DB_USER` / `ATMS_IT_DB_PASSWORD`) to a reachable Postgres with pgvector; container startup is skipped (`PostgresTestcontainerExtension`).

Integration tests compile to **Java 21 bytecode** (`testRelease` in `pom.xml`) while main code targets Java 25, so Spring Boot 3.4 can load `@SpringBootTest` classes on a JDK 25 runtime.

## What not to test in main CI

- Live cloud LLM responses (use mock `ChatModel` except `rag-eval` profile).
- Exact embedding floats from production models.

## Coverage expectations

- Transition service and `TransitionService`/`TicketController` status paths: high branch coverage on FSM.
- Ask path: grounding guard branches (empty vs non-empty retrieval).

## Evidence

- CI runs `./mvnw test` on backend; document frontend test command in [quickstart.md](./quickstart.md).
