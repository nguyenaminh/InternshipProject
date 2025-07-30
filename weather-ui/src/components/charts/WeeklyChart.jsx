import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

const sampleData = [
  { day: "Mon", temp: 30 },
  { day: "Tue", temp: 29 },
  { day: "Wed", temp: 31 },
  { day: "Thu", temp: 28 },
  { day: "Fri", temp: 32 },
];

export default function WeeklyChart() {
  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Weekly Temperature</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={sampleData}>
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
