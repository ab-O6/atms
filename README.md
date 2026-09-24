# ATMS — Support Ticket Management with Grounded Q&A

ATMS (Automated Ticket Management System) is a full-stack support desk application: staff manage tickets through a React UI and REST API, with a planned **retrieval-augmented generation (RAG)** layer that answers natural-language questions over ticket history using only retrieved context and cited display ticket IDs.

The product is specified and evolved using **Spec Kit** artifacts under [`specs/001-support-ticket-rag/`](specs/001-support-ticket-rag/). Engineering rules live in [`.specify/memory/constitution.md`](.specify/memory/constitution.md).

## Features

| Capability | Status | Notes |
|------------|--------|--------|
| Create, list, view, and update tickets | **Shipped** | Display IDs (`TKT-0001`), comments, persistence via PostgreSQL |
| Keyword search and status filter | **Shipped** | `GET /api/tickets?q=…&status=…` (title, description, display ID; case-insensitive substring) |
| Server-enforced ticket lifecycle (FSM) | **Shipped** | Five allowed transitions; resolution notes required to resolve |
| Grounded ask (`POST /api/ai/ask`) | **Planned** | See [Phase 6 in tasks.md](specs/001-support-ticket-rag/tasks.md); index port is currently a no-op |
| Authentication | **Out of scope (v1)** | Local/demo use; CORS restricted to the Vite dev origin |

## Architecture

```text
┌─────────────┐     HTTPS/JSON      ┌──────────────────────────────────────┐
│ React SPA   │ ◄──────────────────►│ Spring Boot API                       │
│ TypeScript  │                     │  ┌────────────┐    ┌───────────────┐  │
└─────────────┘                     │  │ ticket     │    │ rag (planned) │  │
                                    │  │ module     │───►│ module        │  │
                                    │  └─────┬──────┘    └───────┬───────┘  │
                                    │        │                   │          │
                                    │        ▼                   ▼          │
                                    │   PostgreSQL (relational)  PGVector     │
                                    └──────────────────────────────────────┘
```

Backend code is organized **by feature**:

- **`ticket`** — CRUD, search, comments, FSM (`TransitionService` is the sole transition authority). Must not import Spring AI chat/embedding APIs.
- **`rag`** — Canonical documents, chunking, embeddings, PGVector retrieval, ask endpoint, grounding guard (implementation in progress).
- **`shared`** — RFC 9457 `ProblemDetail` handling, CORS, OpenAPI configuration.

Ticket writes call `TicketKnowledgeIndexPort.reindex(...)` in the same transaction as persistence. Today that port is wired to a **no-op adapter** until the RAG module replaces it.

Detailed design: [`specs/001-support-ticket-rag/architecture.md`](specs/001-support-ticket-rag/architecture.md).

### Ticket lifecycle

| State | Terminal |
|-------|----------|
| `OPEN` | No (initial) |
| `IN_PROGRESS` | No |
| `RESOLVED` | No (resolution notes required on entry) |
| `CLOSED` | Yes |
| `CANCELLED` | Yes |

Allowed transitions: `OPEN → IN_PROGRESS`, `IN_PROGRESS → RESOLVED`, `RESOLVED → CLOSED`, `OPEN → CANCELLED`, `IN_PROGRESS → CANCELLED`. All other transitions are rejected with **409** and a stable problem `type` (for example `ticket/invalid-transition`).

Full matrix: [`specs/001-support-ticket-rag/state-machine.md`](specs/001-support-ticket-rag/state-machine.md).

## Technology stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot 3.4.x, Spring Data JPA, Bean Validation, Liquibase, MapStruct, Lombok |
| AI (planned) | Spring AI (OpenAI starter in `pom.xml`; PGVector store pending US4) |
| Database | PostgreSQL 16 with [pgvector](https://github.com/pgvector/pgvector) |
| API docs | springdoc-openapi — Swagger UI at `/swagger-ui.html`, OpenAPI at `/v3/api-docs` |
| Frontend | React 19, TypeScript (`strict`), Vite 6, React Router 7 |
| Tests | JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL + pgvector image) |

