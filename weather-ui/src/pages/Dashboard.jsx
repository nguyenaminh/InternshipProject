import Filters from "../components/Filters";
import StatsCard from "../components/StatsCard";
import DailyChart from "../components/charts/DailyChart";
import WeeklyChart from "../components/charts/WeeklyChart";
import MonthlyChart from "../components/charts/MonthlyChart";

export default function Dashboard() {
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "2rem" }}>
      {/* Filters */}
      <Filters />

      {/* Stats Section */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: "1rem" }}>
        <StatsCard title="Temperature" value="--" />
        <StatsCard title="Humidity" value="--" />
        <StatsCard title="Wind Speed" value="--" />
        <StatsCard title="Records" value="--" />
      </div>

      {/* Charts */}
      <DailyChart />
      <WeeklyChart />
      <MonthlyChart />
    </div>
  );
}
