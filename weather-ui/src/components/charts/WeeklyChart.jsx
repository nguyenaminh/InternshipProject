import { useEffect, useState } from "react";
import {
  LineChart,
  Line,
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
            weekday: "short"
          }),
          temperature: values.temperature != null ? parseFloat(values.temperature.toFixed(1)) : null,
          windSpeed: values.windSpeed != null ? parseFloat(values.windSpeed.toFixed(1)) : null,
          cloudCover: values.cloudCover != null ? parseFloat(values.cloudCover.toFixed(1)) : null,
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
    <div style={{ background: "#111827", padding: "1rem", borderRadius: "8px", color: "white" }}>
      <h3 style={{ marginBottom: "1rem" }}>Last 7 Days Weather Summary</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
          <CartesianGrid stroke="#374151" strokeDasharray="3 3" />
          <XAxis dataKey="label" stroke="#9ca3af" />
          <YAxis stroke="#9ca3af" />
          <Tooltip
            contentStyle={{ backgroundColor: "#1f2937", border: "none", color: "white" }}
            formatter={(value, name) => {
              if (name === "Temp (°C)") return [`${value}°C`, name];
              if (name === "Wind (m/s)") return [`${value} m/s`, name];
              if (name === "Cloud (%)") return [`${value}%`, name];
              return [value, name];
            }}
          />
          <Legend verticalAlign="top" height={36} />
          {filters.dataTypes.temperature && (
            <Line
              type="monotone"
              dataKey="temperature"
              name="Temp (°C)"
              stroke="#2563eb"
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
            />
          )}
          {filters.dataTypes.windSpeed && (
            <Line
              type="monotone"
              dataKey="windSpeed"
              name="Wind (m/s)"
              stroke="#10b981"
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
            />
          )}
          {filters.dataTypes.cloudCover && (
            <Line
              type="monotone"
              dataKey="cloudCover"
              name="Cloud (%)"
              stroke="#facc15"
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
            />
          )}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
