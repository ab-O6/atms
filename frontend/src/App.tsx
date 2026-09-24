import { BrowserRouter, Link, Route, Routes } from "react-router-dom";
import CreateTicketPage from "./features/tickets/CreateTicketPage";
import TicketDetailPage from "./features/tickets/TicketDetailPage";
import TicketListPage from "./features/tickets/TicketListPage";
import "./app.css";

export default function App() {
  return (
    <BrowserRouter>
      <div className="layout">
        <nav className="nav">
          <Link to="/">ATMS Support</Link>
        </nav>
        <main className="main">
          <Routes>
            <Route path="/" element={<TicketListPage />} />
            <Route path="/tickets/new" element={<CreateTicketPage />} />
            <Route path="/tickets/:displayId" element={<TicketDetailPage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}
