import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { formatApiError, listTickets, type TicketSummary } from "../../api/tickets";

export default function TicketListPage() {
  const [tickets, setTickets] = useState<TicketSummary[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    listTickets()
      .then(setTickets)
      .catch((err) => setError(formatApiError(err)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <section>
      <header className="page-header">
        <h1>Tickets</h1>
        <Link to="/tickets/new" className="button">Create ticket</Link>
      </header>
      {loading && <p>Loading…</p>}
      {error && <p className="error" role="alert">{error}</p>}
      {!loading && !error && tickets.length === 0 && (
        <p className="empty">No tickets yet.</p>
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
