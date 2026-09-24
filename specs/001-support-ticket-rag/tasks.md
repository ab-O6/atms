---
description: "Task list for Support Ticket Management with Grounded Q&A"
---

# Tasks: Support Ticket Management with Grounded Q&A

**Input**: Design documents from `/specs/001-support-ticket-rag/`

**Prerequisites**: [plan.md](./plan.md), [spec.md](./spec.md), [data-model.md](./data-model.md), [contracts/openapi.yaml](./contracts/openapi.yaml), [test-strategy.md](./test-strategy.md)

**Tests**: **Required** per spec **SC-002** (US2 list `q`/`status`), **SC-003** (US3 FSM matrix), **SC-010** (US4 deterministic integration + chunker/builder unit tests). Probabilistic **rag-eval** Maven profile is **out of v1 CI gate** (spec clarifications 2026-09-25). US1 uses manual/API smoke per [quickstart.md](./quickstart.md) unless extended later. **SC-004** (meaningful UI validation errors): manual quickstart acceptable for v1; RTL optional per [test-strategy.md](./test-strategy.md).

**Organization**: Tasks grouped by user story for independent increments (backend + frontend paths per [plan.md](./plan.md)).

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no blocking dependency on incomplete tasks in same phase)
- **[Story]**: US1–US4 maps to spec user stories

## Path Conventions

- **Backend**: `backend/src/main/java/com/atms/`, `backend/src/test/java/com/atms/`
- **Frontend**: `frontend/src/`
- **Migrations**: `backend/src/main/resources/db/changelog/`
- **OpenAPI**: `specs/001-support-ticket-rag/contracts/openapi.yaml` (source); springdoc serves at runtime

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Align plan/TR traceability with constitution and spec, then monorepo scaffolding

- [x] T001 [P] Align `plan.md` Constitution Check table with `.specify/memory/constitution.md` v1.1 (principles I–IV + workflow gates)
- [x] T002 [P] Fix FR references in `specs/001-support-ticket-rag/technology-requirements.md` traceability table (spec ends at FR-026; include **SC-010** / rag-eval deferral note)
- [x] T003 Create `backend/` and `frontend/` directory tree per [plan.md](./plan.md) Project Structure section
- [x] T004 Initialize `backend/pom.xml` with Java 25, Spring Boot 4.1.x BOM, Spring AI, JPA, Validation, Liquibase, MapStruct, Lombok, springdoc-openapi, JUnit 5, Testcontainers
- [x] T005 Initialize `frontend/package.json` with React, TypeScript, Vite; set `strict: true` in `frontend/tsconfig.json`
- [x] T006 [P] Add Maven Wrapper files under `backend/` (`mvnw`, `.mvn/wrapper/`)
- [x] T007 [P] Add `docker-compose.yml` at repo root with PostgreSQL 16 + pgvector per [quickstart.md](./quickstart.md)
- [x] T008 [P] Add `backend/src/main/resources/application.yml` and `application-local.yml` skeleton (datasource placeholders, no secrets)
- [x] T009 [P] Add `frontend/.env.example` with `VITE_API_BASE_URL=http://localhost:8080`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared persistence, errors, and index port stub—**blocks all user stories**

**⚠️ CRITICAL**: No user story work until this phase checkpoint passes

- [x] T010 Add Liquibase master `backend/src/main/resources/db/changelog/db.changelog-master.yaml`
- [x] T011 Add changeset enabling `vector` extension and `ticket_display_id_seq` in `backend/src/main/resources/db/changelog/001-extensions-and-sequences.yaml`
- [x] T012 Add `ticket` and `comment` tables per [data-model.md](./data-model.md) (`title` VARCHAR(200) NOT NULL, `assignee` VARCHAR(120) NOT NULL, `category` VARCHAR(80) NOT NULL default `''`, status/priority ENUMs) in `backend/src/main/resources/db/changelog/002-ticket-comment.yaml`
- [x] T013 Create `backend/src/main/java/com/atms/AtmsApplication.java` Spring Boot entrypoint
- [x] T014 [P] Implement RFC 9457 `ProblemDetail` handling in `backend/src/main/java/com/atms/shared/error/GlobalExceptionHandler.java`
- [x] T015 [P] Configure springdoc OpenAPI from `specs/001-support-ticket-rag/contracts/openapi.yaml` alignment in `backend/src/main/java/com/atms/shared/config/OpenApiConfig.java`
- [x] T016 Define `TicketKnowledgeIndexPort` in `backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java` with `reindex(TicketSnapshot)` (or equivalent)
- [x] T017 Implement no-op `TicketKnowledgeIndexPort` in `backend/src/main/java/com/atms/ticket/infrastructure/NoOpTicketKnowledgeIndexAdapter.java` until US4 replaces it
- [x] T018 [P] Add shared test helper `backend/src/test/java/com/atms/support/PostgresTestcontainerExtension.java` for PostgreSQL+pgvector image per [test-strategy.md](./test-strategy.md)
- [x] T019 [P] Create typed API client scaffold `frontend/src/api/client.ts` with base URL from env

