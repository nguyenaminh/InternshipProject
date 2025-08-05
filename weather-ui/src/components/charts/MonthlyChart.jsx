import { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

export default function MonthlyChart({ city = "Hanoi" }) {
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    const fetchMonthlyData = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/weather/monthly/last12?city=${city}`
        );
        const data = await res.json();

        const months = Object.entries(data).map(([month, value]) => ({
          label: new Date(month + "-01").toLocaleString("default", {
            month: "short",
            year: "2-digit",
          }),
          value: value ? parseFloat(value.toFixed(1)) : null,
        }));

        setChartData(months);
      } catch (err) {
        console.error("Error fetching monthly data:", err);
        setChartData([]);
      }
    };

    fetchMonthlyData();
  }, [city]);

  return (
    <div style={{ background: "white", padding: "1rem", borderRadius: "8px" }}>
      <h3>Last 12 Months Average Temperature</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid stroke="#ccc" />
          <XAxis dataKey="label" />
          <YAxis tickFormatter={(val) => val.toFixed(1)} />
          <Tooltip formatter={(val) => `${val.toFixed(1)}°C`} />
          <Bar dataKey="value" fill="#2563eb" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
