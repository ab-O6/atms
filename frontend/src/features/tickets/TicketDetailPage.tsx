import { FormEvent, useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  addComment,
  formatApiError,
  getTicket,
  updateTicket,
  type Priority,
  type TicketDetail,
} from "../../api/tickets";

const priorities: Priority[] = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];

export default function TicketDetailPage() {
  const { displayId } = useParams<{ displayId: string }>();
  const [ticket, setTicket] = useState<TicketDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [commentError, setCommentError] = useState<string | null>(null);
  const [commentBody, setCommentBody] = useState("");
  const [commentAuthor, setCommentAuthor] = useState("");
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    if (!displayId) return;
    setError(null);
    try {
      setTicket(await getTicket(displayId));
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

  return (
    <section>
      <header className="page-header">
        <h1>{ticket.displayId}</h1>
        <Link to="/">Back to list</Link>
      </header>
      <p><strong>Status:</strong> {ticket.status}</p>
      {ticket.category && <p><strong>Category:</strong> {ticket.category}</p>}
      <form onSubmit={onSave} className="form">
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
