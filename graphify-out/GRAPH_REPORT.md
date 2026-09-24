# Graph Report - atms  (2026-09-25)

## Corpus Check
- 109 files · ~89,395 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 19 file(s) not represented in the graph (top: .mdc 9, (none) 4, .toml 1)

## Summary
- 887 nodes · 1208 edges · 73 communities (60 shown, 13 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 50 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2b4cab4c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Specification Analysis Report
- Product Requirements Document
- common.sh
- plan.md
- Per-requirement record
- package.json
- Tasks: [FEATURE NAME]
- Tasks: Support Ticket Management with Grounded Q&A
- speckit-analyze/SKILL.md
- compilerOptions
- Execution Steps
- Feature Specification: Support Ticket Management with Grounded Q&A
- Specification Analysis Report
- Feature Specification: [FEATURE NAME]
- caveman/SKILL.md
- speckit-plan/SKILL.md
- speckit-specify/SKILL.md
- speckit-tasks/SKILL.md
- ATMS Constitution
- Core Principles
- TicketKnowledgeIndexPort
- Implementation Readiness Checklist: Support Ticket Management with Grounded Q&A
- mvnw
- compilerOptions
- Data Model: 001-support-ticket-rag
- TicketController.java
- Implementation Plan: [FEATURE]
- Implementation Plan: Support Ticket Management with Grounded Q&A
- Quickstart: Validate 001-support-ticket-rag
- Technology Requirements: Support Ticket Management with Grounded Q&A
- Test Strategy
- Review code
- speckit-checklist/SKILL.md
- Priority
- speckit-clarify/SKILL.md
- speckit-implement/SKILL.md
- TicketService
- TicketStatus
- Generate tests
- Review RAG output
- Review spec vs implementation
- Shared context (read first)
- speckit-constitution/SKILL.md
- GlobalExceptionHandler.java
- speckit-taskstoissues/SKILL.md
- [CHECKLIST TYPE] Checklist: [FEATURE NAME]
- 2026-09-24 18:39:48Z
- vite-env.d.ts
- 2026-09-24_18-39-48Z-speckit-constitution-principles-java.md
- 2026-09-24_19-24-25Z-java-spring-boot-cursor.md
- 2026-09-24_19-34-32Z-create-these-cursor-commands.md
- 2026-09-24_19-43-10Z-users-abhinavrai-downloads-assessments.md
- 2026-09-24_20-52-04Z-speckit-analyze.md
- com.atms:atms-backend
- OpenApiConfig.java
- Phase 1 complete
- PostgresTestcontainerExtension
- TicketRepository
- TicketEntity
- tickets.ts
- Screens
- Specification Analysis Report
- 2026-09-24 20:52:04Z
- `/speckit-tasks` complete
- Specification Analysis Report
- Phase 2 complete
- Clarify complete
- TicketSnapshotMapper

## God Nodes (most connected - your core abstractions)
1. `TicketEntity` - 27 edges
2. `compilerOptions` - 17 edges
3. `TicketKnowledgeIndexPort` - 16 edges
4. `TicketService` - 15 edges
5. `Priority` - 15 edges
6. `Product Requirements Document` - 15 edges
7. `Specification Analysis Report` - 15 edges
8. `CommentEntity` - 14 edges
9. `Tasks: Support Ticket Management with Grounded Q&A` - 14 edges
10. `TicketStatus` - 13 edges

## Surprising Connections (you probably didn't know these)
- `Phase 2: Foundational (Blocking Prerequisites)` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  specs/001-support-ticket-rag/tasks.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `Phase 7: Polish & Cross-Cutting Concerns` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  specs/001-support-ticket-rag/tasks.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `Findings` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  .specstory/history/2026-09-24_20-27-49Z-speckit-checklist-command.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `Searchable knowledge & index (FR-017–FR-019, TR-001–TR-004, TR-013–TR-014)` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  .specstory/history/2026-09-24_20-27-49Z-speckit-checklist-command.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `Implementation for User Story 2` --references--> `TicketRepository`  [INFERRED]
  specs/001-support-ticket-rag/tasks.md → backend/src/main/java/com/atms/ticket/infrastructure/persistence/TicketRepository.java

## Import Cycles
- None detected.

## Communities (73 total, 13 thin omitted)

### Community 0 - "Specification Analysis Report"
Cohesion: 0.22
Nodes (9): Constitution Alignment Issues, Coverage Summary Table, Metrics, Next Actions, `plan.md`, Specification Analysis Report, `tasks.md`, `technology-requirements.md` (+1 more)

### Community 1 - "Product Requirements Document"
Cohesion: 0.05
Nodes (36): 10. Traceability summary, 1.1 Product goal, 1.2 Primary assessment goal (delivery context), 1.3 Required development workflow, 1. Purpose and assessment context, 2026-09-24 19:43:10Z, 2. Technology constraints, 3.1 Generic AI steering artefacts (+28 more)

### Community 2 - "common.sh"
Cohesion: 0.13
Nodes (29): check-prerequisites.sh script, check_dir(), check_file(), find_specify_root(), format_speckit_command(), get_current_branch(), get_feature_paths(), get_invoke_separator() (+21 more)

### Community 3 - "plan.md"
Cohesion: 0.06
Nodes (38): AI endpoint, API Contract (Tickets), DTO highlights, Error types (stable `type` slugs), Resources, Content Quality, Feature Readiness, Notes (+30 more)

### Community 4 - "Per-requirement record"
Cohesion: 0.06
Nodes (32): 2026-09-24 20:27:49Z, Completion report, Constitution alignment issues, Constitution & engineering, Counts by status, Coverage summary (selected), Defaults (no `/speckit-checklist` args), End-to-end & success criteria (+24 more)

### Community 5 - "package.json"
Cohesion: 0.08
Nodes (23): dependencies, react, react-dom, react-router-dom, devDependencies, @types/react, @types/react-dom, typescript (+15 more)

### Community 6 - "Tasks: [FEATURE NAME]"
Cohesion: 0.07
Nodes (26): Dependencies & Execution Order, Format: `[ID] [P?] [Story] Description`, Implementation for User Story 1, Implementation for User Story 2, Implementation for User Story 3, Implementation Strategy, Incremental Delivery, MVP First (User Story 1 Only) (+18 more)

### Community 7 - "Tasks: Support Ticket Management with Grounded Q&A"
Cohesion: 0.08
Nodes (26): Dependencies & Execution Order, Format: `[ID] [P?] [Story] Description`, Implementation for User Story 2, Implementation for User Story 3, Implementation for User Story 4, Implementation Strategy, Incremental Delivery, MVP First (User Story 1 Only) (+18 more)

### Community 8 - "speckit-analyze/SKILL.md"
Cohesion: 0.08
Nodes (25): 1. Initialize Analysis Context, 2. Load Artifacts (Progressive Disclosure), 3. Build Semantic Models, 4. Detection Passes (Token-Efficient Analysis), 5. Severity Assignment, 6. Produce Compact Analysis Report, 7. Provide Next Actions, 8. Offer Remediation (+17 more)

### Community 9 - "compilerOptions"
Cohesion: 0.11
Nodes (18): compilerOptions, allowImportingTsExtensions, isolatedModules, jsx, lib, module, moduleDetection, moduleResolution (+10 more)

### Community 10 - "Execution Steps"
Cohesion: 0.12
Nodes (15): 1. Initialize Convergence Context, 2. Load Artifacts (Progressive Disclosure), 3. Build the Intent Inventory, 4. Assess the Codebase and Classify Findings, 5. Assign Severity, 6. Present the In-Session Findings Summary, 7. Append Convergence Tasks (or report converged), 8. Provide Next Actions (Handoff) (+7 more)

### Community 11 - "Feature Specification: Support Ticket Management with Grounded Q&A"
Cohesion: 0.13
Nodes (15): Assumptions, Clarifications, Edge Cases, Feature Specification: Support Ticket Management with Grounded Q&A, Functional Requirements, Key Entities, Measurable Outcomes, Requirements *(mandatory)* (+7 more)

### Community 12 - "Specification Analysis Report"
Cohesion: 0.13
Nodes (15): A1 — `plan.md` Performance Goals, Constitution Alignment Issues, Coverage Summary Table, D1 — `spec.md`, D2 — `spec.md`, I1 — `evaluation-strategy.md`, I2 — `plan.md` Technical Context, I3 — `plan.md` doc tree (+7 more)

### Community 13 - "Feature Specification: [FEATURE NAME]"
Cohesion: 0.15
Nodes (12): Assumptions, Edge Cases, Feature Specification: [FEATURE NAME], Functional Requirements, Key Entities *(include if feature involves data)*, Measurable Outcomes, Requirements *(mandatory)*, Success Criteria *(mandatory)* (+4 more)

### Community 14 - "caveman/SKILL.md"
Cohesion: 0.17
Nodes (10): caveman, Example output, How to invoke, See also, What it does, Auto-Clarity, Boundaries, Intensity (+2 more)

### Community 15 - "speckit-plan/SKILL.md"
Cohesion: 0.18
Nodes (10): Completion Report, Done When, Key rules, Mandatory Post-Execution Hooks, Outline, Phase 0: Outline & Research, Phase 1: Design & Contracts, Phases (+2 more)

### Community 16 - "speckit-specify/SKILL.md"
Cohesion: 0.18
Nodes (10): Completion Report, Done When, For AI Generation, Mandatory Post-Execution Hooks, Outline, Pre-Execution Checks, Quick Guidelines, Section Requirements (+2 more)

### Community 17 - "speckit-tasks/SKILL.md"
Cohesion: 0.18
Nodes (10): Checklist Format (REQUIRED), Completion Report, Done When, Mandatory Post-Execution Hooks, Outline, Phase Structure, Pre-Execution Checks, Task Generation Rules (+2 more)

### Community 18 - "ATMS Constitution"
Cohesion: 0.18
Nodes (10): API & Contract Documentation, ATMS Constitution, Core Principles, Development Workflow & Quality Gates, Governance, I. Framework-First Backend, II. Modular Architecture, Lombok & MapStruct, III. Grounded RAG & Vector Index Hygiene (+2 more)

### Community 19 - "Core Principles"
Cohesion: 0.18
Nodes (10): Core Principles, Governance, [PRINCIPLE_1_NAME], [PRINCIPLE_2_NAME], [PRINCIPLE_3_NAME], [PRINCIPLE_4_NAME], [PRINCIPLE_5_NAME], [PROJECT_NAME] Constitution (+2 more)

### Community 20 - "TicketKnowledgeIndexPort"
Cohesion: 0.06
Nodes (33): DisplayIdGenerator, TicketKnowledgeIndexPort, Override, NoOpTicketKnowledgeIndexAdapter, org.springframework.jdbc.core.JdbcTemplate, org.springframework.stereotype.Component, Architecture: 001-support-ticket-rag, Ask (read-mostly) (+25 more)

### Community 21 - "Implementation Readiness Checklist: Support Ticket Management with Grounded Q&A"
Cohesion: 0.18
Nodes (11): Acceptance Criteria Quality, Ambiguities & Conflicts, Dependencies & Assumptions, Edge Case Coverage, Implementation Readiness Checklist: Support Ticket Management with Grounded Q&A, Non-Functional Requirements, Notes, Requirement Clarity (+3 more)

### Community 22 - "mvnw"
Cohesion: 0.38
Nodes (8): mvnw script, clean(), die(), exec_maven(), hash_string(), set_java_home(), trim(), verbose()

### Community 23 - "compilerOptions"
Cohesion: 0.20
Nodes (9): compilerOptions, lib, module, moduleResolution, noEmit, skipLibCheck, strict, target (+1 more)

### Community 24 - "Data Model: 001-support-ticket-rag"
Cohesion: 0.20
Nodes (10): Comment, Data Model: 001-support-ticket-rag, Entity-relationship overview, Enumerations, Liquibase, Priority, Ticket, TicketStatus (+2 more)

### Community 25 - "TicketController.java"
Cohesion: 0.15
Nodes (16): CommentResponse, TicketResponse, TicketSummaryResponse, TicketMapper, TicketController, comparator, org.mapstruct.Mapper, org.mapstruct.Mapping (+8 more)

### Community 26 - "Implementation Plan: [FEATURE]"
Cohesion: 0.22
Nodes (8): Complexity Tracking, Constitution Check, Documentation (this feature), Implementation Plan: [FEATURE], Project Structure, Source Code (repository root), Summary, Technical Context

### Community 27 - "Implementation Plan: Support Ticket Management with Grounded Q&A"
Cohesion: 0.22
Nodes (9): Complexity Tracking, Constitution Check, Documentation (this feature), Implementation Plan: Support Ticket Management with Grounded Q&A, Phase 0 & Phase 1 Outputs, Project Structure, Source Code (repository root), Summary (+1 more)

### Community 28 - "Quickstart: Validate 001-support-ticket-rag"
Cohesion: 0.22
Nodes (9): 1. Database (local), 2. Backend, 3. Frontend, 4. Manual smoke (tickets), 5. Manual smoke (ask), 6. Automated validation, 7. Contracts, Quickstart: Validate 001-support-ticket-rag (+1 more)

### Community 29 - "Technology Requirements: Support Ticket Management with Grounded Q&A"
Cohesion: 0.22
Nodes (9): API contract (prescribed), Grounding and guardrails (technical), Ingestion, RAG pipeline (prescribed flow), Retrieval configuration and documentation, Technology Requirements: Support Ticket Management with Grounded Q&A, Traceability to behavioral spec, Vector index acceptance (+1 more)

### Community 30 - "Test Strategy"
Cohesion: 0.22
Nodes (9): Coverage expectations, Evidence, List search (mandatory gate, SC-002), Pyramid, Retrieval & ingestion (mandatory gate), State machine (mandatory gate, SC-003), Test Strategy, Testcontainers (+1 more)

### Community 31 - "Review code"
Cohesion: 0.25
Nodes (7): React / TypeScript (when present), Report format, Review code, Scope, Spring Boot / Java (when present), Tests (when reviewing test code), What to evaluate

### Community 32 - "speckit-checklist/SKILL.md"
Cohesion: 0.25
Nodes (7): Anti-Examples: What NOT To Do, Checklist Purpose: "Unit Tests for English", Example Checklist Types & Sample Items, Execution Steps, Post-Execution Checks, Pre-Execution Checks, User Input

### Community 33 - "Priority"
Cohesion: 0.17
Nodes (11): AddCommentRequest, CreateTicketRequest, UpdateTicketRequest, Priority, CRITICAL, HIGH, LOW, MEDIUM (+3 more)

### Community 34 - "speckit-clarify/SKILL.md"
Cohesion: 0.29
Nodes (6): Completion Report, Done When, Mandatory Post-Execution Hooks, Outline, Pre-Execution Checks, User Input

### Community 35 - "speckit-implement/SKILL.md"
Cohesion: 0.29
Nodes (6): Completion Report, Done When, Mandatory Post-Execution Hooks, Outline, Pre-Execution Checks, User Input

### Community 36 - "TicketService"
Cohesion: 0.30
Nodes (6): CommentService, TicketService, TicketSnapshot, CommentRepository, org.springframework.stereotype.Service, org.springframework.transaction.annotation.Transactional

### Community 37 - "TicketStatus"
Cohesion: 0.22
Nodes (7): TicketStatus, CANCELLED, CLOSED, IN_PROGRESS, OPEN, RESOLVED, instant

### Community 38 - "Generate tests"
Cohesion: 0.33
Nodes (5): Deliver, Discover conventions, Generate tests, Scope, What to cover

### Community 39 - "Review RAG output"
Cohesion: 0.33
Nodes (5): Hallucination row format, Inputs, Report sections, Review RAG output, Verification checklist

### Community 40 - "Review spec vs implementation"
Cohesion: 0.33
Nodes (5): Inputs, Method, Per-requirement record, Review spec vs implementation, Summary

### Community 41 - "Shared context (read first)"
Cohesion: 0.33
Nodes (5): Evidence and citations, Orient before judging, Output discipline, Shared context (read first), Stack expectations (when code exists)

### Community 42 - "speckit-constitution/SKILL.md"
Cohesion: 0.33
Nodes (5): Outline, Post-Execution Checks, Pre-Execution Checks, Scope Guard, User Input

### Community 43 - "GlobalExceptionHandler.java"
Cohesion: 0.19
Nodes (14): GlobalExceptionHandler, TicketNotFoundException, collectors, httpstatus, jakarta.servlet.http.HttpServletRequest, jakarta.validation.ConstraintViolationException, java.net.URI, org.springframework.http.ProblemDetail (+6 more)

### Community 44 - "speckit-taskstoissues/SKILL.md"
Cohesion: 0.40
Nodes (4): Outline, Post-Execution Checks, Pre-Execution Checks, User Input

### Community 45 - "[CHECKLIST TYPE] Checklist: [FEATURE NAME]"
Cohesion: 0.40
Nodes (4): [Category 1], [Category 2], [CHECKLIST TYPE] Checklist: [FEATURE NAME], Notes

### Community 46 - "2026-09-24 18:39:48Z"
Cohesion: 0.50
Nodes (3): 2026-09-24 18:39:48Z, Constitution updated, How your principles were organized

### Community 59 - "OpenApiConfig.java"
Cohesion: 0.15
Nodes (15): OpenApiConfig, Override, WebConfig, classpathresource, io.swagger.v3.oas.models.OpenAPI, ioexception, loggerfactory, openapiv3parser (+7 more)

### Community 60 - "Phase 1 complete"
Cohesion: 0.22
Nodes (8): AtmsApplication, org.springframework.boot.autoconfigure.SpringBootApplication, Checklist gate (read-only), Important follow-up (T004 vs plan/constitution), Next, Phase 1 complete, Tasks marked done, springapplication

### Community 61 - "PostgresTestcontainerExtension"
Cohesion: 0.33
Nodes (6): Override, PostgresTestcontainerExtension, org.junit.jupiter.api.extension.BeforeAllCallback, org.junit.jupiter.api.extension.ExtensionContext, org.testcontainers.containers.PostgreSQLContainer, org.testcontainers.utility.DockerImageName

### Community 62 - "TicketRepository"
Cohesion: 0.19
Nodes (6): TicketRepository, list, optional, org.springframework.data.jpa.repository.JpaRepository, org.springframework.data.jpa.repository.Query, uuid

### Community 63 - "TicketEntity"
Cohesion: 0.16
Nodes (19): arraylist, CommentEntity, TicketEntity, cascadetype, column, enumerated, enumtype, fetchtype (+11 more)

### Community 64 - "tickets.ts"
Cohesion: 0.12
Nodes (30): ApiError, apiFetch(), getApiBaseUrl(), ProblemDetailBody, addComment(), Comment, createTicket(), CreateTicketPayload (+22 more)

### Community 65 - "Screens"
Cohesion: 0.25
Nodes (8): 1. Ticket list, 2. Create ticket, 3. Ticket detail, 4. Ask (RAG), Citations display, Error UX, Screens, UI Flow

### Community 66 - "Specification Analysis Report"
Cohesion: 0.25
Nodes (8): Constitution Alignment Issues, Coverage Summary Table, Gate status, Metrics, Newly checked (9 items), Next Actions, Specification Analysis Report, Unmapped Tasks

### Community 67 - "2026-09-24 20:52:04Z"
Cohesion: 0.33
Nodes (5): 2026-09-24 20:52:04Z, Completion report, Coverage summary (taxonomy), Next command, `/speckit-clarify` — no questions

### Community 68 - "`/speckit-tasks` complete"
Cohesion: 0.33
Nodes (6): Clarifications reflected, Format check, MVP scope, Parallel highlights, `/speckit-tasks` complete, Summary

### Community 69 - "Specification Analysis Report"
Cohesion: 0.33
Nodes (6): Constitution Alignment Issues, Coverage Summary Table, Metrics, Next Actions, Specification Analysis Report, Unmapped Tasks

### Community 70 - "Phase 2 complete"
Cohesion: 0.50
Nodes (4): Checklist gate (read-only), Next, Phase 2 checkpoint, Phase 2 complete

### Community 71 - "Clarify complete"
Cohesion: 0.50
Nodes (4): Clarify complete, Coverage summary, Still blocking implement (not in spec), Suggested next steps

## Knowledge Gaps
- **474 isolated node(s):** `common.sh script`, `com.atms:atms-backend`, `LOW`, `MEDIUM`, `HIGH` (+469 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 532 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TicketKnowledgeIndexPort` connect `TicketKnowledgeIndexPort` to `tickets.ts`, `TicketService`, `Per-requirement record`, `Tasks: Support Ticket Management with Grounded Q&A`?**
  _High betweenness centrality (0.168) - this node is a cross-community bridge._
- **Why does `Tasks: Support Ticket Management with Grounded Q&A` connect `Tasks: Support Ticket Management with Grounded Q&A` to `plan.md`?**
  _High betweenness centrality (0.110) - this node is a cross-community bridge._
- **Why does `Tasks **T010–T019** ✓` connect `tickets.ts` to `TicketService`, `Phase 2 complete`, `GlobalExceptionHandler.java`, `TicketKnowledgeIndexPort`, `OpenApiConfig.java`, `PostgresTestcontainerExtension`?**
  _High betweenness centrality (0.107) - this node is a cross-community bridge._
- **Are the 8 inferred relationships involving `TicketKnowledgeIndexPort` (e.g. with `Layering (package-by-feature)` and `9. Module integration (ticket ↔ rag)`) actually correct?**
  _`TicketKnowledgeIndexPort` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `common.sh script`, `com.atms:atms-backend`, `LOW` to the rest of the system?**
  _474 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product Requirements Document` be split into smaller, more focused modules?**
  _Cohesion score 0.05405405405405406 - nodes in this community are weakly interconnected._
- **Should `common.sh` be split into smaller, more focused modules?**
  _Cohesion score 0.12698412698412698 - nodes in this community are weakly interconnected._