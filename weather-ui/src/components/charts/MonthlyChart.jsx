import { AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

export default function MonthlyChart({ data }) {
  // Transform map of { month -> temp } into an array
  const chartData = data
    ? Object.entries(data).map(([month, temp]) => ({
        month: `Month ${month}`,
        temp,
      }))
    : [];

  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Monthly Temperature Trend</h3>
      <ResponsiveContainer width="100%" height={300}>
        <AreaChart data={chartData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip />
          <Area type="monotone" dataKey="temp" stroke="#2563eb" fill="#93c5fd" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
