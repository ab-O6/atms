# Feature Specification: Support Ticket Management with Grounded Q&A

**Feature Branch**: `001-support-ticket-rag`

**Created**: 2026-09-25

**Status**: Draft

**Input**: Product requirements sections 4 (Application functional requirements) through 8.3 (Security and engineering evidence). No content outside that range.

**Technology binding**: Prescribed interfaces, RAG pipeline, and engineering acceptance terms are in [technology-requirements.md](./technology-requirements.md). This file states **what** users and the business need; the companion file states **mandated technical acceptance** from the same source without expanding scope.

## Clarifications

### Session 2026-09-25

- Q: What is the canonical ticket identifier that lists, detail views, keyword search, and RAG citations must all use (for example the illustrative `TKT-1001`)? → A: Server-generated **display ticket id** (e.g. `TKT-1001`) for all user-facing surfaces, keyword search results, and ask citations; separate **internal surrogate key** for persistence only—not exposed in UI or ask responses.
- Q: When are resolution notes captured, and must they be present before a ticket can move to `RESOLVED`? → A: **Required** when transitioning to `RESOLVED`; may be updated while status is `RESOLVED` until the ticket is `CLOSED`.
- Q: What rules apply to ticket **category** for persistence and RAG metadata, given category is not listed among the updatable fields in the product requirements? → A: **Optional at create**; if omitted, persist **empty category**; **not changeable** after create.
- Q: Which ticket fields must **keyword search** (ticket list search, not RAG) match against, and what matching rule applies? → A: **Title, description, and display ticket id**; **case-insensitive substring** match.
- Q: How must grounded ask responses expose cited display ticket ids and a no-relevant-tickets outcome in the response payload? → A: HTTP **success** with **answer** text and structured **sources** (display ticket ids); **no-match**: **empty sources**, **noMatch** true, honest message—no fabricated answer.
- Q: Should the separate probabilistic **rag-eval** Maven profile be required for v1 “done”, or explicitly out of v1 with only deterministic RAG integration tests gating merge? → A: **v1 deferred**—merge gate is **deterministic** RAG integration tests (ingestion, retrieval, `noMatch`, citations); probabilistic **rag-eval** may be documented and run locally but is **not** a required CI gate for v1.
- Q: Where should the mandatory “caught AI mistake” write-up for TR-017 / SC-009 live in the repo? → A: **`docs/decisions/ai-mistakes.md`** (create if missing).
- Q: For v1, must keyword search and status filter on the ticket list have automated backend tests, or is manual quickstart verification enough? → A: **Required** automated backend integration/API tests for `q` and `status` (FR-006a rules, empty results)—SC-002 merge gate.
- Q: When resolution notes are updated while status is `RESOLVED`, must searchable knowledge re-index in the same database transaction as that write? → A: **Yes**—same transaction, **full re-index** (same rule as description/comment knowledge mutations).
- Q: For v1, must JUnit unit tests (no Spring) for the ticket chunker and canonical knowledge document builder run in default CI, in addition to RAG integration tests? → A: **Required** in default `./mvnw test` (pure unit tests, no LLM).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Manage support tickets (Priority: P1)

Support staff create tickets, browse the list, open details, update title, description, priority, and assignee, and add comments. Data remains available after the application restarts. Invalid input is rejected with clear feedback in the user interface.

**Why this priority**: Core ticket handling is the foundation for search, status workflow, and grounded Q&A over ticket history.

**Independent Test**: Create a ticket, list and view it, update fields and assignee, add a comment, restart the application, and confirm data and meaningful validation errors on bad input.

**Acceptance Scenarios**:

1. **Given** no ticket exists, **When** the user creates a ticket with valid data, **Then** the ticket appears in the list and can be viewed in detail.
2. **Given** an existing ticket, **When** the user updates title, description, priority, or assignee, **Then** the detail view reflects the changes.
3. **Given** an existing ticket, **When** the user adds a comment, **Then** the comment appears on the ticket detail.
4. **Given** invalid input on create or update, **When** the user submits, **Then** the server rejects the request and the UI shows a meaningful error.
5. **Given** persisted tickets, **When** the application restarts, **Then** ticket data is still present.

---

### User Story 2 - Find tickets by keyword and status (Priority: P2)

Support staff search tickets by keyword and filter the list by status to narrow work queues.

**Why this priority**: Discovery supports daily operations before lifecycle and AI-assisted history questions.

**Independent Test**: Create tickets with distinct text and statuses; verify keyword search and status filter return the expected subsets. **v1** requires **automated backend tests** (API or integration) proving FR-006a matching and status filter behavior, including empty results.

