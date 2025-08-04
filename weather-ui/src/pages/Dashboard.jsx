import { useEffect, useState } from "react";
import Filters from "../components/Filters";
import DailyChart from "../components/charts/DailyChart";
import WeeklyChart from "../components/charts/WeeklyChart";
import MonthlyChart from "../components/charts/MonthlyChart";

export default function Dashboard() {
  const [dailyData, setDailyData] = useState([]);
  const [weeklyData, setWeeklyData] = useState({});
  const [monthlyData, setMonthlyData] = useState({});
  const [filters, setFilters] = useState({
    city: "Hanoi",
    date: new Date().toISOString().slice(0, 10),
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Latest 3 hours
        const latestRes = await fetch("http://localhost:8080/api/weather/latest");
        setDailyData(await latestRes.json());

        // Weekly stats
        const weeklyRes = await fetch(
          `http://localhost:8080/api/weather/daily?city=${filters.city}&month=${filters.date.slice(0,7)}`
        );
        setWeeklyData(await weeklyRes.json());

        // Monthly stats
        const monthlyRes = await fetch(
          `http://localhost:8080/api/weather/monthly?city=${filters.city}&year=${filters.date.slice(0,4)}`
        );
        setMonthlyData(await monthlyRes.json());
      } catch (err) {
        console.error("Error fetching dashboard data:", err);
      }
    };

    fetchData();
  }, [filters]);

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "2rem" }}>
      <Filters onChange={setFilters} />
      <DailyChart data={dailyData} />
      <WeeklyChart data={weeklyData} />
      <MonthlyChart data={monthlyData} />
    </div>
  );
}
