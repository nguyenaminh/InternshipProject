export default function StatsCard({ title, value}) {
    return (
        <div style={{
            background: "white",
            borderRadius: "8px",
            padding: "1rem",
            boxShadow: "0 1px 3px rgba(0,0,0,0,1)",
            textAlign: "center"
        }}>
            <h3 style={{ color: "#4b5563", marginBottom: "0.5rem" }}>{title}</h3>
            <p style={{ fontSize: "1.25rem", fontWeight: "bold"}}>{value}</p>
        </div>
    );
}