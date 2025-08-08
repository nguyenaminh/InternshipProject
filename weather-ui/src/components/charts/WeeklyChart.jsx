import { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";

export default function WeeklyChart({ filters }) {
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    const fetchWeeklyData = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/weather/weekly/last7?city=${filters.city}`
        );
        if (!res.ok) throw new Error("Failed to fetch");
        const data = await res.json();

        const formatted = Object.entries(data).map(([date, values]) => ({
          label: new Date(date).toLocaleDateString("en-GB", {
            weekday: "short",
            day: "numeric",
          }),
          temperature: values.temperature?.toFixed(1),
          windSpeed: values.windSpeed?.toFixed(1),
          cloudCover: values.cloudCover?.toFixed(1),
        }));

        setChartData(formatted);
      } catch (err) {
        console.error("Error fetching weekly data:", err);
        setChartData([]);
      }
    };

    fetchWeeklyData();
  }, [filters.city]);

  return (
    <div style={{ background: "black", padding: "1rem", borderRadius: "8px" }}>
      <h3 style={{ color: "white" }}>Last 7 Days Weather Summary</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="label" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar
            dataKey="temperature"
            name="Temp (°C)"
            fill="#2563eb"
            hide={!filters.dataTypes.temperature}
          />
          <Bar
            dataKey="windSpeed"
            name="Wind (m/s)"
            fill="#10b981"
            hide={!filters.dataTypes.windSpeed}
          />
          <Bar
            dataKey="cloudCover"
            name="Cloud (%)"
            fill="#facc15"
            hide={!filters.dataTypes.cloudCover}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
