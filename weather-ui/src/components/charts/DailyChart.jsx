import { useEffect, useState } from "react";
import {
  LineChart,
  Line,
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
            temp:
              values.temp != null ? parseFloat(values.temp.toFixed(1)) : null,
            wind:
              values.wind != null ? parseFloat(values.wind.toFixed(1)) : null,
            cloud:
              values.cloud != null ? parseFloat(values.cloud.toFixed(0)) : null,
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
    <div
      style={{
        background: "#111827",
        padding: "1rem",
        borderRadius: "8px",
        color: "white",
      }}
    >
      <h3 style={{ marginBottom: "1rem" }}>Daily Temperatures</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
          <CartesianGrid stroke="#374151" strokeDasharray="3 3" />
          <XAxis dataKey="time" interval={3} stroke="#9ca3af" />
          <YAxis stroke="#9ca3af" />
          <Tooltip
            contentStyle={{ backgroundColor: "#1f2937", border: "none", color: "white" }}
            formatter={(value, name) => {
              if (name === "Temp (°C)") return [`${value}°C`, name];
              if (name === "Wind (km/h)") return [`${value} km/h`, name];
              if (name === "Cloud (%)") return [`${value}%`, name];
              return [value, name];
            }}
          />
          <Legend verticalAlign="top" height={36} />

          {dataTypes.temperature && (
            <Line
              type="monotone"
              dataKey="temp"
              name="Temp (°C)"
              stroke="#2563eb"
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
            />
          )}
          {dataTypes.windSpeed && (
            <Line
              type="monotone"
              dataKey="wind"
              name="Wind (km/h)"
              stroke="#10b981"
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
            />
          )}
          {dataTypes.cloudCover && (
            <Line
              type="monotone"
              dataKey="cloud"
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
