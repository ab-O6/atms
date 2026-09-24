<!--
Sync Impact Report
- Version change: 1.0.0 → 1.1.0
- Modified principles: II. Modular Architecture & Lombok DI (MapStruct added)
- Added sections: API & Contract Documentation
- Removed sections: none
- Deferred TODOs: none
-->

# ATMS Constitution

## Core Principles

### I. Framework-First Backend

The backend MUST target Java 25 with idiomatic Spring Boot 4.1.x and a compatible
Spring AI release. Prefer declarative configuration (properties, auto-configuration,
starter modules) and framework conventions; avoid custom wiring that fights Spring
unless a spec-approved exception documents why.

**Rationale**: Aligning with the framework reduces defect surface and keeps upgrades
tractable.

### II. Modular Architecture, Lombok & MapStruct

Code MUST follow clean modular boundaries and SOLID. Use Lombok to eliminate
boilerplate (getters, setters, constructors). Depend on Spring beans via
constructor injection using Lombok-generated required-args constructors. Required
dependency fields MUST be `final`.

Object mapping between layers (entity, domain, DTO, API models) MUST use MapStruct
with compile-time generated mappers; avoid hand-written mapping logic except where
a spec-approved exception documents why MapStruct is insufficient.

**Rationale**: Constructor injection and immutability make dependencies explicit;
MapStruct keeps mappings type-safe, reviewable, and free of reflection-heavy
utilities.

### III. Test-First Business Rules & State Authority

Test-Driven Development is mandatory for business rules and the ticket state
machine: specify behavior in tests before implementation. Frontend and backend MUST
each validate their own inputs; ONLY the backend MAY perform ticket state
transitions.

**Rationale**: Split validation limits bad data early; centralizing transitions
preserves a single source of truth for ticket lifecycle.

### IV. Grounded RAG & Vector Index Hygiene

RAG answers MUST use only retrieved ticket context. Responses MUST cite ticket IDs.
When no relevant tickets meet retrieval criteria, the system MUST explicitly state
that no relevant tickets were found. Retrieval `top-k` and similarity threshold MUST
be configurable. Ticket knowledge MUST be re-indexed after mutations that affect
searchable content.

**Rationale**: Grounding and citations prevent hallucinated ticket facts; re-indexing
keeps vector search consistent with persistence.

### V. Spec-Before-Code, Security & Official Sources

No feature implementation MAY begin until specification, plan, and tasks are
reviewed and accepted per project workflow. Secrets MUST NEVER be committed to the
repository. When resolving framework or API behavior, prefer official documentation
over anecdotal sources.

**Rationale**: Review gates reduce rework; secret hygiene and authoritative references
protect the system and team velocity.

## Technology Stack

| Layer | Requirement |
|-------|-------------|
| Persistence | PostgreSQL with PGVector for relational data and vector search |
| Backend | Java 25, Spring Boot 4.1.x, Spring AI, MapStruct (versions compatible per dependency BOM) |
| Frontend | React with TypeScript; `strict` mode enabled in compiler options |

Stack choices in this section MUST NOT be swapped without a constitution amendment.

## API & Contract Documentation

1. **HTTP APIs**: Every request handler (REST controller endpoint) MUST be
   documented in the project OpenAPI specification. Documentation MUST describe the
   operation, request parameters and body schema, response schemas, and relevant
   error responses so the contract is complete without reading implementation code.
2. **Service interfaces**: Every method on a public application or domain interface
   MUST document its contract: purpose, preconditions, each parameter (name, role,
   and data requirements), return type semantics, and documented exceptions or error
   outcomes. Use standard Java documentation (`@param`, `@return`, `@throws`) or
   equivalent project-approved format consistently.
3. **Single source of truth**: OpenAPI MUST stay aligned with implemented handlers;
   PRs that add or change endpoints MUST update the spec in the same change set.

**Rationale**: Explicit contracts enable frontend integration, agent tooling, and
safe refactors across module boundaries.

## Development Workflow & Quality Gates

1. **Specification path**: Feature work flows through reviewed `spec.md`, `plan.md`,
   and `tasks.md` (or equivalent Spec Kit artifacts) before code changes.
2. **Testing**: Business rules and state-machine transitions MUST have automated
   tests; red-green-refactor is the default loop.
3. **RAG configuration**: Operators MUST be able to tune retrieval `top-k` and
   similarity threshold without code changes (configuration or admin surface as
   defined in spec).
4. **Compliance check**: Pull requests MUST note constitution-relevant decisions
   (state transitions, RAG behavior, indexing triggers, API/OpenAPI changes,
   new or changed interface contracts) in review description.

## Governance

This constitution supersedes ad-hoc team habits for the ATMS project. Amendments
require:

1. A documented proposal describing principle changes and migration impact.
2. Update to `.specify/memory/constitution.md` with semantic version bump:
   - **MAJOR**: Removal or incompatible redefinition of a principle.
   - **MINOR**: New principle or materially expanded obligation.
   - **PATCH**: Clarifications and non-semantic wording fixes.
3. `LAST_AMENDED_DATE` set to the amendment date (ISO `YYYY-MM-DD`).

All contributors and automated agents MUST treat this file as binding for
architecture and quality decisions. Spec Kit commands (`/speckit-specify`,
`/speckit-plan`, `/speckit-tasks`, `/speckit-implement`) MUST align deliverables
with these principles.

**Version**: 1.1.0 | **Ratified**: 2026-09-25 | **Last Amended**: 2026-09-25