**Checkpoint**: Application boots against local DB; migrations apply; no-op index port wired

---

## Phase 3: User Story 1 - Manage support tickets (Priority: P1) 🎯 MVP

**Goal**: CRUD tickets, list, detail, update (title/description/priority/assignee), comments, persistence across restart, server validation + UI errors

**Independent Test**: Create ticket → list/view → update fields and assignee → add comment → restart backend → data present; invalid input shows meaningful UI errors (spec User Story 1)

### Implementation for User Story 1

- [x] T020 [P] [US1] Add domain enums `TicketStatus`, `Priority` in `backend/src/main/java/com/atms/ticket/domain/`
- [x] T021 [P] [US1] Add JPA entities `TicketEntity`, `CommentEntity` matching [data-model.md](./data-model.md) in `backend/src/main/java/com/atms/ticket/infrastructure/persistence/`
- [x] T022 [US1] Add `TicketRepository`, `CommentRepository` in `backend/src/main/java/com/atms/ticket/infrastructure/persistence/`
- [x] T023 [US1] Implement `DisplayIdGenerator` using `ticket_display_id_seq` format `TKT-%04d` in `backend/src/main/java/com/atms/ticket/application/DisplayIdGenerator.java`
- [x] T024 [US1] Implement `TicketService` (create, getByDisplayId, list without search yet, update mutable fields only—**not** category) in `backend/src/main/java/com/atms/ticket/application/TicketService.java`
- [x] T025 [US1] Implement `CommentService` in `backend/src/main/java/com/atms/ticket/application/CommentService.java`
- [x] T026 [P] [US1] Add MapStruct mappers in `backend/src/main/java/com/atms/ticket/api/mapper/TicketMapper.java`
- [x] T027 [P] [US1] Add request/response DTOs with Bean Validation (`CreateTicketRequest` required title/description/priority/assignee; optional category; `UpdateTicketRequest` excludes category) in `backend/src/main/java/com/atms/ticket/api/dto/`
- [x] T028 [US1] Implement `TicketController` for `POST/GET /api/tickets`, `GET/PATCH /api/tickets/{displayId}`, `POST .../comments` per `contracts/openapi.yaml` in `backend/src/main/java/com/atms/ticket/api/TicketController.java`
- [x] T029 [US1] Invoke `TicketKnowledgeIndexPort.reindex` after ticket/comment writes in `TicketService`/`CommentService` (no-op until US4)
- [x] T030 [P] [US1] Add `frontend/src/features/tickets/TicketListPage.tsx` with list and navigation to detail
- [x] T031 [P] [US1] Add `frontend/src/features/tickets/TicketDetailPage.tsx` with view/update and comment form
- [x] T032 [P] [US1] Add `frontend/src/features/tickets/CreateTicketPage.tsx` with optional category field
- [x] T033 [US1] Wire ticket API methods in `frontend/src/api/tickets.ts` and surface validation/ProblemDetail errors in UI per [ui-flow.md](./ui-flow.md)

**Checkpoint**: MVP ticket flows work via UI and curl; display ids visible; category immutable after create

---

## Phase 4: User Story 2 - Find tickets by keyword and status (Priority: P2)

**Goal**: Keyword search on title, description, display ticket id (case-insensitive substring); filter list by status

**Independent Test**: Seed tickets with distinct text/statuses; automated backend tests prove `q` and `status` behavior including FR-006a exclusions and empty results (spec User Story 2, **SC-002**)

### Tests for User Story 2 (write first, expect FAIL)

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T034 [P] [US2] Add `TicketListSearchIntegrationTest` with Testcontainers in `backend/src/test/java/com/atms/ticket/integration/TicketListSearchIntegrationTest.java` (`q` matches title/description/display_id case-insensitive substring; excludes comments/resolution_notes/category; `status` filter; empty results)

### Implementation for User Story 2

- [x] T035 [US2] Extend `TicketRepository` with keyword + status query (`ILIKE` on title, description, display_id; exclude comments/resolution_notes/category from keyword) in `backend/src/main/java/com/atms/ticket/infrastructure/persistence/TicketRepository.java`
- [x] T036 [US2] Extend `TicketService.list` and `TicketController` `GET /api/tickets` with `q` and `status` parameters per `contracts/openapi.yaml:12-21`
- [x] T037 [P] [US2] Add search input and status filter controls to `frontend/src/features/tickets/TicketListPage.tsx`
- [x] T038 [US2] Update `frontend/src/api/tickets.ts` to pass `q` and `status` query params

