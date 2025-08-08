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

export default function MonthlyChart({ filters }) {
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    const fetchMonthlyData = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/weather/monthly/last12?city=${filters.city}`
        );
        if (!res.ok) throw new Error("Failed to fetch");
        const data = await res.json();

        const formatted = Object.entries(data).map(([month, values]) => ({
          label: new Date(month + "-01").toLocaleDateString("en-GB", {
            month: "short",
            year: "2-digit",
          }),
          temperature: values.temperature?.toFixed(1),
          windSpeed: values.windSpeed?.toFixed(1),
          cloudCover: values.cloudCover?.toFixed(1),
        }));

        setChartData(formatted);
      } catch (err) {
        console.error("Error fetching monthly data:", err);
        setChartData([]);
      }
    };

    fetchMonthlyData();
  }, [filters.city]);

  return (
    <div style={{ background: "black", padding: "1rem", borderRadius: "8px" }}>
      <h3 style={{ color: "white" }}>Last 12 Months Weather Summary</h3>
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
