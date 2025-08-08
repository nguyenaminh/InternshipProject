import { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
  Legend,
} from "recharts";

export default function DailyChart({ filters }) {
  const { city, dataTypes } = filters;
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    const fetchLast24h = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/weather/latest24h?city=${city}`
        );
        if (!res.ok) throw new Error("Fetch failed");
        const raw = await res.json();

        const dataMap = new Map();
        raw.forEach((d) => {
          const dt = new Date(d.dateTime);
          dt.setMinutes(0, 0, 0);
          dataMap.set(dt.getTime(), {
            temp: d.temperature,
            wind: d.windSpeed,
            cloud: d.cloudCover,
          });
        });

        const now = new Date();
        now.setMinutes(0, 0, 0);

        const slots = [];
        for (let i = 23; i >= 0; i--) {
          const slotTime = new Date(now.getTime() - i * 60 * 60 * 1000);
          const timeLabel = slotTime.toLocaleTimeString("en-GB", {
            hour: "2-digit",
            minute: "2-digit",
          });
          const values = dataMap.get(slotTime.getTime()) || {};
          slots.push({
            time: timeLabel,
            temp: values.temp != null ? parseFloat(values.temp.toFixed(1)) : null,
            wind: values.wind != null ? parseFloat(values.wind.toFixed(1)) : null,
            cloud: values.cloud != null ? parseFloat(values.cloud.toFixed(0)) : null,
          });
        }

        setChartData(slots);
      } catch (err) {
        console.error("DailyChart load error:", err);
        setChartData([]);
      }
    };

    fetchLast24h();
  }, [city]);

  return (
    <div style={{ background: "#111", padding: "1rem", borderRadius: "8px", color: "white" }}>
      <h3 style={{ marginBottom: "1rem" }}>Last 24 Hours - Weather Overview</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid stroke="#333" />
          <XAxis dataKey="time" interval={3} stroke="#ccc" />
          <YAxis stroke="#ccc" />
          <Tooltip
            contentStyle={{ backgroundColor: "#222", border: "none" }}
            formatter={(value, name) => {
              if (name === "Temp (°C)") return [`${value}°C`, name];
              if (name === "Wind (m/s)") return [`${value} m/s`, name];
              if (name === "Cloud (%)") return [`${value}%`, name];
              return [value, name];
            }}
          />
          <Legend />

          <Bar
            dataKey="temp"
            name="Temp (°C)"
            fill="#2563eb"
            hide={!filters.dataTypes.temperature}
          />
          <Bar
            dataKey="wind"
            name="Wind (m/s)"
            fill="#10b981"
            hide={!filters.dataTypes.windSpeed}
          />
          <Bar
            dataKey="cloud"
            name="Cloud (%)"
            fill="#facc15"
            hide={!filters.dataTypes.cloudCover}
          />
        </BarChart>

      </ResponsiveContainer>
    </div>
  );
}
