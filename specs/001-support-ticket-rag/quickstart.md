# Quickstart: Validate 001-support-ticket-rag

**Date**: 2026-09-25  
**Prerequisites**: Java 25, Maven 3.9+, Node 20+, Docker (Testcontainers / local PostgreSQL)

## 1. Database (local)

```bash
docker run -d --name atms-pg -e POSTGRES_PASSWORD=atms -e POSTGRES_DB=atms -p 5432:5432 pgvector/pgvector:pg16
```

Configure `backend/src/main/resources/application-local.yml` (implementation) with JDBC URL and `spring.ai` provider env vars.

## 2. Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Liquibase applies schema and enables `vector` extension.

## 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Point API base URL to `http://localhost:8080`.

## 4. Manual smoke (tickets)

1. Create ticket via UI or:

```bash
curl -s -X POST http://localhost:8080/api/tickets \
  -H 'Content-Type: application/json' \
  -d '{"title":"Payment failed","description":"Card declined","priority":"HIGH","assignee":"agent1","category":"payment"}'
```

2. List with filter: `GET /api/tickets?status=OPEN&q=payment`
3. Transition: `PATCH /api/tickets/TKT-0001/status` with `{"status":"IN_PROGRESS"}` then resolve with `resolutionNotes`.
4. Restart backend; `GET` ticket — data still present.

## 5. Manual smoke (ask)

After ticket indexed:

```bash
curl -s -X POST http://localhost:8080/api/ai/ask \
  -H 'Content-Type: application/json' \
  -d '{"question":"Have we seen payment failures before?"}'
```

Expect `noMatch: false`, `sources` containing display id(s), grounded `answer`.

Ask unrelated question with empty index → `noMatch: true`, `sources: []`.

## 6. Automated validation

```bash
cd backend
./mvnw test
```

Includes Testcontainers FSM and RAG integration tests per [test-strategy.md](./test-strategy.md).

Optional:

```bash
./mvnw test -Prag-eval
```

## 7. Contracts

OpenAPI: [contracts/openapi.yaml](./contracts/openapi.yaml). After implementation, verify springdoc export matches this file.

## References

- [data-model.md](./data-model.md)
- [api-contract.md](./api-contract.md)
- [rag-api-contract.md](./rag-api-contract.md)
- [state-machine.md](./state-machine.md)
