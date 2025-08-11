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
          minHeight: "100vh", // ⬅ changed from height
          fontFamily: "sans-serif",
          color: "white",
          overflowX: "hidden",
          overflowY: "hidden", // keep auto if you want page scroll
          boxSizing: "border-box",
        }}
      >
        <Background />
        <div style={{ position: "relative", zIndex: 1 }}>
          <Navbar filters={filters} setFilters={setFilters} />

          <div style={{ display: "flex", minHeight: "calc(100vh - 64px)" }}> 
            {/* ⬅ changed height to minHeight */}
            <Sidebar />
            <main
              style={{
                flexGrow: 1,
                padding: "2rem",
                overflowY: "auto",
                display: "flex",
                flexDirection: "column",
                marginLeft: "180px", // match sidebar width
                marginTop: "64px"    // match navbar height
              }}
            >
              <Routes>
                <Route path="/" element={<Dashboard filters={filters} />} />
              </Routes>
            </main>
          </div>
        </div>
      </div>
    </Router>
  );
}

export default App;
