import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Dashboard from "./pages/Dashboard";
import DataTable from "./pages/DataTable";
import Sidebar from "./components/Sidebar";

function App() {
  return (
    <Router>
      <div style={{ display: "flex", minHeight: "100vh", fontFamily: "sans-serif" }}>
        <Sidebar />
        <div style={{ flexGrow: 1 }}>
          <Navbar />
          <div style={{ padding: "2rem" }}>
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/table" element={<DataTable />} />
            </Routes>
          </div>
        </div>
      </div>
    </Router>
  );
}

export default App;
