# Shared context (read first)

Use this block for every command in `.cursor/commands/` except when a command explicitly overrides it.

## Orient before judging

1. Run `graphify query "<topic>"` (or `graphify path` / `graphify explain`) before broad Read/Grep/Glob exploration when `graphify-out/graph.json` exists (see `.cursor/rules/graphify.mdc`).
2. Detect what is **actually present**—do not assume Spring, React, or tests exist until you see build files and `src/`:
   - Backend: `pom.xml`, `build.gradle*`, `**/src/main/java/**`
   - Frontend: `package.json`, `**/src/**/*.{ts,tsx}`, test runner config (`vitest`, `jest`, `playwright`, etc.)
   - Specs: `spec.md`, `plan.md`, `tasks.md` under feature folders; `.specify/memory/constitution.md`
3. Load matching project rules from `.cursor/rules/` (only those that apply to files you inspect):
   - `java-springboot.mdc`, `api-standards.mdc` — Java/API
   - `frontend.mdc` — React/TypeScript
   - `testing.mdc` — tests
   - `rag-vector-store.mdc` — RAG/indexing
   - `spec-driven-development.mdc` — spec workflow
   - `documentation.mdc` — when comparing docs vs behavior

If implementation is missing, say so and judge against **documented conventions** only where the command allows; mark items **Unable to Verify**.

## Stack expectations (when code exists)

| Layer | Expected (ATMS) | Verify in repo |
|-------|-----------------|----------------|
| Backend | Java 25, Spring Boot, JPA, Liquibase, PostgreSQL, ProblemDetail errors | `pom.xml` / Gradle, dependencies |
| Frontend | React, TypeScript `strict`, centralized API client | `package.json`, `tsconfig` |
| Tests | JUnit 5 (+ Mockito sparingly); frontend runner as configured | `src/test`, `*.test.ts(x)` |
| RAG | Spring AI, PGVector, ticket-only grounding, configurable top-k/threshold | `rag/**`, config |

## Evidence and citations

- Cite findings as `path:line` (or line range). Quote only what you need.
- Prefer behavior and public contracts over private implementation details.
- Do not paste secrets, tokens, or full stack traces in the report.

## Output discipline

- Structured markdown: headings, tables, or bullet lists—no filler.
- Actionable only: skip style nitpicks unless they violate project rules or hurt maintainability.
- **Read-only commands** must not edit, format, or commit files.
