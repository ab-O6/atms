# UI Flow

**Date**: 2026-09-25  
**Frontend**: React + TypeScript (`strict`)

## Screens

### 1. Ticket list

- Table/cards: display id, title, status, priority, assignee, updated time.
- **Search** input → `GET /api/tickets?q=...`
- **Status filter** dropdown → `GET /api/tickets?status=...`
- Empty search/filter → empty state message.
- Actions: Create ticket, open detail.

### 2. Create ticket

- Fields: title, description, priority, assignee, optional category.
- Submit → `POST /api/tickets`; on 400 show field errors from ProblemDetail `errors[]`.
- Success → navigate to detail.

### 3. Ticket detail

- Show all fields, comments thread, resolution notes (read-only unless `RESOLVED`).
- Edit title/description/priority/assignee → `PATCH`.
- Add comment form → `POST .../comments`.
- Status actions (buttons limited by FSM): e.g. Start → `IN_PROGRESS`, Resolve (modal with resolution notes), Close, Cancel.
- Illegal transition → show `detail` from 409 ProblemDetail.

### 4. Ask (RAG)

- Panel or route: question textarea, Ask button.
- `POST /api/ai/ask`
- **Match**: show `answer` + **Sources** list (display ids linking to ticket detail).
- **noMatch**: show `answer` and empty sources; distinct empty state styling.
- Loading and error states for 400/500.

## Error UX

- Map `ProblemDetail.detail` (and validation extensions) to inline or toast messages—no raw stack traces.

## Citations display

- Render `sources` as chips/links to `/tickets/{displayId}`.
- Do not show internal UUIDs.