**Acceptance Scenarios**:

1. **Given** tickets with varied titles and descriptions, **When** the user searches by a keyword present in a ticket, **Then** matching tickets are returned.
2. **Given** tickets in multiple statuses, **When** the user filters by one status, **Then** only tickets in that status are shown.

---

### User Story 3 - Enforce ticket status lifecycle (Priority: P2)

Ticket status changes follow a fixed lifecycle. The system allows only valid transitions and rejects invalid ones. Only the server may authorize status changes.

**Why this priority**: Lifecycle rules are deterministic business logic that must not be bypassed from the client.

**Independent Test**: Apply each allowed transition and confirm success; attempt forbidden transitions (e.g. `CLOSED` → `OPEN`) and confirm server rejection. Automated tests cover the full transition matrix per [technology-requirements.md](./technology-requirements.md).

**Acceptance Scenarios**:

1. **Given** a ticket in `OPEN`, **When** status moves to `IN_PROGRESS`, **Then** the change succeeds.
2. **Given** a ticket in `IN_PROGRESS`, **When** status moves to `RESOLVED`, **Then** the change succeeds.
3. **Given** a ticket in `RESOLVED`, **When** status moves to `CLOSED`, **Then** the change succeeds.
4. **Given** a ticket in `OPEN`, **When** status moves to `CANCELLED`, **Then** the change succeeds.
5. **Given** a ticket in `IN_PROGRESS`, **When** status moves to `CANCELLED`, **Then** the change succeeds.
6. **Given** a ticket in `CLOSED`, **When** status moves to `OPEN`, **Then** the server rejects the transition.
7. **Given** a ticket in `RESOLVED`, **When** status moves to `OPEN`, **Then** the server rejects the transition.
8. **Given** a ticket in `CANCELLED`, **When** status moves to `OPEN`, **Then** the server rejects the transition.
9. **Given** a ticket in `IN_PROGRESS`, **When** status moves to `RESOLVED` without resolution notes, **Then** the server rejects the transition.
10. **Given** a ticket in `RESOLVED` with resolution notes, **When** the user updates resolution notes before `CLOSED`, **Then** the update succeeds and searchable knowledge reflects the change on refresh.

**Allowed transitions (authoritative)**:

- `OPEN` → `IN_PROGRESS`
- `IN_PROGRESS` → `RESOLVED`
- `RESOLVED` → `CLOSED`
- `OPEN` → `CANCELLED`
- `IN_PROGRESS` → `CANCELLED`

---

### User Story 4 - Ask natural-language questions over ticket history (Priority: P3)

Users ask questions in natural language about ticket history. Answers use only relevant real ticket data, cite ticket identifiers used, and state clearly when nothing relevant is found instead of inventing an answer. Each question receives one grounded response; the assistant does not independently create tickets, send notifications, or take other autonomous actions.

**Why this priority**: Grounded Q&A depends on accurate tickets, comments, and search knowledge that stays current when tickets change.

**Independent Test**: Prepare ticket history that answers sample questions; submit questions; verify cited ticket IDs, honest no-match responses, and no fabricated answers when nothing relevant is found. **v1 acceptance** requires **deterministic** automated integration tests for ingestion, retrieval, `noMatch`, and citation shape, plus **JUnit unit tests** (no Spring) for the canonical knowledge document builder and ticket chunker in default CI; probabilistic **rag-eval** (recall@k, live embeddings) is optional and not a CI merge gate. Technical ask interface and index behavior per [technology-requirements.md](./technology-requirements.md).

**Acceptance Scenarios**:

1. **Given** ticket history that matches the question, **When** the user submits a natural-language question, **Then** the response is grounded in that ticket data and cites the specific **display ticket id(s)** (e.g. `TKT-1001`) used—not internal surrogate keys.
2. **Given** no relevant tickets for the question, **When** the user submits the question, **Then** the response returns HTTP success with **noMatch** true, **empty sources**, an honest message, and no fabricated answer body.
3. **Given** a support-specific question, **When** the system answers, **Then** it uses only ticket history retrieved for that question and does not rely on general knowledge for support-specific facts.
4. **Given** a ticket is updated or closed, **When** search knowledge is refreshed per product rules, **Then** subsequent answers can reflect the updated content.

**Illustrative in-scope questions** (not an exhaustive list):

- “Have we seen payment failures before?”
- “What was the resolution for ticket TKT-1001?”
- “What are the common causes of shipment tracking issues?”
- “Show me similar resolved tickets.”
- “Which high-priority tickets are related to payment?”

