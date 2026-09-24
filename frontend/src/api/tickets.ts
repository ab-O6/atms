import { ApiError, apiFetch } from "./client";

export type Priority = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
export type TicketStatus =
  | "OPEN"
  | "IN_PROGRESS"
  | "RESOLVED"
  | "CLOSED"
  | "CANCELLED";

export type TicketSummary = {
  displayId: string;
  title: string;
  status: TicketStatus;
  priority: Priority;
  assignee: string;
  updatedAt: string;
};

export type Comment = {
  id: string;
  body: string;
  author: string;
  createdAt: string;
};

export type TicketDetail = TicketSummary & {
  description: string;
  category: string;
  resolutionNotes: string | null;
  createdAt: string;
  comments: Comment[];
};

export type CreateTicketPayload = {
  title: string;
  description: string;
  priority: Priority;
  assignee: string;
  category?: string;
};

export type UpdateTicketPayload = {
  title?: string;
  description?: string;
  priority?: Priority;
  assignee?: string;
};

export function formatApiError(error: unknown): string {
  if (error instanceof ApiError) {
    return error.problem?.detail ?? error.message;
  }
  if (error instanceof Error) {
    return error.message;
  }
  return "Unexpected error";
}

export async function listTickets(): Promise<TicketSummary[]> {
  return apiFetch<TicketSummary[]>("/api/tickets");
}

export async function getTicket(displayId: string): Promise<TicketDetail> {
  return apiFetch<TicketDetail>(`/api/tickets/${encodeURIComponent(displayId)}`);
}

export async function createTicket(payload: CreateTicketPayload): Promise<TicketDetail> {
  return apiFetch<TicketDetail>("/api/tickets", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export async function updateTicket(
  displayId: string,
  payload: UpdateTicketPayload,
): Promise<TicketDetail> {
  return apiFetch<TicketDetail>(`/api/tickets/${encodeURIComponent(displayId)}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export async function addComment(
  displayId: string,
  body: string,
  author: string,
): Promise<Comment> {
  return apiFetch<Comment>(`/api/tickets/${encodeURIComponent(displayId)}/comments`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ body, author }),
  });
}
