import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  formatApiError,
  listTickets,
  type TicketStatus,
  type TicketSummary,
} from "../../api/tickets";

const STATUS_OPTIONS: { value: TicketStatus | ""; label: string }[] = [
  { value: "", label: "All statuses" },
  { value: "OPEN", label: "Open" },
  { value: "IN_PROGRESS", label: "In progress" },
  { value: "RESOLVED", label: "Resolved" },
  { value: "CLOSED", label: "Closed" },
  { value: "CANCELLED", label: "Cancelled" },
];

export default function TicketListPage() {
  const [tickets, setTickets] = useState<TicketSummary[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState<TicketStatus | "">("");

  const loadTickets = useCallback(() => {
    setLoading(true);
    setError(null);
    listTickets({
      q: keyword,
      status,
    })
      .then(setTickets)
      .catch((err) => setError(formatApiError(err)))
      .finally(() => setLoading(false));
  }, [keyword, status]);

  useEffect(() => {
    loadTickets();
  }, [loadTickets]);

  return (
    <section>
      <header className="page-header">
        <h1>Tickets</h1>
        <Link to="/tickets/new" className="button">Create ticket</Link>
      </header>
      <form
        className="filters"
        onSubmit={(e) => {
          e.preventDefault();
          loadTickets();
        }}
      >
        <label>
          Search
          <input
            type="search"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Title, description, or ID"
            aria-label="Search tickets"
          />
        </label>
        <label>
          Status
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value as TicketStatus | "")}
            aria-label="Filter by status"
          >
            {STATUS_OPTIONS.map((opt) => (
              <option key={opt.value || "all"} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </label>
        <button type="submit" className="button button-secondary">Apply</button>
      </form>
      {loading && <p>Loading…</p>}
      {error && <p className="error" role="alert">{error}</p>}
      {!loading && !error && tickets.length === 0 && (
        <p className="empty">No tickets match your filters.</p>
      )}
      {!loading && tickets.length > 0 && (
        <table className="ticket-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Status</th>
              <th>Priority</th>
              <th>Assignee</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map((t) => (
              <tr key={t.displayId}>
                <td>
                  <Link to={`/tickets/${t.displayId}`}>{t.displayId}</Link>
                </td>
                <td>{t.title}</td>
                <td>{t.status}</td>
                <td>{t.priority}</td>
                <td>{t.assignee}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
