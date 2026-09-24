# Implementation Plan: Support Ticket Management with Grounded Q&A

**Branch**: `001-support-ticket-rag` | **Date**: 2026-09-25 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/001-support-ticket-rag/spec.md` and [technology-requirements.md](./technology-requirements.md)

## Summary

Deliver a full-stack support ticket system (CRUD, keyword search, status filter, server-enforced lifecycle) plus grounded natural-language Q&A over ticket history. Backend uses **Java 25**, **Spring Boot 4.1.x**, **Spring AI**, **Maven**, **PostgreSQL + PGVector**, **Liquibase**, **JPA**, **Bean Validation**. Frontend uses **React + TypeScript** (`strict`). Architecture is **package-by-feature** with **ticket** and **rag** modules isolated; deterministic ticket logic never depends on probabilistic AI code paths. RAG flow: ticket → canonical knowledge document → chunks → embeddings → PGVector → similarity retrieval → grounding guard → LLM → answer + display ticket id sources. **Top-k** and **similarity threshold** are externalized. Re-index runs **reliably in the same transaction boundary** as knowledge-bearing ticket writes (commit only after vectors updated or marked failed with rollback). **Integration tests** cover state transitions and retrieval; **Testcontainers** for PostgreSQL/PGVector where appropriate.

## Technical Context

**Language/Version**: Java 25 (backend), TypeScript strict (frontend)

**Primary Dependencies**: Spring Boot 4.1.x, Spring AI (embedding + chat), Spring Data JPA, Spring Validation, Liquibase, MapStruct, Lombok, springdoc-openapi, React, Vite (or equivalent SPA toolchain)

**Storage**: PostgreSQL 16+ with `pgvector` extension; relational tables for tickets/comments; vector table(s) for chunk embeddings + JSON metadata

**Testing**: JUnit 5, Mockito (narrow), Spring Boot Test, Testcontainers (PostgreSQL + pgvector). Default CI: FSM, list search, RAG ingestion/retrieval integration tests plus chunker/builder unit tests (**SC-002**, **SC-003**, **SC-010**). Probabilistic `rag-eval` profile optional per [test-strategy.md](./test-strategy.md)—not a v1 CI gate.

**Target Platform**: JVM server (local/dev/prod), static SPA served separately or via reverse proxy

**Project Type**: Web application (backend API + React UI)

**Performance Goals**: Interactive ticket UI (list/detail) responsive under team-scale data (thousands of tickets); ask latency dominated by embedding + LLM—document expected latency ranges in [architecture.md](./architecture.md) / [quickstart.md](./quickstart.md) during implementation (**T060**, **T064**); no hard SLA in v1

**Constraints**: No secrets in repo; OpenAPI source of truth for HTTP; RFC 9457 errors; constitution Principles I–IV and Development Workflow gates; single retrieval→generate per ask (no agent)

**Scale/Scope**: Single-team support desk; one PostgreSQL database; no multi-tenant auth in v1 unless added later

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle / gate | Plan compliance |
|------------------|-----------------|
| I Framework-first backend | Spring Boot 4.1.x, Spring AI, declarative config, starters |
| II Modular architecture, Lombok & MapStruct | Package-by-feature; constructor injection; MapStruct DTO mapping |
| III Grounded RAG & vector index hygiene | Guard + citations; configurable top-k/threshold; re-index on knowledge mutations |
| IV Spec-before-code, security & official sources | Spec, plan, tasks reviewed before code; no secrets in repo |
| Development Workflow §2 (FSM tests) | State-machine integration test matrix; `TransitionService` sole transition authority |
| API & Contract Documentation | [contracts/openapi.yaml](./contracts/openapi.yaml) + [api-contract.md](./api-contract.md); interface JavaDoc on public application ports |
| Technology stack (constitution) | Java 25, Boot 4.1.x, PostgreSQL/PGVector, React TS strict |

**Post–Phase 1 re-check**: PASS — data model and contracts preserve FSM, ask response shape, and module boundaries.

## Project Structure

### Documentation (this feature)

```text
specs/001-support-ticket-rag/
├── plan.md
├── research.md
├── data-model.md
├── architecture.md
├── api-contract.md
├── state-machine.md
├── rag-ingestion.md
├── rag-api-contract.md
├── evaluation-strategy.md
├── ui-flow.md
├── test-strategy.md
├── quickstart.md
├── contracts/openapi.yaml
├── spec.md
├── technology-requirements.md
└── tasks.md              # implementation task list (/speckit-tasks)
```

### Source Code (repository root)

```text
backend/
├── pom.xml
├── src/main/java/com/atms/
│   ├── AtmsApplication.java
│   ├── shared/
│   │   ├── config/
│   │   ├── error/              # ProblemDetail, ControllerAdvice
│   │   └── web/
│   ├── ticket/                 # deterministic domain — no Spring AI imports
│   │   ├── api/                # TicketController, DTOs
│   │   ├── application/        # TicketService, TransitionService, CommentService
│   │   ├── domain/             # Ticket, Comment, TicketStatus, events
│   │   └── infrastructure/     # JPA entities, repositories, Liquibase refs
│   └── rag/                    # AI/RAG only
│       ├── api/                # AskController
│       ├── application/        # Ingestion, retrieval, grounding, AskService
│       ├── domain/             # KnowledgeDocument, Chunk, RetrievalResult
│       └── infrastructure/     # PGVector store, Spring AI clients
├── src/main/resources/
│   ├── application.yml
│   └── db/changelog/
└── src/test/java/com/atms/
    ├── ticket/integration/     # FSM Testcontainers tests
    └── rag/integration/        # retrieval + ingestion tests

frontend/
├── package.json
├── tsconfig.json               # strict: true
└── src/
    ├── api/                    # typed client from OpenAPI
    ├── features/
    │   ├── tickets/
    │   └── ask/
    ├── components/
    └── App.tsx
```

**Structure Decision**: Monorepo with `backend/` and `frontend/`; **package-by-feature** inside backend (`ticket` vs `rag`) with `shared` for cross-cutting web/error/config only. Ticket module publishes domain events or calls a narrow `KnowledgeIndexPort` interface implemented in `rag` to avoid circular dependencies while keeping deterministic tests free of LLM mocks in ticket tests.

## Complexity Tracking

No constitution violations requiring justification.

## Phase 0 & Phase 1 Outputs

| Artifact | Path | Status |
|----------|------|--------|
| Research | [research.md](./research.md) | Complete |
| Data model | [data-model.md](./data-model.md) | Complete |
| Architecture | [architecture.md](./architecture.md) | Complete |
| API contract | [api-contract.md](./api-contract.md), [contracts/openapi.yaml](./contracts/openapi.yaml) | Complete |
| State machine | [state-machine.md](./state-machine.md) | Complete |
| RAG ingestion | [rag-ingestion.md](./rag-ingestion.md) | Complete |
| RAG API | [rag-api-contract.md](./rag-api-contract.md) | Complete |
| Evaluation | [evaluation-strategy.md](./evaluation-strategy.md) | Complete |
| UI flow | [ui-flow.md](./ui-flow.md) | Complete |
| Test strategy | [test-strategy.md](./test-strategy.md) | Complete |
| Quickstart | [quickstart.md](./quickstart.md) | Complete |

**Next**: `/speckit-implement` per [tasks.md](./tasks.md).