---

### Edge Cases

- Invalid status transition attempted (any path not listed as allowed) → server rejects; UI shows meaningful error where applicable.
- Transition to `RESOLVED` without non-empty resolution notes → server rejects; UI shows meaningful error.
- Resolution notes edit attempted when status is not `RESOLVED` → server rejects (except notes supplied as part of the `IN_PROGRESS` → `RESOLVED` transition).
- Resolution notes updated while `RESOLVED` → **full re-index** in same transaction as write; subsequent ask reflects new notes after commit.
- Search or filter with no matches → empty result set; no misleading data.
- Question with no retrievable relevant tickets → HTTP success, **noMatch** true, **empty sources**, honest message; no fabricated answer.
- Ticket updated or closed → search knowledge is refreshed so answers are not based on stale ticket content.
- Invalid input on ticket or ask flows → server validation failure with meaningful UI errors for ticket flows.

## Requirements *(mandatory)*

### Functional Requirements

**Ticket management**

- **FR-001**: System MUST allow users to create a ticket.
- **FR-002**: System MUST allow users to list tickets.
- **FR-003**: System MUST allow users to view ticket details.
- **FR-004**: System MUST allow users to update title, description, priority, and assignee (category is **not** updatable after create).
- **FR-004a**: System MUST accept an optional **category** at ticket creation; if omitted, category MUST be stored as empty and remain immutable thereafter.
- **FR-005**: System MUST allow users to add comments to a ticket.
- **FR-006**: System MUST allow users to search tickets by keyword.
- **FR-006a**: Keyword search (ticket list) MUST match **title**, **description**, and **display ticket id** using **case-insensitive substring** matching; comments, resolution notes, and category MUST NOT be used for list keyword search.
- **FR-007**: System MUST allow users to filter tickets by status.
- **FR-008**: System MUST persist ticket data so it survives application restart.
- **FR-009**: System MUST validate input on the server.
- **FR-010**: System MUST display meaningful errors in the UI when validation or operations fail.

**Status state machine (server authority)**

- **FR-011**: System MUST enforce the ticket status state machine on the server.
- **FR-012**: System MUST allow only these transitions: `OPEN` → `IN_PROGRESS`; `IN_PROGRESS` → `RESOLVED`; `RESOLVED` → `CLOSED`; `OPEN` → `CANCELLED`; `IN_PROGRESS` → `CANCELLED`.
- **FR-013**: System MUST reject all other status transitions (including `CLOSED` → `OPEN`, `RESOLVED` → `OPEN`, and `CANCELLED` → `OPEN`).
- **FR-013a**: Transition to `RESOLVED` MUST require non-empty **resolution notes** supplied on that transition.
- **FR-013b**: Resolution notes MAY be updated only while status is `RESOLVED` (until `CLOSED`); updates MUST NOT be allowed in other statuses except as part of the `IN_PROGRESS` → `RESOLVED` transition.

**Grounded Q&A (behavior)**

- **FR-014**: System MUST provide natural-language question answering over ticket history, grounded strictly in real ticket data.
- **FR-015**: System MUST cite the specific **display ticket id(s)** (e.g. `TKT-1001`) used to produce each assistant answer in a structured **sources** list; internal surrogate keys MUST NOT appear in **sources** or user-facing ask output.
- **FR-015b**: Ask responses MUST include natural-language **answer** text and structured **sources** (display ticket ids) on HTTP success when tickets are relevant.
- **FR-015a**: System MUST assign a server-generated display ticket id at ticket creation; display ticket id MUST appear in list, detail, keyword search results, and RAG metadata as `ticketId`.
- **FR-016**: When no relevant tickets are found, ask MUST return HTTP success with **noMatch** true, **empty sources**, and an honest message; MUST NOT return a fabricated plausible answer.

**Searchable ticket knowledge (behavior)**

- **FR-017**: System MUST make ticket description, comments, and resolution notes available for search when answering history questions.
- **FR-018**: Searchable ticket knowledge MUST carry identifying and filtering attributes: ticket identifier, status, priority, assignee, and category.
- **FR-019**: System MUST refresh search knowledge when a ticket is updated or closed so answers are not based on stale content.
- **FR-019a**: Updates to **resolution notes** while status is `RESOLVED` MUST trigger a **full re-index** in the **same database transaction** as the persistence write (not async or lazy refresh).

**Grounding and guardrails (behavior)**

