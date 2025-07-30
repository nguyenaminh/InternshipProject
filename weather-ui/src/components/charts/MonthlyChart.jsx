import { AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

const sampleData = [
  { day: 1, temp: 30 },
  { day: 5, temp: 28 },
  { day: 10, temp: 32 },
  { day: 15, temp: 29 },
  { day: 20, temp: 31 },
  { day: 25, temp: 30 },
];

export default function MonthlyChart() {
  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Monthly Temperature Trend</h3>
      <ResponsiveContainer width="100%" height={300}>
        <AreaChart data={sampleData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="day" />
          <YAxis />
          <Tooltip />
          <Area type="monotone" dataKey="temp" stroke="#2563eb" fill="#93c5fd" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