**Checkpoint**: `./mvnw test` includes list search integration suite; UI search/filter matches FR-006a

---

## Phase 5: User Story 3 - Enforce ticket status lifecycle (Priority: P2)

**Goal**: Server-only FSM with five allowed transitions, resolution notes rules, forbidden transitions rejected with 409

**Independent Test**: All allowed transitions succeed; spec forbidden examples and resolve-without-notes fail; automated matrix tests pass (spec User Story 3, **SC-003**)

### Tests for User Story 3 (write first, expect FAIL)

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T039 [P] [US3] Add `TicketStateMachineIntegrationTest` with Testcontainers in `backend/src/test/java/com/atms/ticket/integration/TicketStateMachineIntegrationTest.java` (allowed transitions succeed; forbidden → 409; resolve without notes → 409; resolution notes edit only in RESOLVED)

### Implementation for User Story 3

- [x] T040 [US3] Implement `TransitionService` as sole FSM authority per [state-machine.md](./state-machine.md) in `backend/src/main/java/com/atms/ticket/application/TransitionService.java`
- [x] T041 [US3] Add `PATCH /api/tickets/{displayId}/status` with `TransitionStatusRequest` (`resolutionNotes` required when status is `RESOLVED`) in `backend/src/main/java/com/atms/ticket/api/TicketController.java`
- [x] T042 [US3] Add `PATCH /api/tickets/{displayId}/resolution-notes` (`resolutionNotes` minLength 1; only when status `RESOLVED`) and invoke `TicketKnowledgeIndexPort.reindex` in same transaction as write per **FR-019a** in `backend/src/main/java/com/atms/ticket/application/ResolutionNotesService.java` (or equivalent) and controller
- [x] T043 [P] [US3] Add status transition and resolution-notes UI on `frontend/src/features/tickets/TicketDetailPage.tsx` with 409 error display
- [x] T044 [US3] Extend `frontend/src/api/tickets.ts` with status and resolution-notes patch methods

**Checkpoint**: `./mvnw test` passes FSM integration suite; UI can drive full happy-path lifecycle

---

## Phase 6: User Story 4 - Ask natural-language questions over ticket history (Priority: P3)

**Goal**: Grounded ask with display-id citations, noMatch on empty retrieval, re-index on knowledge mutations, configurable top-k/threshold

**Independent Test**: Indexed tickets answer sample questions with `sources`; no-match returns HTTP 200 with `noMatch: true` and empty `sources`; updates visible after re-index; unit tests for chunker/builder green in default CI (spec User Story 4, **SC-010**)

### Tests for User Story 4 (write first, expect FAIL)

- [ ] T045 [P] [US4] Add `CanonicalKnowledgeDocumentBuilderTest` (JUnit, no Spring) in `backend/src/test/java/com/atms/rag/application/CanonicalKnowledgeDocumentBuilderTest.java` (description, comments chronological, resolution notes; exclude title per [rag-ingestion.md](./rag-ingestion.md))
- [ ] T046 [P] [US4] Add `TicketChunkerTest` (JUnit, no Spring) in `backend/src/test/java/com/atms/rag/application/TicketChunkerTest.java` (paragraph split, ~800 char window, ~100 overlap)
- [ ] T047 [P] [US4] Add `RagIngestionIntegrationTest` in `backend/src/test/java/com/atms/rag/integration/RagIngestionIntegrationTest.java` (chunks + metadata `ticketId` = display id; re-index on description update; resolution-notes update while RESOLVED)
- [ ] T048 [P] [US4] Add `RagRetrievalIntegrationTest` with mock/fake embedding in `backend/src/test/java/com/atms/rag/integration/RagRetrievalIntegrationTest.java` (grounded `sources` or `noMatch`; no LLM call on empty retrieval)

### Implementation for User Story 4

