export default function Filters() {
    return (
        <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
            <select>
                <option>Station A</option>
                <option>Station B</option>
            </select>
            <input type="date" />
            <input type="date" />
            <button>Today</button>
            <button>This Week</button>
            <button>This Month</button>
        </div>
    );
}