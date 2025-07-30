import { Link } from "react-router-dom";

export default function Navbar() {
    return (
        <nav style={{ background: "#87f8e9ff", color: "white", padding: "1rem"}}>
            <h1 style={{ display: "inline-block", marginRight: "2rem"}}>
                Weather Monitoring Dashboard
            </h1>
            <Link to="/" style={{ marginRight: "1rem", color: "white"}}>Dashboard</Link>
            <Link to="/table" style={{ color: "white"}}>Data Table</Link>
        </nav>
    );
}