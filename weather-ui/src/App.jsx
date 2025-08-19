import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Dashboard from "./pages/Dashboard";
import Sidebar from "./components/Sidebar";
import Background from "./components/Background";
import { useState } from "react";

function App() {
  // Move filters state here
  const [filters, setFilters] = useState({
    city: "Hanoi",
    range: "daily", // daily | weekly | monthly
    dataTypes: {
      temperature: true,
      windSpeed: true,
      cloudCover: true,
    },
  });

  return (
    <Router>
      <div
        style={{
          position: "relative",
          minHeight: "100vh",   // fixed height, not min-height
          fontFamily: "sans-serif",
          color: "white",
          overflowX: "hidden",
          overflowY: "auto", // or "auto" if you want scroll inside
          boxSizing: "border-box",
        }}
      >
        {/* Starry background behind everything */}
        <Background />

        {/* Content container with higher zIndex */}
        <div style={{ position: "relative", zIndex: 1 }}>
          {/* Pass filters and setter to Navbar */}
          <Navbar filters={filters} setFilters={setFilters} />

          <div style={{ display: "flex" }}>
            <Sidebar />
            <main style={{ flexGrow: 1, padding: "2rem", overflowY: "auto" }}>
              <Routes>
                <Route path="/" element={<Dashboard filters={filters} />} />
                {/* You can remove /table route if datatable is inside dashboard now */}
                
              </Routes>
            </main>
          </div>
        </div>
      </div>
    </Router>
  );
}

export default App;
