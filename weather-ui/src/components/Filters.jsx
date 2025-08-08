import { useState, useEffect } from "react";

const defaultFilters = {
  city: "Hanoi",
  range: "daily", // daily | weekly | monthly
  dataTypes: {
    temperature: true,
    windSpeed: true,
    cloudCover: true,
  },
};

export default function Filters({ onChange }) {
  const [filters, setFilters] = useState(defaultFilters);
  const [inputCity, setInputCity] = useState(defaultFilters.city);

  useEffect(() => {
    onChange(filters);
  }, [filters, onChange]);

  const handleRangeChange = (range) => {
    setFilters((prev) => ({ ...prev, range }));
  };

  const handleCheckboxChange = (key) => {
    setFilters((prev) => ({
      ...prev,
      dataTypes: {
        ...prev.dataTypes,
        [key]: !prev.dataTypes[key],
      },
    }));
  };

  const handleCitySearch = async (e) => {
    e.preventDefault();
    const city = inputCity.trim().toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
    if (!city) return;

    try {
      // 1. Trigger the producer to fetch weather data for the searched city
      const res = await fetch(`http://localhost:8081/api/produce/fetch?city=${encodeURIComponent(city)}`, {
        method: "POST",
      });

      if (!res.ok) throw new Error("Producer fetch failed");
      console.log(`Fetch triggered for city: ${city}`);

      // 2. Wait for the backend to finish producing + consumer to save data
      await new Promise((resolve) => setTimeout(resolve, 2000)); 
    } catch (err) {
      console.error("Failed to trigger backend fetch:", err);
    }

    // 3. Now set filter state to re-trigger data fetch with the new city
    setFilters((prev) => ({ ...prev, city }));
  };



  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1rem", marginBottom: "1rem" }}>
      {/* City / Country Search */}
      <form onSubmit={handleCitySearch} style={{ display: "flex", gap: "0.5rem" }}>
        <input
          type="text"
          value={inputCity}
          placeholder="Enter city or country"
          onChange={(e) => setInputCity(e.target.value)}
          style={{ padding: "0.5rem", borderRadius: "6px", flex: "1" }}
        />
        <button
          type="submit"
          style={{
            background: "#2563eb",
            color: "white",
            border: "none",
            padding: "0.5rem 1rem",
            borderRadius: "6px",
            cursor: "pointer",
          }}
        >
          Search
        </button>
      </form>

      {/* Date Range Buttons */}
      <div style={{ display: "flex", gap: "1rem" }}>
        {["daily", "weekly", "monthly"].map((type) => (
          <button
            key={type}
            onClick={() => handleRangeChange(type)}
            style={{
              background: filters.range === type ? "#10b981" : "#e5e7eb",
              color: filters.range === type ? "white" : "black",
              border: "none",
              padding: "0.5rem 1rem",
              borderRadius: "6px",
              cursor: "pointer",
            }}
          >
            {type.charAt(0).toUpperCase() + type.slice(1)}
          </button>
        ))}
      </div>

      {/* Data Types Checkboxes */}
      <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
        <label>
          <input
            type="checkbox"
            checked={filters.dataTypes.temperature}
            onChange={() => handleCheckboxChange("temperature")}
          />
          Temperature
        </label>
        <label>
          <input
            type="checkbox"
            checked={filters.dataTypes.windSpeed}
            onChange={() => handleCheckboxChange("windSpeed")}
          />
          Wind Speed
        </label>
        <label>
          <input
            type="checkbox"
            checked={filters.dataTypes.cloudCover}
            onChange={() => handleCheckboxChange("cloudCover")}
          />
          Cloud Cover
        </label>
      </div>
    </div>
  );
}
