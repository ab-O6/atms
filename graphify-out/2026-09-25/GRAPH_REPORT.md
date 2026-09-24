# Graph Report - atms  (2026-09-25)

## Corpus Check
- 85 files · ~84,878 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 17 file(s) not represented in the graph (top: .mdc 9, (none) 4, .toml 1)

## Summary
- 736 nodes · 820 edges · 69 communities (57 shown, 12 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 15 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3caf16ef`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- 2026-09-24 20:52:04Z
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
- Architecture: 001-support-ticket-rag
- Implementation Readiness Checklist: Support Ticket Management with Grounded Q&A
- mvnw
- compilerOptions
- Data Model: 001-support-ticket-rag
- Research: 001-support-ticket-rag
- Implementation Plan: [FEATURE]
- Implementation Plan: Support Ticket Management with Grounded Q&A
- Quickstart: Validate 001-support-ticket-rag
- Technology Requirements: Support Ticket Management with Grounded Q&A
- Test Strategy
- Review code
- speckit-checklist/SKILL.md
- RAG Ingestion
- speckit-clarify/SKILL.md
- speckit-implement/SKILL.md
- POST /api/ai/ask
- State Machine: Ticket Lifecycle
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
- TicketSnapshot
- .reindex
- client.ts
- Specification Analysis Report
- TicketKnowledgeIndexPort
- Dependencies & Execution Order
- Implementation Strategy

## God Nodes (most connected - your core abstractions)
1. `compilerOptions` - 17 edges
2. `Product Requirements Document` - 15 edges
3. `Specification Analysis Report` - 15 edges
4. `Tasks: Support Ticket Management with Grounded Q&A` - 14 edges
5. `Tasks: [FEATURE NAME]` - 13 edges
6. `TicketKnowledgeIndexPort` - 11 edges
7. `Implementation Readiness Checklist: Support Ticket Management with Grounded Q&A` - 11 edges
8. `create-new-feature.sh script` - 10 edges
9. `2026-09-24 20:52:04Z` - 10 edges
10. `Research: 001-support-ticket-rag` - 10 edges

## Surprising Connections (you probably didn't know these)
- `Layering (package-by-feature)` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  specs/001-support-ticket-rag/architecture.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `Findings` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  .specstory/history/2026-09-24_20-27-49Z-speckit-checklist-command.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `Searchable knowledge & index (FR-017–FR-019, TR-001–TR-004, TR-013–TR-014)` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  .specstory/history/2026-09-24_20-27-49Z-speckit-checklist-command.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `9. Module integration (ticket ↔ rag)` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  specs/001-support-ticket-rag/research.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java
- `Phase 2: Foundational (Blocking Prerequisites)` --references--> `TicketKnowledgeIndexPort`  [INFERRED]
  specs/001-support-ticket-rag/tasks.md → backend/src/main/java/com/atms/ticket/application/TicketKnowledgeIndexPort.java

## Import Cycles
- None detected.

## Communities (69 total, 12 thin omitted)

### Community 0 - "2026-09-24 20:52:04Z"
Cohesion: 0.05
Nodes (38): 2026-09-24 20:52:04Z, Clarifications reflected, Clarify complete, Completion report, Constitution Alignment Issues, Constitution Alignment Issues, Constitution Alignment Issues, Coverage summary (+30 more)

### Community 1 - "Product Requirements Document"
Cohesion: 0.05
Nodes (36): 10. Traceability summary, 1.1 Product goal, 1.2 Primary assessment goal (delivery context), 1.3 Required development workflow, 1. Purpose and assessment context, 2026-09-24 19:43:10Z, 2. Technology constraints, 3.1 Generic AI steering artefacts (+28 more)

### Community 2 - "common.sh"
Cohesion: 0.13
Nodes (29): check-prerequisites.sh script, check_dir(), check_file(), find_specify_root(), format_speckit_command(), get_current_branch(), get_feature_paths(), get_invoke_separator() (+21 more)

### Community 3 - "plan.md"
Cohesion: 0.09
Nodes (24): AI endpoint, API Contract (Tickets), DTO highlights, Error types (stable `type` slugs), Resources, Content Quality, Feature Readiness, Notes (+16 more)

### Community 4 - "Per-requirement record"
Cohesion: 0.06
Nodes (32): 2026-09-24 20:27:49Z, Completion report, Constitution alignment issues, Constitution & engineering, Counts by status, Coverage summary (selected), Defaults (no `/speckit-checklist` args), End-to-end & success criteria (+24 more)

### Community 5 - "package.json"
Cohesion: 0.08
Nodes (25): dependencies, react, react-dom, devDependencies, @types/react, @types/react-dom, typescript, vite (+17 more)

### Community 6 - "Tasks: [FEATURE NAME]"
Cohesion: 0.07
Nodes (26): Dependencies & Execution Order, Format: `[ID] [P?] [Story] Description`, Implementation for User Story 1, Implementation for User Story 2, Implementation for User Story 3, Implementation Strategy, Incremental Delivery, MVP First (User Story 1 Only) (+18 more)

### Community 7 - "Tasks: Support Ticket Management with Grounded Q&A"
Cohesion: 0.17
Nodes (12): Format: `[ID] [P?] [Story] Description`, Implementation for User Story 2, Implementation for User Story 4, Notes, Parallel Example: User Story 4, Path Conventions, Phase 1: Setup (Shared Infrastructure), Phase 4: User Story 2 - Find tickets by keyword and status (Priority: P2) (+4 more)

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

### Community 20 - "Architecture: 001-support-ticket-rag"
Cohesion: 0.18
Nodes (11): Architecture: 001-support-ticket-rag, Ask (read-mostly), Chunking and embedding (summary), Configuration (externalized), Documentation map, Layering (package-by-feature), Observability, Request flows (+3 more)

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

### Community 25 - "Research: 001-support-ticket-rag"
Cohesion: 0.22
Nodes (9): 1. Embedding model and chat model, 2. Chunking strategy for ticket knowledge, 3. Canonical knowledge document, 4. Re-index reliability, 5. Knowledge-bearing mutation triggers, 6. Retrieval and grounding guard, 7. Display ticket id generation, 8. Integration testing approach (+1 more)

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

### Community 33 - "RAG Ingestion"
Cohesion: 0.25
Nodes (8): Canonical document content (embeddable), Chunking, Deletion, Failure handling, Metadata (per chunk, JSONB), Pipeline, RAG Ingestion, Triggers

### Community 34 - "speckit-clarify/SKILL.md"
Cohesion: 0.29
Nodes (6): Completion Report, Done When, Mandatory Post-Execution Hooks, Outline, Pre-Execution Checks, User Input

### Community 35 - "speckit-implement/SKILL.md"
Cohesion: 0.29
Nodes (6): Completion Report, Done When, Mandatory Post-Execution Hooks, Outline, Pre-Execution Checks, User Input

### Community 36 - "POST /api/ai/ask"
Cohesion: 0.29
Nodes (7): Errors, Grounding guard (server), POST /api/ai/ask, RAG API Contract, Request, Response 200 (match), Response 200 (no relevant tickets)

### Community 37 - "State Machine: Ticket Lifecycle"
Cohesion: 0.29
Nodes (7): Allowed transitions, Explicitly forbidden (examples), HTTP mapping, Side effects on transition, State Machine: Ticket Lifecycle, States, Test matrix

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
Cohesion: 0.23
Nodes (13): GlobalExceptionHandler, collectors, httpstatus, jakarta.servlet.http.HttpServletRequest, jakarta.validation.ConstraintViolationException, java.net.URI, org.springframework.http.ProblemDetail, org.springframework.http.ResponseEntity (+5 more)

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
Cohesion: 0.21
Nodes (11): OpenApiConfig, classpathresource, io.swagger.v3.oas.models.OpenAPI, ioexception, loggerfactory, openapiv3parser, org.slf4j.Logger, org.springframework.context.annotation.Bean (+3 more)

### Community 60 - "Phase 1 complete"
Cohesion: 0.22
Nodes (8): AtmsApplication, org.springframework.boot.autoconfigure.SpringBootApplication, Checklist gate (read-only), Important follow-up (T004 vs plan/constitution), Next, Phase 1 complete, Tasks marked done, springapplication

### Community 61 - "PostgresTestcontainerExtension"
Cohesion: 0.33
Nodes (6): Override, PostgresTestcontainerExtension, org.junit.jupiter.api.extension.BeforeAllCallback, org.junit.jupiter.api.extension.ExtensionContext, org.testcontainers.containers.PostgreSQLContainer, org.testcontainers.utility.DockerImageName

### Community 62 - "TicketSnapshot"
Cohesion: 0.32
Nodes (5): TicketSnapshot, Override, NoOpTicketKnowledgeIndexAdapter, org.springframework.stereotype.Component, uuid

### Community 63 - ".reindex"
Cohesion: 0.33
Nodes (5): Implementation for User Story 1, Implementation for User Story 3, Phase 3: User Story 1 - Manage support tickets (Priority: P1) 🎯 MVP, Phase 5: User Story 3 - Enforce ticket status lifecycle (Priority: P2), Tests for User Story 3 (write first, expect FAIL)

### Community 64 - "client.ts"
Cohesion: 0.47
Nodes (4): ApiError, apiFetch(), getApiBaseUrl(), ProblemDetailBody

### Community 65 - "Specification Analysis Report"
Cohesion: 0.33
Nodes (6): Constitution Alignment Issues, Coverage Summary Table, Metrics, Next Actions, Specification Analysis Report, Unmapped Tasks

### Community 66 - "TicketKnowledgeIndexPort"
Cohesion: 0.40
Nodes (4): TicketKnowledgeIndexPort, 9. Module integration (ticket ↔ rag), Phase 2: Foundational (Blocking Prerequisites), Phase 7: Polish & Cross-Cutting Concerns

### Community 67 - "Dependencies & Execution Order"
Cohesion: 0.50
Nodes (4): Dependencies & Execution Order, Parallel Opportunities, Phase Dependencies, User Story Dependencies

### Community 68 - "Implementation Strategy"
Cohesion: 0.50
Nodes (4): Implementation Strategy, Incremental Delivery, MVP First (User Story 1 Only), Suggested MVP Scope

## Knowledge Gaps
- **457 isolated node(s):** `common.sh script`, `com.atms:atms-backend`, `name`, `private`, `version` (+452 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 498 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TicketKnowledgeIndexPort` connect `TicketKnowledgeIndexPort` to `Specification Analysis Report`, `Per-requirement record`, `Architecture: 001-support-ticket-rag`, `TicketSnapshot`, `.reindex`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **Why does `Tasks: Support Ticket Management with Grounded Q&A` connect `Tasks: Support Ticket Management with Grounded Q&A` to `TicketKnowledgeIndexPort`, `Dependencies & Execution Order`, `plan.md`, `Implementation Strategy`, `.reindex`?**
  _High betweenness centrality (0.061) - this node is a cross-community bridge._
- **Why does `2026-09-24 20:52:04Z` connect `2026-09-24 20:52:04Z` to `Specification Analysis Report`, `Phase 1 complete`, `Specification Analysis Report`?**
  _High betweenness centrality (0.061) - this node is a cross-community bridge._
- **What connects `common.sh script`, `com.atms:atms-backend`, `name` to the rest of the system?**
  _457 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `2026-09-24 20:52:04Z` be split into smaller, more focused modules?**
  _Cohesion score 0.05128205128205128 - nodes in this community are weakly interconnected._
- **Should `Product Requirements Document` be split into smaller, more focused modules?**
  _Cohesion score 0.05405405405405406 - nodes in this community are weakly interconnected._
- **Should `common.sh` be split into smaller, more focused modules?**
  _Cohesion score 0.12698412698412698 - nodes in this community are weakly interconnected._