- [ ] T049 [US4] Add `ticket_vector_chunk` Liquibase changeset per [data-model.md](./data-model.md) in `backend/src/main/resources/db/changelog/003-ticket-vector-chunk.yaml`
- [ ] T050 [P] [US4] Add `TicketVectorChunkEntity` and repository in `backend/src/main/java/com/atms/rag/infrastructure/persistence/`
- [ ] T051 [US4] Implement canonical knowledge document builder (description, comments chronological, resolution notes; exclude title from embeddable text per [rag-ingestion.md](./rag-ingestion.md)) in `backend/src/main/java/com/atms/rag/application/CanonicalKnowledgeDocumentBuilder.java`
- [ ] T052 [US4] Implement paragraph + ~800 char window / ~100 overlap chunker in `backend/src/main/java/com/atms/rag/application/TicketChunker.java`
- [ ] T053 [US4] Configure Spring AI embedding + `atms.rag.top-k` and `atms.rag.similarity-threshold` in `backend/src/main/resources/application.yml` and `backend/src/main/java/com/atms/rag/infrastructure/config/RagProperties.java`
- [ ] T054 [US4] Implement `TicketKnowledgeIndexAdapter` replacing no-op (delete-by-ticket_id, embed, insert; same transaction as ticket write) in `backend/src/main/java/com/atms/rag/infrastructure/TicketKnowledgeIndexAdapter.java`
- [ ] T055 [US4] Implement retrieval + grounding guard (empty retrieval → noMatch without LLM) in `backend/src/main/java/com/atms/rag/application/AskService.java`
- [ ] T056 [US4] Implement `AskController` `POST /api/ai/ask` (`question` max 2000) returning `answer`, `sources`, `noMatch` per `contracts/openapi.yaml:149-308` in `backend/src/main/java/com/atms/rag/api/AskController.java`
- [ ] T057 [US4] Wire status transition side-effects to full re-index per [rag-ingestion.md](./rag-ingestion.md) triggers in `TransitionService` via index port
- [ ] T058 [P] [US4] Add `frontend/src/features/ask/AskPage.tsx` showing answer, cited display ids, and no-match state per [ui-flow.md](./ui-flow.md)
- [ ] T059 [US4] Add `frontend/src/api/ask.ts` and route wiring in `frontend/src/App.tsx`

**Checkpoint**: Manual ask smoke in [quickstart.md](./quickstart.md) §5 passes; US4 unit + integration tests green with mock chat model

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Docs, evidence, and spec hygiene (constitution/TR alignment done in Phase 1 **T001–T002**)

- [ ] T060 [P] Document chunking and embedding model choice in `specs/001-support-ticket-rag/architecture.md` (TR-005, TR-006) if not already complete at implementation time
- [x] T061 Align `specs/001-support-ticket-rag/evaluation-strategy.md` AI-mistakes path with **`docs/decisions/ai-mistakes.md`** (spec **SC-009**)
- [ ] T062 Record at least one caught AI/RAG mistake per TR-017 in `docs/decisions/ai-mistakes.md` (create file if missing)
- [ ] T063 Add JavaDoc on public application interfaces (`TicketService`, `TransitionService`, `AskService`, `TicketKnowledgeIndexPort`) per constitution API & Contract Documentation
- [ ] T064 [P] Update `specs/001-support-ticket-rag/quickstart.md` with verified commands and frontend test script if added
- [ ] T065 Run full backend `./mvnw test` and manual quickstart checklist; fix any gaps found in `specs/001-support-ticket-rag/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)** → **Foundational (Phase 2)** → **US1 → US2 → US3 → US4** (recommended serial) → **Polish (Phase 7)**
- US2–US4 require Phase 2; US4 requires real index adapter and replaces no-op from T017

### User Story Dependencies

| Story | Depends on | Notes |
|-------|------------|--------|
| US1 (P1) | Phase 2 | MVP; no FSM/search/ask |
| US2 (P2) | US1 list API | Extends list; **SC-002** tests gate merge |
| US3 (P2) | US1 ticket entity | FSM tests gate merge |
| US4 (P3) | US1 writes + US3 status/resolution-notes for re-index triggers | **SC-010** unit + integration tests gate merge |

### Parallel Opportunities

- Phase 1: **T001–T002** (doc alignment, complete); T006–T009 in parallel after T003–T005
- Phase 2: T014–T015, T018–T019 in parallel
- US1: T020–T021, T026–T027, T030–T032 in parallel before services/controllers
- US2: T034 before T035–T038
- US3: T039 before T040–T044
- US4: T045–T048 before T049–T057; T050 parallel with T049 after changelog
- Polish: T060, T061, T064 in parallel where marked [P]

---

## Parallel Example: User Story 4

```bash
# Unit + integration tests first (fail until implementation):
T045, T046, T047, T048

# Persistence + builder stack:
T049, T050, T051, T052
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1–2
2. Complete Phase 3 (US1)
3. **STOP**: Run quickstart §4 ticket smoke without search/FSM/ask
4. Demo CRUD + persistence

### Incremental Delivery

1. US1 → US2 (discoverability + **SC-002** tests) → US3 (lifecycle) → US4 (grounded ask + **SC-010**)
2. Each checkpoint independently demonstrable

### Suggested MVP Scope

**Phases 1–3 only** (through T033): ticket management UI + API + PostgreSQL persistence.

---

## Notes

- Ticket module MUST NOT import Spring AI types (constitution / plan module boundaries)
- Never commit API keys; use env for Spring AI provider
- `[P]` tasks touch different files—avoid two agents on same file
- Commit after each task or logical group; re-run `./mvnw test` after US2, US3, and US4 test phases
- Probabilistic **rag-eval** profile: optional local tooling only for v1 (not a task gate)
