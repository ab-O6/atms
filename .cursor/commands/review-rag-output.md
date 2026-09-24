# Review RAG output

Read and follow `.cursor/commands/_shared-stack-context.md` first.

**Mode: read-only.** Do not modify, format, or commit any file.

## Inputs

User provides (or you locate in logs/fixtures/tests):

1. **Model answer** — final user-facing text and any structured citation payload
2. **Retrieval results** — ticket chunks/IDs, scores, thresholds applied, `top-k` config snapshot if available
3. **No-answer template** — expected copy when retrieval is empty or below threshold (from spec, mapper, or `.cursor/rules/rag-vector-store.mdc`)

Also read `.cursor/rules/rag-vector-store.mdc` and constitution **Principle IV** (grounded RAG).

## Verification checklist

For each check, record **pass** / **fail** with evidence.

| # | Check |
|---|--------|
| 1 | Every **factual support claim** in the answer is supported by text in **retrieved ticket context** (quote chunk + ticket ID) |
| 2 | Every **cited ticket ID** appears in the retrieval result set |
| 3 | No **external or general LLM knowledge** (no facts, policies, or ticket details not present in retrieved chunks) |
| 4 | If retrieval was empty or below threshold, output matches the **specified no-answer** response (wording and no fabricated tickets) |
| 5 | Unsupported claims are listed explicitly as **hallucinations** |

## Hallucination row format

| Claim in answer | Verdict | Why |
|-----------------|---------|-----|
| (quote) | `hallucination` \| `supported` \| `unverifiable` | Missing chunk, wrong ID, paraphrase drift, etc. |

## Report sections

1. **Verdict** — `grounded` \| `partially grounded` \| `not grounded` \| `correct no-answer` \| `incorrect no-answer`
2. **Checklist** — table of the five checks
3. **Hallucinations** — only failures; empty section if none
4. **Retrieval notes** — threshold/top-k hit/miss, missing metadata on chunks (if relevant)
5. **Recommended actions** — prompt, mapper, indexing, or test fixes (no code edits in this command)

Do not use parametric knowledge to “validate” ticket facts—only retrieved context counts.
