import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

export default function DailyChart({ data }) {
  const now = new Date();
  
  // Generate the last 9 hours (including current)
  const hours = Array.from({ length: 9 }, (_, i) => {
    const date = new Date(now);
    date.setHours(now.getHours() - (8 - i), 0, 0, 0); // go back 8 → 0 hours
    return date;
  });

  // Map backend data by hour for quick lookup
  const tempByHour = {};
  if (Array.isArray(data)) {
    data.forEach(d => {
      const hour = new Date(d.dateTime).getHours();
      tempByHour[hour] = d.temperature;
    });
  }

  // Build chart data for each of the 9 hours
  const chartData = hours.map(h => ({
    time: h.toLocaleTimeString([], { hour: "2-digit" }),
    temp: tempByHour[h.getHours()] ?? null,
  }));

  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Last 9 Hours Temperature</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="time" />
          <YAxis />
          <Tooltip />
          <Line type="monotone" dataKey="temp" stroke="#2563eb" connectNulls />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
