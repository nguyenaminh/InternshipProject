import { useState } from "react";
import Filters from "../components/Filters";
import DailyChart from "../components/charts/DailyChart";
import WeeklyChart from "../components/charts/WeeklyChart";
import MonthlyChart from "../components/charts/MonthlyChart";

export default function Dashboard() {
  const [filters, setFilters] = useState({
    city: "Hanoi",
    range: "daily", // daily | weekly | monthly
    dataTypes: {
      temperature: true,
      windSpeed: true,
      cloudCover: true,
    },
  });

  return (
    <div className="space-y-6">
      {/* Filters Bar */}
      <Filters onChange={setFilters} />

      {/* Chart Section */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {filters.range === "daily" && <DailyChart filters={filters} />}
        {filters.range === "weekly" && <WeeklyChart filters={filters} />}
        {filters.range === "monthly" && <MonthlyChart filters={filters} />}
      </div>
    </div>
  );
}
