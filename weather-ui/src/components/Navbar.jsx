import Filters from "./Filters";

export default function Navbar({ filters, setFilters }) {
  const scrollToTop = (e) => {
    e.preventDefault();
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <nav
      style={{
        background:
          "rgba(30, 64, 175, 0.1)", // less opaque, more transparent
        backdropFilter: "blur(5px)", // glass blur effect
        WebkitBackdropFilter: "blur(10px)",
        borderBottom: "2px solid #3b82f6",
        color: "white",
        padding: "0.75rem 2rem",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        boxShadow: "0 8px 20px rgba(37, 99, 235, 0.3)",
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        width: "98%",
        zIndex: 20,
        flexWrap: "wrap",
        gap: "1rem",
        fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
      }}
    >
      <a href="#"
        onClick={scrollToTop}
        style={{
          textDecoration: "none",
          color: "inherit", // keep text color same as navbar
        }}
      >
        <h1
          style={{
            fontSize: "1.5rem",
            fontWeight: "900",
            userSelect: "none",
            whiteSpace: "nowrap",
            margin: 0,
            textShadow: "0 2px 6px rgba(0,0,0,0.4)",
            letterSpacing: "0.05em",
          }}
        >
          Weather Monitoring Dashboard
        </h1>
      </a>

      <div style={{ flexGrow: 1, maxWidth: "600px" }}>
        <Filters filters={filters} onChange={setFilters} />
      </div>
    </nav>
  );
}
