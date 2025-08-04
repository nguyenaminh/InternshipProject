import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

export default function DailyChart({ data }) {
  // Transform list of WeatherData objects into Recharts format
  const chartData = Array.isArray(data)
    ? data.map(d => ({
        time: new Date(d.dateTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
        temp: d.temperature,
      }))
    : [];

  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Last 3 Hours Temperature</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="time" />
          <YAxis />
          <Tooltip />
          <Line type="monotone" dataKey="temp" stroke="#2563eb" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
