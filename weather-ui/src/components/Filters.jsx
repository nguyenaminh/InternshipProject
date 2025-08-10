import { useState, useEffect } from "react";

const defaultFilters = {
  city: "Hanoi",
  range: "daily",
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

  const formatCity = (cityStr) =>
    cityStr
      .trim()
      .split(" ")
      .filter(Boolean)
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(" ");

  const handleCitySearch = async (e) => {
    e.preventDefault();
    const formattedCity = formatCity(inputCity);
    if (!formattedCity) return;

    try {
      const res = await fetch(
        `http://localhost:8081/api/produce/fetch?city=${encodeURIComponent(formattedCity)}`,
        { method: "POST" }
      );
      if (!res.ok) throw new Error("Producer fetch failed");
      console.log(`Fetch triggered for city: ${formattedCity}`);

      await new Promise((resolve) => setTimeout(resolve, 2000));
    } catch (err) {
      console.error("Failed to trigger backend fetch:", err);
    }

    setFilters((prev) => ({ ...prev, city: formattedCity }));
    setInputCity(formattedCity);
  };

  return (
    <div
      style={{
        display: "flex",
        flexWrap: "wrap",
        gap: "0.5rem",
        alignItems: "center",
        marginBottom: "0.5rem",
      }}
    >
      {/* City / Country Search */}
      <form
        onSubmit={handleCitySearch}
        style={{ display: "flex", gap: "0.5rem", flexGrow: 1, minWidth: 150 }}
      >
        <input
          type="text"
          value={inputCity}
          placeholder="City or country"
          onChange={(e) => setInputCity(e.target.value)}
          style={{
            padding: "0.3rem 0.5rem",
            borderRadius: "4px",
            flexGrow: 1,
            fontSize: "0.9rem",
          }}
          aria-label="City search input"
        />
        <button
          type="submit"
          style={{
            background: "#2563eb",
            color: "white",
            border: "none",
            padding: "0.3rem 0.8rem",
            borderRadius: "4px",
            cursor: "pointer",
            fontSize: "0.9rem",
            fontWeight: "600",
          }}
        >
          Search
        </button>
      </form>

      {/* Date Range Buttons */}
      <div style={{ display: "flex", gap: "0.25rem", flexWrap: "nowrap" }}>
        {["daily", "weekly", "monthly"].map((type) => (
          <button
            key={type}
            onClick={() => handleRangeChange(type)}
            style={{
              background: filters.range === type ? "#10b981" : "#e5e7eb",
              color: filters.range === type ? "white" : "black",
              border: "none",
              padding: "0.3rem 0.6rem",
              borderRadius: "4px",
              cursor: "pointer",
              fontSize: "0.85rem",
              whiteSpace: "nowrap",
            }}
          >
            {type.charAt(0).toUpperCase() + type.slice(1)}
          </button>
        ))}
      </div>

      {/* Data Types Checkboxes */}
      <div
        style={{
          display: "flex",
          gap: "0.5rem",
          alignItems: "center",
          fontSize: "0.85rem",
          whiteSpace: "nowrap",
        }}
      >
        <label style={{ display: "flex", alignItems: "center", gap: "0.15rem" }}>
          <input
            type="checkbox"
            checked={filters.dataTypes.temperature}
            onChange={() => handleCheckboxChange("temperature")}
          />
          Temp
        </label>
        <label style={{ display: "flex", alignItems: "center", gap: "0.15rem" }}>
          <input
            type="checkbox"
            checked={filters.dataTypes.windSpeed}
            onChange={() => handleCheckboxChange("windSpeed")}
          />
          Wind
        </label>
        <label style={{ display: "flex", alignItems: "center", gap: "0.15rem" }}>
          <input
            type="checkbox"
            checked={filters.dataTypes.cloudCover}
            onChange={() => handleCheckboxChange("cloudCover")}
          />
          Cloud
        </label>
      </div>
    </div>
  );
}
