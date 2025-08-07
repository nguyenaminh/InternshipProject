import { useState } from "react";
import Filters from "../components/Filters";
import DailyChart from "../components/charts/DailyChart";
import WeeklyChart from "../components/charts/WeeklyChart";
import MonthlyChart from "../components/charts/MonthlyChart";

export default function Dashboard() {
  const [filters, setFilters] = useState({
    city: "Hanoi",
    date: "today",
  });

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <DailyChart filters={filters} />
      <WeeklyChart filters={filters} />
      <MonthlyChart filters={filters} />
    </div>
  );
}
