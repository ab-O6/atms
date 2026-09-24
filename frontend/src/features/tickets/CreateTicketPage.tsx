import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  createTicket,
  formatApiError,
  type Priority,
} from "../../api/tickets";

const priorities: Priority[] = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];

export default function CreateTicketPage() {
  const navigate = useNavigate();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<Priority>("MEDIUM");
  const [assignee, setAssignee] = useState("");
  const [category, setCategory] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const created = await createTicket({
        title,
        description,
        priority,
        assignee,
        category: category.trim() === "" ? undefined : category,
      });
      navigate(`/tickets/${created.displayId}`);
    } catch (err) {
      setError(formatApiError(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section>
      <header className="page-header">
        <h1>Create ticket</h1>
        <Link to="/">Back to list</Link>
      </header>
      <form onSubmit={onSubmit} className="form">
        <label>
          Title
          <input value={title} onChange={(e) => setTitle(e.target.value)} required maxLength={200} />
        </label>
        <label>
          Description
          <textarea value={description} onChange={(e) => setDescription(e.target.value)} required rows={5} />
        </label>
        <label>
          Priority
          <select value={priority} onChange={(e) => setPriority(e.target.value as Priority)}>
            {priorities.map((p) => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </label>
        <label>
          Assignee
          <input value={assignee} onChange={(e) => setAssignee(e.target.value)} required maxLength={120} />
        </label>
        <label>
          Category (optional)
          <input value={category} onChange={(e) => setCategory(e.target.value)} maxLength={80} />
        </label>
        {error && <p className="error" role="alert">{error}</p>}
        <button type="submit" className="button" disabled={submitting}>
          {submitting ? "Creating…" : "Create"}
        </button>
      </form>
    </section>
  );
}
