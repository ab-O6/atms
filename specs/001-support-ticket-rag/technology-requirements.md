# Technology Requirements: Support Ticket Management with Grounded Q&A

**Status**: Draft  
**Created**: 2026-09-25  
**Source**: Product requirements sections 4 through 8.3 (prescribed acceptance and interface terms).  
**Companion**: [spec.md](./spec.md) (behavior and outcomes without implementation binding).

This document holds **technology and contract guidance** mandated by the product requirements. It does not add scope beyond [spec.md](./spec.md).

## RAG pipeline (prescribed flow)

The system MUST implement this end-to-end flow:

1. Support tickets  
2. Create knowledge documents  
3. Chunk  
4. Generate embeddings  
5. Vector store  
6. User question  
7. Similarity search  
8. Relevant tickets  
9. LLM + context  
10. Grounded answer  
11. Ticket sources (citations)

## Ingestion

- **TR-001**: Convert ticket **description**, **comments**, and **resolution notes** into searchable knowledge documents.
- **TR-002**: Knowledge document metadata MUST include: `ticketId` (the **display ticket id**, e.g. `TKT-1001`, not the internal surrogate key), `status`, `priority`, `assignee`, `category`.
- **TR-003**: **Re-ingest / refresh embeddings** when a ticket is **updated** or **closed** so the knowledge base does not go stale.

## Retrieval configuration and documentation

- **TR-004**: **Top-K** and **similarity threshold** MUST be **configurable**, not hardcoded.
- **TR-005**: **Chunking strategy** for ticket data (paragraph-based, fixed-size, semantic splitting, or other approach from specification analysis) MUST be **documented and justified** in `architecture.md`.
- **TR-006**: **Embedding model choice** (e.g. local vs cloud) and **cost / latency / quality** tradeoffs considered MUST be **documented and justified** in `architecture.md`.

## Grounding and guardrails (technical)

- **TR-007**: Assistant MUST use **only retrieved ticket context** for support-specific questions; MUST NOT fall back on general LLM knowledge for those questions.
- **TR-008**: If no relevant tickets, MUST respond explicitly; MUST NOT fabricate plausible answers.
- **TR-009**: **Single retrieval → generate** flow per question (not an autonomous agent). MUST NOT independently create tickets, send notifications, chain tools, or take further actions.

## API contract (prescribed)

- **TR-010**: Expose **`POST /api/ai/ask`** with request body containing a `question` field.

Example request:

```json
{
  "question": "What caused previous payment failures?"
}
```

- **TR-011**: For in-scope questions, HTTP success response MUST include natural-language **answer** text and structured **sources** listing **display ticket id(s)** (e.g. `TKT-1001`) used; internal surrogate keys MUST NOT appear in **sources**.
- **TR-012**: For out-of-scope or no-match questions, HTTP success response MUST set **noMatch** true (or equivalent flag), return **empty sources**, include an honest no-relevant-tickets message, and MUST NOT fabricate a plausible answer.

## Vector index acceptance

- **TR-013**: Ticket data MUST be converted into **embeddings** and stored in a **vector store**.
- **TR-014**: **Re-ingestion** MUST occur when a ticket is **updated** so embeddings do not go stale.

## Verification and engineering evidence

- **TR-015**: **State-machine integration tests** MUST pass.
- **TR-016**: **No secrets** MUST be committed to the repository.
- **TR-017**: At least **one meaningful AI mistake** (incorrect code or ungrounded / hallucinated assistant answer) MUST be **caught and documented** during development.

## Traceability to behavioral spec

| Technology requirement | Behavioral spec |
| ---------------------- | --------------- |
| TR-001–TR-003, TR-013–TR-014 | User Story 4; FR-017–FR-020 |
| TR-004–TR-006 | FR-021–FR-023; Success Criteria SC-007–SC-009 |
| TR-007–TR-009 | FR-024–FR-026 |
| TR-010–TR-012 | FR-027, FR-031 |
| TR-015 | FR-029; SC-003 |
| TR-016–TR-017 | FR-034–FR-035; SC-009 |
