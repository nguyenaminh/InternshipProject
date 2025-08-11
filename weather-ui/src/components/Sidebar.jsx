import { Link } from "react-router-dom";
import { FaTachometerAlt, FaTable } from "react-icons/fa";

export default function Sidebar() {
  return (
    <aside
      style={{
        width: "180px",
        background: "linear-gradient(180deg, #0f2027, #203a43, #2c5364)",
        color: "#cbd5e1",
        padding: "1.5rem 1rem",
        display: "flex",
        flexDirection: "column",
        position: "fixed",
        top: "100px", // match navbar height
        left: 0,
        height: "calc(100vh - 64px)", // fill the remaining space
        overflowY: "auto",
        boxShadow: "2px 0 10px rgba(15, 32, 39, 0.8)",
        fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
        userSelect: "none",
        zIndex: 1000
      }}
    >
      <h2
        style={{
          marginBottom: "2rem",
          fontSize: "1.3rem",
          fontWeight: "700",
          letterSpacing: "1.2px",
          textAlign: "center",
          color: "#94a3b8",
          textShadow: "0 1px 3px rgba(44, 83, 100, 0.8)",
        }}
      >
        Menu
      </h2>

      <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
        <li style={{ marginBottom: "1.5rem" }}>
          <Link
            to="/"
            style={{
              display: "flex",
              alignItems: "center",
              color: "#cbd5e1",
              textDecoration: "none",
              padding: "0.7rem 1rem",
              borderRadius: "10px",
              fontWeight: "600",
              fontSize: "1.05rem",
              transition: "background-color 0.3s ease, box-shadow 0.3s ease",
              boxShadow: "inset 0 0 0 0 transparent",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = "#1e40af";
              e.currentTarget.style.color = "#fff";
              e.currentTarget.style.boxShadow = "0 0 12px #1e40afcc";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = "transparent";
              e.currentTarget.style.color = "#cbd5e1";
              e.currentTarget.style.boxShadow = "inset 0 0 0 0 transparent";
            }}
          >
            <FaTachometerAlt style={{ marginRight: "0.8rem", fontSize: "1.3rem" }} />
            Dashboard
          </Link>
        </li>

        <li>
          <a
            href="#datatable"
            style={{
              display: "flex",
              alignItems: "center",
              color: "#cbd5e1",
              textDecoration: "none",
              padding: "0.7rem 1rem",
              borderRadius: "10px",
              fontWeight: "600",
              fontSize: "1.05rem",
              transition: "background-color 0.3s ease, box-shadow 0.3s ease",
              cursor: "pointer",
              boxShadow: "inset 0 0 0 0 transparent",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = "#1e40af";
              e.currentTarget.style.color = "#fff";
              e.currentTarget.style.boxShadow = "0 0 12px #1e40afcc";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = "transparent";
              e.currentTarget.style.color = "#cbd5e1";
              e.currentTarget.style.boxShadow = "inset 0 0 0 0 transparent";
            }}
          >
            <FaTable style={{ marginRight: "0.8rem", fontSize: "1.3rem" }} />
            Data Table
          </a>
        </li>
      </ul>
    </aside>
  );
}
