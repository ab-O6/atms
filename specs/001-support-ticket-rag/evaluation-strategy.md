# Evaluation Strategy (RAG)

**Date**: 2026-09-25  
**Testing split**: [test-strategy.md](./test-strategy.md)

## Goals

- Prove **grounding** (citations ⊆ retrieved tickets, no external facts in deterministic fixtures).
- Measure **retrieval quality** separately from **generation quality** (probabilistic).
- Document at least one **caught AI mistake** during development (TR-017).

## Deterministic CI (gate)

| Check | Method |
|-------|--------|
| Empty retrieval → `noMatch` | Integration test with threshold/k and fixture tickets |
| Citation shape | Assert `sources` are display ids, never UUID |
| Citation ⊆ retrieval | Spy/log retrieval set; assert response sources subset |
| Re-index after comment | Write ticket, ask, add comment, ask again—second answer reflects comment (fake embeddings with keyword separation) |
| No LLM on no-match | Mock chat client—verify never called when retrieval empty |

## Probabilistic eval (non-gating or soft gate)

Fixture corpus: 10–20 synthetic tickets with known themes (payment, shipment).

| Metric | Target (initial) | Notes |
|--------|------------------|-------|
| Retrieval recall@k | ≥ 0.8 on labeled question→ticket pairs | Tune threshold in eval only |
| Citation accuracy | 100% on eval set when retrieval correct | Manual or LLM-judge with human review |
| Hallucination rate | 0% ticket ids not in corpus | Automated id check |

Run in separate Maven profile `rag-eval` with real embedding model (optional Ollama locally) or recorded embeddings. **v1**: profile is **optional** and **not** a required CI merge gate (**SC-010**); default CI uses deterministic integration + chunker/builder unit tests.

## Hallucination / grounding review

Use `.cursor/commands/review-rag-output.md` workflow on sample asks; record one documented mistake in **`docs/decisions/ai-mistakes.md`** (spec **SC-009**, TR-017).

## Regression

When changing chunking, model, or threshold defaults, re-run eval profile and update [architecture.md](./architecture.md) rationale section.
