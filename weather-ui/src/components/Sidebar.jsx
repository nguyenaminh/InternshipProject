import { Link } from "react-router-dom";

export default function Sidebar() {
  return (
    <aside style={{
      width: "200px",
      background: "#1e3a8a",
      color: "white",
      padding: "1rem"
    }}>
      <h2 style={{ marginBottom: "1rem" }}>Menu</h2>
      <ul style={{ listStyle: "none", padding: 0 }}>
        <li style={{ marginBottom: "0.5rem" }}>
          <Link to="/" style={{ color: "white", textDecoration: "none" }}>Dashboard</Link>
        </li>
        <li>
          <Link to="/table" style={{ color: "white", textDecoration: "none" }}>Data Table</Link>
        </li>
      </ul>
    </aside>
  );
}
