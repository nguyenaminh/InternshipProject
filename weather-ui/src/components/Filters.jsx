import { useState } from "react";

export default function Filters({ onChange }) {
  const [city, setCity] = useState("Hanoi");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const handleToday = () => {
    const today = new Date().toISOString().slice(0, 10);
    setStartDate(today);
    setEndDate(today);
    onChange({ city, date: today });
  };

  const handleThisWeek = () => {
    const today = new Date();
    const monday = new Date(today.setDate(today.getDate() - today.getDay() + 1))
      .toISOString()
      .slice(0, 10);
    const sunday = new Date(today.setDate(today.getDate() - today.getDay() + 7))
      .toISOString()
      .slice(0, 10);
    setStartDate(monday);
    setEndDate(sunday);
    onChange({ city, start: monday, end: sunday });
  };

  const handleThisMonth = () => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
      .toISOString()
      .slice(0, 10);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      .toISOString()
      .slice(0, 10);
    setStartDate(firstDay);
    setEndDate(lastDay);
    onChange({ city, start: firstDay, end: lastDay });
  };

  return (
    <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
      <select value={city} onChange={(e) => { setCity(e.target.value); onChange({ city: e.target.value }); }}>
        <option value="Hanoi">Hanoi</option>
        <option value="StationA">Station A</option>
        <option value="StationB">Station B</option>
      </select>
      <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
      <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
      <button onClick={handleToday}>Today</button>
      <button onClick={handleThisWeek}>This Week</button>
      <button onClick={handleThisMonth}>This Month</button>
    </div>
  );
}