The feature plan targets Java 25 and Spring Boot 4.1.x; the running build is defined by [`backend/pom.xml`](backend/pom.xml).

## Prerequisites

- **Java 21+** and **Maven 3.9+** (or use `./mvnw` in `backend/`)
- **Node.js 20+** and npm
- **Docker** for local PostgreSQL and for Testcontainers integration tests

## Quick start

### 1. Start PostgreSQL

From the repository root:

```bash
docker compose up -d
```

Or run a one-off container (see [`specs/001-support-ticket-rag/quickstart.md`](specs/001-support-ticket-rag/quickstart.md)):

```bash
docker run -d --name atms-pg \
  -e POSTGRES_PASSWORD=atms \
  -e POSTGRES_USER=atms \
  -e POSTGRES_DB=atms \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

Default credentials: database `atms`, user `atms`, password `atms`.

### 2. Configure the backend

Override datasource and (when RAG is enabled) AI settings via environment variables. Do **not** commit API keys.

Example for local profile:

```bash
export SPRING_PROFILES_ACTIVE=local
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/atms
export SPRING_DATASOURCE_USERNAME=atms
export SPRING_DATASOURCE_PASSWORD=atms
# When ask/RAG is implemented:
# export SPRING_AI_OPENAI_API_KEY=...
```

[`backend/src/main/resources/application.yml`](backend/src/main/resources/application.yml) defines defaults including `atms.rag.top-k` and `atms.rag.similarity-threshold`. Chat and embedding are disabled in the default YAML until configured.

### 3. Run the API

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Liquibase applies schema on startup (including the `vector` extension and ticket tables). The server listens on **port 8080**.

### 4. Run the frontend

```bash
cd frontend
cp .env.example .env   # if needed
npm install
npm run dev
```

Set `VITE_API_BASE_URL=http://localhost:8080` in `.env` (default). The dev server uses **port 5173**; CORS allows that origin for `/api/**`.

### 5. Smoke test (tickets)

Create a ticket:

```bash
curl -s -X POST http://localhost:8080/api/tickets \
  -H 'Content-Type: application/json' \
  -d '{"title":"Payment failed","description":"Card declined","priority":"HIGH","assignee":"agent1","category":"payment"}'
```

List with filters:

```bash
curl -s 'http://localhost:8080/api/tickets?status=OPEN&q=payment'
```

Transition status (example):

```bash
curl -s -X PATCH http://localhost:8080/api/tickets/TKT-0001/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"IN_PROGRESS"}'
```

More examples: [`specs/001-support-ticket-rag/quickstart.md`](specs/001-support-ticket-rag/quickstart.md).

### 6. Ask endpoint (when RAG is complete)

```bash
curl -s -X POST http://localhost:8080/api/ai/ask \
  -H 'Content-Type: application/json' \
  -d '{"question":"Have we seen payment failures before?"}'
```

Expected shape: `answer`, `sources` (display ticket IDs), `noMatch`. Contract: [`specs/001-support-ticket-rag/rag-api-contract.md`](specs/001-support-ticket-rag/rag-api-contract.md).

## HTTP API overview

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/tickets` | Create ticket |
| `GET` | `/api/tickets` | List; query `status`, `q` |
| `GET` | `/api/tickets/{displayId}` | Detail and comments |
| `PATCH` | `/api/tickets/{displayId}` | Update title, description, priority, assignee |
| `POST` | `/api/tickets/{displayId}/comments` | Add comment |
| `PATCH` | `/api/tickets/{displayId}/status` | FSM transition |
| `PATCH` | `/api/tickets/{displayId}/resolution-notes` | Update notes while `RESOLVED` |
| `POST` | `/api/ai/ask` | Grounded Q&A (planned) |

**OpenAPI source of truth:** [`specs/001-support-ticket-rag/contracts/openapi.yaml`](specs/001-support-ticket-rag/contracts/openapi.yaml).

Errors use **RFC 9457** `ProblemDetail` (`application/problem+json`). The frontend client parses `detail` and problem `type` for user-visible messages.

## Testing

From `backend/`:

```bash
./mvnw test
```

This runs unit tests (for example `TransitionServiceTest`) and integration tests with Testcontainers:

- `TicketListSearchIntegrationTest` — keyword and status filter behavior
- `TicketStateMachineIntegrationTest` — FSM matrix and resolution-notes rules

**Remote Docker (SSH context):** Testcontainers does not support `DOCKER_HOST=ssh://…` directly. Use the helper script from the repo root:

```bash
./scripts/backend-integration-test.sh -Dtest=TicketListSearchIntegrationTest
```

Or point tests at an existing Postgres instance:

```bash
export ATMS_IT_JDBC_URL=jdbc:postgresql://host:5432/atms
export ATMS_IT_DB_USER=atms
export ATMS_IT_DB_PASSWORD=atms
cd backend && ./mvnw test
```

Optional probabilistic RAG evaluation (not a v1 CI gate):

```bash
./mvnw test -Prag-eval
```

Strategy: [`specs/001-support-ticket-rag/test-strategy.md`](specs/001-support-ticket-rag/test-strategy.md).

Frontend: `npm run build` runs TypeScript check and production bundle; add component tests per project conventions when introduced.

## Repository layout

```text
atms/
├── backend/                 # Spring Boot API
│   ├── src/main/java/com/atms/
│   │   ├── ticket/          # Tickets, FSM, persistence
│   │   ├── rag/             # RAG (in progress)
│   │   └── shared/          # Errors, web, config
│   └── src/main/resources/
│       ├── application.yml
│       └── db/changelog/    # Liquibase
├── frontend/                # React SPA
│   └── src/
│       ├── api/             # Typed HTTP client
│       └── features/tickets/
├── specs/001-support-ticket-rag/   # Spec, plan, tasks, contracts
├── docker-compose.yml       # PostgreSQL + pgvector
├── scripts/                 # Test helpers
└── graphify-out/            # Code knowledge graph (graphify)
```

Implementation checklist: [`specs/001-support-ticket-rag/tasks.md`](specs/001-support-ticket-rag/tasks.md).

## Documentation map

| Topic | Document |
|-------|----------|
| Product requirements | [`specs/001-support-ticket-rag/spec.md`](specs/001-support-ticket-rag/spec.md) |
| Implementation plan | [`specs/001-support-ticket-rag/plan.md`](specs/001-support-ticket-rag/plan.md) |
| Data model | [`specs/001-support-ticket-rag/data-model.md`](specs/001-support-ticket-rag/data-model.md) |
| Ticket HTTP contract | [`specs/001-support-ticket-rag/api-contract.md`](specs/001-support-ticket-rag/api-contract.md) |
| Ask HTTP contract | [`specs/001-support-ticket-rag/rag-api-contract.md`](specs/001-support-ticket-rag/rag-api-contract.md) |
| RAG ingestion | [`specs/001-support-ticket-rag/rag-ingestion.md`](specs/001-support-ticket-rag/rag-ingestion.md) |
| UI flows | [`specs/001-support-ticket-rag/ui-flow.md`](specs/001-support-ticket-rag/ui-flow.md) |
| Engineering constitution | [`.specify/memory/constitution.md`](.specify/memory/constitution.md) |

## Development workflow

1. **Requirement → spec → plan → tasks** before broad implementation ([spec-driven development](.cursor/rules/spec-driven-development.mdc)).
2. Change HTTP behavior only with updates to OpenAPI and contract docs.
3. Keep ticket logic deterministic and free of LLM dependencies; RAG stays in the `rag` package.
4. After substantive code changes, refresh the local knowledge graph: `graphify update .` (see [`.cursor/rules/graphify.mdc`](.cursor/rules/graphify.mdc)).

## Security notes (v1)

- No authentication in the default assessment/demo setup.
- Never commit secrets; use environment variables for cloud model API keys.
- RAG answers must cite display ticket IDs and return an honest **no-match** when retrieval is empty (constitution Principle III).

## License

No license file is present in this repository yet. Add one before public distribution.
