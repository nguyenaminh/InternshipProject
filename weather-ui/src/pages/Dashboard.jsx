import { useState, useEffect } from "react";
import { WiThermometer, WiStrongWind, WiCloud } from "react-icons/wi";
import DailyChart from "../components/charts/DailyChart";
import WeeklyChart from "../components/charts/WeeklyChart";
import MonthlyChart from "../components/charts/MonthlyChart";
import DataTable from "./DataTable";

export default function Dashboard({ filters }) {
  const [latestStats, setLatestStats] = useState({
    temperature: null,
    windSpeed: null,
    cloudCover: null,
  });
  const [loadingStats, setLoadingStats] = useState(false);

  useEffect(() => {
    async function fetchLatestStats() {
      setLoadingStats(true);
      try {
        const cityParam = encodeURIComponent(filters.city);
        const res = await fetch(`http://localhost:8080/api/weather/latest24h?city=${cityParam}`);
        if (!res.ok) throw new Error("Failed to fetch latest stats");
        const data = await res.json();

        const latest = data.length > 0 ? data[data.length - 1] : null;

        setLatestStats({
          temperature: latest?.temperature ?? null,
          windSpeed: latest?.windSpeed ?? null,
          cloudCover: latest?.cloudCover ?? null,
        });
      } catch (error) {
        console.error("Error loading latest stats:", error);
        setLatestStats({
          temperature: null,
          windSpeed: null,
          cloudCover: null,
        });
      } finally {
        setLoadingStats(false);
      }
    }

    fetchLatestStats();
  }, [filters.city]);

  const icons = [WiThermometer, WiStrongWind, WiCloud];

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        gap: "1.5rem",
        background: "#111827",
        minHeight: "100vh",
        padding: "1rem",
        color: "#e5e7eb",
      }}
    >
      {/* Stats Overview Cards */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
          gap: "1rem",
        }}
      >
        {[
          {
            title: "Current Temperature",
            value:
              loadingStats
                ? "Loading..."
                : latestStats.temperature != null
                ? `${latestStats.temperature.toFixed(1)}°C`
                : "N/A",
            color: "#fbbf24",
          },
          {
            title: "Wind Speed",
            value:
              loadingStats
                ? "Loading..."
                : latestStats.windSpeed != null
                ? `${latestStats.windSpeed.toFixed(1)} km/h`
                : "N/A",
            color: "#60a5fa",
          },
          {
            title: "Cloud Cover",
            value:
              loadingStats
                ? "Loading..."
                : latestStats.cloudCover != null
                ? `${latestStats.cloudCover.toFixed(0)}%`
                : "N/A",
            color: "#a78bfa",
          },
        ].map((stat, idx) => {
          const Icon = icons[idx];
          return (
            <div
              key={idx}
              style={{
                background: "#1f2937",
                padding: "1rem",
                borderRadius: "0.5rem",
                boxShadow: "0 2px 4px rgba(0,0,0,0.3)",
                display: "flex",
                alignItems: "center",
                gap: "1rem",
              }}
            >
              <Icon size={60} color={stat.color} />
              <div>
                <h3
                  style={{
                    fontSize: "1.125rem",
                    marginBottom: "0.75rem",
                    color: "#d1d5db",
                  }}
                >
                  {stat.title}
                </h3>
                <p
                  style={{
                    fontSize: "1.75rem",
                    fontWeight: "bold",
                    color: stat.color,
                    margin: 0,
                  }}
                >
                  {stat.value}
                </p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Charts */}
      <div style={{ display: "grid", gridTemplateColumns: "1fr", gap: "1rem" }}>
        {filters.range === "daily" && (
          <div
            style={{
              background: "#1f2937",
              padding: "1rem",
              borderRadius: "0.5rem",
              boxShadow: "0 4px 6px rgba(0,0,0,0.3)",
            }}
          >
            <DailyChart filters={filters} />
          </div>
        )}
        {filters.range === "weekly" && (
          <div
            style={{
              background: "#1f2937",
              padding: "1rem",
              borderRadius: "0.5rem",
              boxShadow: "0 4px 6px rgba(0,0,0,0.3)",
            }}
          >
            <WeeklyChart filters={filters} />
          </div>
        )}
        {filters.range === "monthly" && (
          <div
            style={{
              background: "#1f2937",
              padding: "1rem",
              borderRadius: "0.5rem",
              boxShadow: "0 4px 6px rgba(0,0,0,0.3)",
            }}
          >
            <MonthlyChart filters={filters} />
          </div>
        )}
      </div>

      {/* Data Table */}
      <div
        id="datatable"
        style={{
          background: "#1f2937",
          padding: "1rem",
          borderRadius: "0.5rem",
          boxShadow: "0 4px 6px rgba(0,0,0,0.3)",
        }}
      >
        <DataTable />
      </div>
    </div>
  );
}
