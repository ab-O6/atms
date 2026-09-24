# Generate tests

Read and follow `.cursor/commands/_shared-stack-context.md` first.

**Mode: implementation allowed** for test files and minimal test fixtures only—do not refactor production code unless required to make tests runnable (then keep diff minimal and explain).

## Scope

Generate tests for user-selected code, the active file, or named symbols. Match **existing** frameworks, directory layout, naming, fixtures, and utilities—discover them before writing anything.

## Discover conventions

| Area | Look for |
|------|----------|
| Java | `src/test/java`, JUnit 5 (`@Test`, `@ParameterizedTest`), `@SpringBootTest` / slice tests, Testcontainers, Mockito usage patterns |
| Frontend | `*.test.ts(x)`, `*.spec.ts(x)`, setup files, MSW/handlers, `render` helpers, `vitest`/`jest` config |
| RAG/business split | `.cursor/rules/testing.mdc` — deterministic business tests in main suite; flaky RAG eval separate |

Follow `.cursor/rules/testing.mdc`:

- Test **observable behavior** (inputs, outputs, exceptions, side effects)—not private methods or mock call counts unless contract-critical
- Ticket FSM: table/parameterized tests for **every allowed and rejected** transition when touching lifecycle code
- No new dependencies without strong justification
- No live LLM/network in deterministic suite

## What to cover

- Happy paths
- Edge cases and validation failures
- Error paths (ProblemDetail / HTTP status on API tests; error UI only where frontend is in scope)
- Important business rules called out in spec or code comments

Avoid testing framework internals or implementation details that do not affect behavior.

## Deliver

1. New or updated test files only (unless a tiny production hook is unavoidable).
2. Brief summary: files touched, scenarios covered, anything intentionally not covered.
3. **Run tests** when the toolchain is available:
   - Java: `./mvnw test` / `./gradlew test` with focused class or module if full suite is heavy
   - Frontend: `npm test` / `pnpm test` / `yarn test` with pattern matching new tests
4. If run fails, fix tests (or document blocking env issue)—do not weaken assertions to green CI.
