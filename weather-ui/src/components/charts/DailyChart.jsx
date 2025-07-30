import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

const sampleData = [
  { hour: "00:00", temp: 25 },
  { hour: "06:00", temp: 28 },
  { hour: "12:00", temp: 32 },
  { hour: "18:00", temp: 29 }
];

export default function DailyChart() {
  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Daily Temperature</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={sampleData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="hour" />
          <YAxis />
          <Tooltip />
          <Line type="monotone" dataKey="temp" stroke="#2563eb" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
