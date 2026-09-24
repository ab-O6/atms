# Review code

Read and follow `.cursor/commands/_shared-stack-context.md` first.

**Mode: read-only.** Do not modify, format, or commit any file.

## Scope

Review code the user selected, the active editor file, or paths they name. If unclear, ask once; otherwise infer from selection/context.

## What to evaluate

For each issue found, cover **correctness**, **bugs**, **security**, **performance**, **maintainability**, **error handling**, and **project convention** violations.

### Spring Boot / Java (when present)

Check against `.cursor/rules/java-springboot.mdc` and `.cursor/rules/api-standards.mdc`:

- Constructor injection; no field `@Autowired`; transactions on services not controllers
- DTOs/records at API boundary; no entity leakage; Bean Validation at edge
- RFC 9457 `ProblemDetail` via `@ControllerAdvice`; stable status codes (409 for state conflicts)
- Liquibase for schema; JPA query/N+1 risks; thin controllers

### React / TypeScript (when present)

Check against `.cursor/rules/frontend.mdc`:

- `strict` typing; no scattered `fetch` outside API module
- Loading / empty / error states for async UI
- ProblemDetail parsing for user-visible errors
- Backend authority for ticket transitions—no client-only security or FSM duplication beyond UX

### Tests (when reviewing test code)

Align with `.cursor/rules/testing.mdc`: behavior over implementation; FSM transition matrix completeness; deterministic vs RAG eval separation.

## Report format

Output **only actionable findings**. If none, state **No actionable findings** and optionally list **Residual risks / not reviewed** (e.g. untested paths).

Per finding:

| Field | Content |
|-------|---------|
| **Severity** | `critical` \| `high` \| `medium` \| `low` |
| **Location** | `path:line` (or range) |
| **Issue** | What is wrong |
| **Impact** | User, security, data, or ops effect |
| **Fix** | Concrete change (no code edits—describe the fix) |

Group by severity (critical first). Do not include praise or generic advice.