- **FR-020**: The assistant MUST answer support-specific questions only from retrieved ticket history, not from general knowledge.
- **FR-021**: When no tickets are relevant, the assistant MUST say so explicitly and MUST NOT produce a fabricated but plausible-sounding answer (same no-match contract as **FR-016**).
- **FR-022**: The assistant MUST answer one question with one grounded response and MUST NOT independently create tickets, send notifications, chain tools, or take further actions.

**End-to-end product acceptance (behavioral summary)**

*Normative detail for the items below is in **FR-001–FR-022**; these entries exist for acceptance tracing only.*

- **FR-023**: Users MUST be able to create tickets from the UI; list, view, update (including assignee), and comment; search by keyword; and filter by status.
- **FR-024**: Valid status transitions MUST succeed; invalid transitions MUST be rejected on the server; persisted data MUST survive restart; server validation and meaningful UI errors MUST work for ticket flows.
- **FR-025**: Natural-language ask MUST return grounded, ticket-sourced answers with citations for in-scope questions and honest no-match responses otherwise.

**Prescribed technical requirements**

- **FR-026**: All technology, API, vector index, retrieval configuration, and engineering evidence requirements in [technology-requirements.md](./technology-requirements.md) are **in scope** and MUST be satisfied without adding product scope beyond sections 4–8.3.

### Key Entities

- **Ticket**: Support item with **display ticket id** (server-generated, e.g. `TKT-1001`, canonical for UI/search/citations), **internal surrogate key** (persistence only, not user-facing), title, description, priority, assignee, **category** (optional at create, immutable thereafter; empty string if omitted), status (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED`), **resolution notes** (required at `RESOLVED`, editable while `RESOLVED` until `CLOSED`), and durable persistence. Resolution notes are part of searchable ticket knowledge.
- **Comment**: Text attached to a ticket; part of searchable ticket knowledge.
- **Searchable ticket knowledge**: Derived from description, comments, and resolution notes, with attributes ticket identifier, status, priority, assignee, and category.
- **Ask request**: Natural-language question about ticket history.
- **Grounded answer**: HTTP success payload with **answer** text and **sources** (display ticket ids used), or **noMatch** true with **empty sources** and an honest message when nothing relevant is found.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A user can create a ticket from the UI, see it in the list, open details, update fields and assignee, and add a comment in one session; after application restart, that ticket data is still available.
- **SC-002**: Keyword search returns tickets when the query matches title, description, or display ticket id (case-insensitive substring); status filter shows only tickets in the selected status; **automated backend tests** cover list `q`/`status` behavior (including FR-006a exclusions and empty results).
- **SC-003**: Every allowed status transition succeeds; documented invalid examples (`CLOSED` → `OPEN`, `RESOLVED` → `OPEN`, `CANCELLED` → `OPEN`) and every other disallowed transition is rejected on the server; automated tests cover the full transition matrix.
- **SC-004**: Invalid server input for ticket operations produces validation failures and the UI shows meaningful errors (v1 verified via manual quickstart / API smoke; frontend RTL optional per [test-strategy.md](./test-strategy.md)).
- **SC-005**: After ticket history is searchable, in-scope natural-language questions receive answers grounded in ticket data with cited **display ticket id(s)** only.
- **SC-006**: Out-of-scope or no-match questions return HTTP success with **noMatch** true, **empty sources**, and an honest message, with no fabricated answer.
- **SC-007**: After a ticket is updated or closed, answers to history questions can reflect the latest ticket content (no stale search knowledge).
- **SC-008**: Operators can tune how many historical matches are considered and how closely content must match, without requiring a code change for each tuning change (per [technology-requirements.md](./technology-requirements.md)).
- **SC-009**: Documented rationale exists for how ticket text is prepared for search and how search models are chosen; at least one meaningful AI error was caught and documented during development in **`docs/decisions/ai-mistakes.md`**; no secrets appear in the repository (per [technology-requirements.md](./technology-requirements.md)).
- **SC-010**: v1 RAG quality gate is **deterministic** automated integration tests (ingestion, retrieval, empty-retrieval `noMatch`, display-id citations) plus **unit tests** for canonical knowledge document builder and ticket chunker in default CI; probabilistic **rag-eval** profile is **not** required in CI for v1 complete.

## Assumptions

- Specification content is limited to product requirements sections 4 through 8.3.
- [technology-requirements.md](./technology-requirements.md) captures prescribed technical acceptance from the same sections; planning artifacts (`architecture.md`, `api-contract.md`, `rag-ingestion.md`, etc.) elaborate TR items without changing scope.
- Probabilistic RAG evaluation (`rag-eval` profile) may be added for tuning and regression but is **out of v1 mandatory acceptance**; see **SC-010**.
