import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

export default function WeeklyChart({ data }) {
  const chartData = data 
    ? Object.entries(data).map(([day, temp]) => ({ day, temp }))
    : [];

  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Weekly Temperature</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="day" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="temp" fill="#2563eb" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
