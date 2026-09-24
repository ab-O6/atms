# RAG API Contract

**Date**: 2026-09-25  
**OpenAPI**: [contracts/openapi.yaml](./contracts/openapi.yaml) (`/api/ai/ask`)

## POST /api/ai/ask

Ask a natural-language question over indexed ticket history. Single retrieval → generate; not an agent.

### Request

```json
{
  "question": "What caused previous payment failures?"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| question | string | yes | not blank, max 2000 chars |

### Response 200 (match)

```json
{
  "answer": "Based on ticket history, payment failures were linked to ...",
  "sources": ["TKT-1001", "TKT-1004"],
  "noMatch": false
}
```

| Field | Type | Rules |
|-------|------|--------|
| answer | string | Natural language; grounded in retrieved chunks only |
| sources | string[] | Distinct **display ticket ids**; subset of retrieval results |
| noMatch | boolean | `false` when sources non-empty |

### Response 200 (no relevant tickets)

```json
{
  "answer": "No relevant tickets were found for your question.",
  "sources": [],
  "noMatch": true
}
```

- LLM MUST NOT be invoked when retrieval is empty/below threshold.
- `answer` MUST be honest template or LLM paraphrase of no-match—never fabricated ticket facts.

### Errors

| Status | When |
|--------|------|
| 400 | Validation failure on `question` |
| 500 | Embedding/LLM infrastructure failure (generic ProblemDetail) |

### Grounding guard (server)

1. Similarity search with configured top-k and threshold.
2. If zero chunks pass → return no-match payload.
3. Else build prompt from chunk text only; system message forbids external knowledge.
4. Post-process: every id in `sources` must appear in retrieval metadata; strip hallucinated ids.
