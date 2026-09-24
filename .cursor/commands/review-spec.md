# Review spec vs implementation

Read and follow `.cursor/commands/_shared-stack-context.md` first.

**Mode: read-only.** Do not modify, format, or commit any file.

## Inputs

1. **Specification / requirements** — user paste, attached doc, or feature artifacts (`spec.md`, linked requirements, constitution principles).
2. **Implementation** — trace in backend (`**/src/main/java/**`), frontend (`**/*.{ts,tsx}`), config, migrations, and **tests**.

If no spec is provided, ask for it or use the feature `spec.md` under the path the user indicates.

## Method

1. Extract a numbered list of **testable requirements** (acceptance criteria, MUST/SHALL, user stories with clear outcomes).
2. For each requirement, search implementation and tests (graphify first when available).
3. Assign exactly one status:

| Status | Meaning |
|--------|---------|
| **Implemented** | Behavior matches requirement; tests or clear code path support it |
| **Partial** | Some behavior exists; gaps in scope, edge cases, API, UI, or tests |
| **Missing** | No meaningful implementation |
| **Incorrect** | Code exists but violates requirement or constitution |
| **Unable to Verify** | Spec ambiguous, env-dependent, or code absent |

4. Note **constitution** conflicts (`.specify/memory/constitution.md`) as **Incorrect** or **Partial** with principle reference.

## Per-requirement record

| Field | Content |
|-------|---------|
| **ID** | R1, R2, … |
| **Requirement** | Short quote or paraphrase |
| **Status** | One of the five above |
| **Evidence** | `path:line` for code and/or tests (or “not found”) |
| **Gap** | What is missing or wrong |
| **Recommended action** | Concrete next step (spec update, implement, add test, fix bug) |

## Summary

End with:

- Counts by status
- **Top gaps** (ordered by risk/user impact)
- **Suggested task slices** aligned with `tasks.md` / spec-driven workflow if present

Do not implement fixes in this command.
