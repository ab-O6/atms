import { FormEvent, useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  addComment,
  formatApiError,
  getTicket,
  transitionTicketStatus,
  updateResolutionNotes,
  updateTicket,
  type Priority,
  type TicketDetail,
  type TicketStatus,
} from "../../api/tickets";

const priorities: Priority[] = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];

type TransitionAction = {
  label: string;
  status: TicketStatus;
  needsResolutionNotes?: boolean;
};

function nextActions(status: TicketStatus): TransitionAction[] {
  switch (status) {
    case "OPEN":
      return [
        { label: "Start work", status: "IN_PROGRESS" },
        { label: "Cancel ticket", status: "CANCELLED" },
      ];
    case "IN_PROGRESS":
      return [
        { label: "Mark resolved", status: "RESOLVED", needsResolutionNotes: true },
        { label: "Cancel ticket", status: "CANCELLED" },
      ];
    case "RESOLVED":
      return [{ label: "Close ticket", status: "CLOSED" }];
    default:
      return [];
  }
}

export default function TicketDetailPage() {
  const { displayId } = useParams<{ displayId: string }>();
  const [ticket, setTicket] = useState<TicketDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [statusError, setStatusError] = useState<string | null>(null);
  const [notesError, setNotesError] = useState<string | null>(null);
  const [commentError, setCommentError] = useState<string | null>(null);
  const [commentBody, setCommentBody] = useState("");
  const [commentAuthor, setCommentAuthor] = useState("");
  const [resolveNotes, setResolveNotes] = useState("");
  const [resolutionNotesEdit, setResolutionNotesEdit] = useState("");
  const [saving, setSaving] = useState(false);
  const [statusBusy, setStatusBusy] = useState(false);

  const load = useCallback(async () => {
    if (!displayId) return;
    setError(null);
    try {
      const detail = await getTicket(displayId);
      setTicket(detail);
      setResolutionNotesEdit(detail.resolutionNotes ?? "");
    } catch (err) {
      setError(formatApiError(err));
    }
  }, [displayId]);

  useEffect(() => {
    load();
  }, [load]);

  async function onSave(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!ticket || !displayId) return;
    setSaveError(null);
    setSaving(true);
    const form = new FormData(event.currentTarget);
    try {
      const updated = await updateTicket(displayId, {
        title: String(form.get("title") ?? ""),
        description: String(form.get("description") ?? ""),
        priority: String(form.get("priority") ?? "MEDIUM") as Priority,
        assignee: String(form.get("assignee") ?? ""),
      });
      setTicket(updated);
    } catch (err) {
      setSaveError(formatApiError(err));
    } finally {
      setSaving(false);
    }
  }

  async function onTransition(action: TransitionAction) {
    if (!displayId) return;
    setStatusError(null);
    setStatusBusy(true);
    try {
      const notes = action.needsResolutionNotes ? resolveNotes : undefined;
      const updated = await transitionTicketStatus(displayId, action.status, notes);
      setTicket(updated);
      setResolveNotes("");
      setResolutionNotesEdit(updated.resolutionNotes ?? "");
    } catch (err) {
      setStatusError(formatApiError(err));
    } finally {
      setStatusBusy(false);
    }
  }

  async function onUpdateResolutionNotes(event: FormEvent) {
    event.preventDefault();
    if (!displayId) return;
    setNotesError(null);
    try {
      const updated = await updateResolutionNotes(displayId, resolutionNotesEdit);
      setTicket(updated);
    } catch (err) {
      setNotesError(formatApiError(err));
    }
  }

  async function onAddComment(event: FormEvent) {
    event.preventDefault();
    if (!displayId) return;
    setCommentError(null);
    try {
      await addComment(displayId, commentBody, commentAuthor);
      setCommentBody("");
      await load();
    } catch (err) {
      setCommentError(formatApiError(err));
    }
  }

  if (!displayId) {
    return <p className="error">Missing ticket id.</p>;
  }

  if (error) {
    return (
      <section>
        <p className="error" role="alert">{error}</p>
        <Link to="/">Back to list</Link>
      </section>
    );
  }

  if (!ticket) {
    return <p>Loading…</p>;
  }

  const actions = nextActions(ticket.status);

  return (
    <section>
      <header className="page-header">
        <h1>{ticket.displayId}</h1>
        <Link to="/">Back to list</Link>
      </header>
      <p><strong>Status:</strong> {ticket.status}</p>
      {ticket.resolutionNotes && (
        <p><strong>Resolution notes:</strong> {ticket.resolutionNotes}</p>
      )}
      {ticket.category && <p><strong>Category:</strong> {ticket.category}</p>}

      {actions.length > 0 && (
        <section className="status-actions">
          <h2>Status actions</h2>
          {actions.some((a) => a.needsResolutionNotes) && (
            <label>
              Resolution notes (required to resolve)
              <textarea
                value={resolveNotes}
                onChange={(e) => setResolveNotes(e.target.value)}
                rows={3}
              />
            </label>
          )}
          <div className="status-buttons">
            {actions.map((action) => (
              <button
                key={action.status}
                type="button"
                className="button button-secondary"
                disabled={statusBusy}
                onClick={() => onTransition(action)}
              >
                {action.label}
              </button>
            ))}
          </div>
          {statusError && <p className="error" role="alert">{statusError}</p>}
        </section>
      )}

      {ticket.status === "RESOLVED" && (
        <section className="form">
          <h2>Edit resolution notes</h2>
          <form onSubmit={onUpdateResolutionNotes}>
            <label>
              Resolution notes
              <textarea
                value={resolutionNotesEdit}
                onChange={(e) => setResolutionNotesEdit(e.target.value)}
                required
                minLength={1}
                rows={4}
              />
            </label>
            {notesError && <p className="error" role="alert">{notesError}</p>}
            <button type="submit" className="button">Save resolution notes</button>
          </form>
        </section>
      )}

      <form onSubmit={onSave} className="form">
        <h2>Ticket details</h2>
        <label>
          Title
          <input name="title" defaultValue={ticket.title} required maxLength={200} />
        </label>
        <label>
          Description
          <textarea name="description" defaultValue={ticket.description} required rows={5} />
        </label>
        <label>
          Priority
          <select name="priority" defaultValue={ticket.priority}>
            {priorities.map((p) => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </label>
        <label>
          Assignee
          <input name="assignee" defaultValue={ticket.assignee} required maxLength={120} />
        </label>
        {saveError && <p className="error" role="alert">{saveError}</p>}
        <button type="submit" className="button" disabled={saving}>
          {saving ? "Saving…" : "Save changes"}
        </button>
      </form>
      <section className="comments">
        <h2>Comments</h2>
        <ul>
          {ticket.comments.map((c) => (
            <li key={c.id}>
              <strong>{c.author}</strong> <time>{new Date(c.createdAt).toLocaleString()}</time>
              <p>{c.body}</p>
            </li>
          ))}
        </ul>
        <form onSubmit={onAddComment} className="form">
          <label>
            Author
            <input value={commentAuthor} onChange={(e) => setCommentAuthor(e.target.value)} required maxLength={120} />
          </label>
          <label>
            Comment
            <textarea value={commentBody} onChange={(e) => setCommentBody(e.target.value)} required rows={3} />
          </label>
          {commentError && <p className="error" role="alert">{commentError}</p>}
          <button type="submit" className="button">Add comment</button>
        </form>
      </section>
    </section>
  );
}
