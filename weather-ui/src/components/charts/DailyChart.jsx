import { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";

export default function DailyChart({ city = "Hanoi" }) {
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    const fetchLast24h = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/weather/latest24h?city=${city}`
        );
        if (!res.ok) throw new Error("Fetch failed");
        const raw = await res.json(); // Array<WeatherData>

        // Build map of hour slot -> temperature (could be null)
        const tempMap = new Map();
        raw.forEach((d) => {
          const dt = new Date(d.dateTime);
          dt.setMinutes(0, 0, 0);
          tempMap.set(dt.getTime(), d.temperature);
        });

        // Align now to the latest exact past hour
        const now = new Date();
        now.setMinutes(0, 0, 0);

        const slots = [];
        for (let i = 23; i >= 0; i--) {
          const slotTime = new Date(now.getTime() - i * 60 * 60 * 1000);
          const timeLabel = slotTime.toLocaleTimeString("en-GB", {
            hour: "2-digit",
            minute: "2-digit",
          });
          const rawTemp = tempMap.get(slotTime.getTime());
          const temp = rawTemp != null ? parseFloat(rawTemp.toFixed(1)) : null;
          slots.push({ time: timeLabel, temp });
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
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Last 24 Hours Temperature</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid stroke="#eee" />
          <XAxis dataKey="time" interval={3} />
          <YAxis />
          <Tooltip formatter={(value) => (value != null ? `${value}°C` : "N/A")} />
          <Bar dataKey="temp" fill="#2563eb" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
