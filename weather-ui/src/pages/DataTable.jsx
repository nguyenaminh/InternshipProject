import { useEffect, useState } from "react";

export default function DataTable() {
  const [weatherData, setWeatherData] = useState([]);
  const [city, setCity] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [error, setError] = useState("");
  const [hasNext, setHasNext] = useState(false);
  const [loading, setLoading] = useState(false);
  const [dateError, setDateError] = useState("");
  const [sortBy, setSortBy] = useState("dateTime");
  const [direction, setDirection] = useState("desc");

  const styles = {
    input: {
      padding: "0.5rem",
      borderRadius: 4,
      border: "1px solid #374151",
      backgroundColor: "#1f2937",
      color: "#e5e7eb",
    },
    label: {
      marginBottom: 4,
      fontWeight: "600",
    },
    button: (disabled) => ({
      padding: "0.6rem 1.2rem",
      backgroundColor: disabled ? "#4b5563" : "#2563eb",
      color: "white",
      border: "none",
      borderRadius: 4,
      cursor: disabled ? "not-allowed" : "pointer",
      fontWeight: "600",
    }),
    sortableHeader: {
      cursor: "pointer",
      userSelect: "none",
    },
    sortArrow: {
      marginLeft: 6,
      fontSize: "0.75rem",
      verticalAlign: "middle",
    },
  };

  const validateDates = () => {
    if (startDate && endDate && startDate > endDate) {
      setDateError("Start Date must be earlier or equal to End Date");
      return false;
    }
    setDateError("");
    return true;
  };

  useEffect(() => {
    if (!validateDates()) return;
    fetchFilteredData();
  }, [city, startDate, endDate, page, size, sortBy, direction]);

  async function fetchFilteredData() {
    if (!validateDates()) return;

    setLoading(true);
    try {
      const query = new URLSearchParams({
        ...(city.trim() && { city: city.trim() }),
        ...(startDate && { start: startDate }),
        ...(endDate && { end: endDate }),
        page,
        size,
        sortBy,
        direction,
      });

      const res = await fetch(`http://localhost:8080/api/weather/filter?${query.toString()}`);

      if (!res.ok) throw new Error(`HTTP ${res.status}`);

      const data = await res.json();

      setWeatherData(data.content || []);
      setHasNext(!data.last);
      setError("");
    } catch (err) {
      console.error(err);
      setWeatherData([]);
      setError("Failed to fetch filtered data");
    } finally {
      setLoading(false);
    }
  }

  const toggleSort = (field) => {
    if (sortBy === field) {
      setDirection(direction === "asc" ? "desc" : "asc");
    } else {
      setSortBy(field);
      setDirection("asc");
    }
  };

  const renderSortArrow = (field) => {
    if (sortBy !== field) return null;
    return direction === "asc" ? (
      <span style={styles.sortArrow} aria-label="ascending">
        ▲
      </span>
    ) : (
      <span style={styles.sortArrow} aria-label="descending">
        ▼
      </span>
    );
  };

  const formatDateTime = (dtString) => {
    const dt = new Date(dtString);
    const options = {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    };
    return dt.toLocaleString(undefined, options);
  };

  // Cell styles with nowrap on numeric and city columns for readability
  const cellStyle = {
    padding: "0.5rem",
  };
  const nowrapCell = {
    ...cellStyle,
    whiteSpace: "nowrap",
  };

  return (
    <div
      style={{
        maxWidth: 900,
        margin: "auto",
        padding: "1rem",
        color: "#e5e7eb",
        backgroundColor: "#111827",
        borderRadius: 8,
      }}
    >
      <h2 style={{ marginBottom: "1rem", color: "#f3f4f6" }}>Weather Data Records</h2>

      <form
        onSubmit={(e) => e.preventDefault()}
        style={{
          display: "flex",
          gap: "1rem",
          flexWrap: "wrap",
          marginBottom: "1.5rem",
          color: "#e5e7eb",
        }}
        aria-label="Filter weather data"
      >
        <div style={{ flex: "1 1 150px", display: "flex", flexDirection: "column" }}>
          <label htmlFor="cityInput" style={styles.label}>
            City
          </label>
          <input
            id="cityInput"
            type="text"
            placeholder="Enter city"
            value={city}
            onChange={(e) => {
              setCity(e.target.value);
              setPage(0);
            }}
            style={styles.input}
            aria-label="City filter"
          />
        </div>

        <div style={{ flex: "1 1 150px", display: "flex", flexDirection: "column" }}>
          <label htmlFor="startDate" style={styles.label}>
            Start Date
          </label>
          <input
            id="startDate"
            type="date"
            value={startDate}
            onChange={(e) => {
              setStartDate(e.target.value);
              setPage(0);
            }}
            style={styles.input}
            aria-label="Start date filter"
          />
        </div>

        <div style={{ flex: "1 1 150px", display: "flex", flexDirection: "column" }}>
          <label htmlFor="endDate" style={styles.label}>
            End Date
          </label>
          <input
            id="endDate"
            type="date"
            value={endDate}
            onChange={(e) => {
              setEndDate(e.target.value);
              setPage(0);
            }}
            style={styles.input}
            aria-label="End date filter"
          />
        </div>

        <div style={{ flex: "0 0 100px", display: "flex", flexDirection: "column" }}>
          <label htmlFor="pageSize" style={styles.label}>
            Rows
          </label>
          <select
            id="pageSize"
            value={size}
            onChange={(e) => setSize(Number(e.target.value))}
            style={styles.input}
            aria-label="Rows per page"
          >
            <option value={3}>3</option>
            <option value={5}>5</option>
            <option value={10}>10</option>
          </select>
        </div>
      </form>

      {dateError && (
        <p style={{ color: "#f87171", marginBottom: "1rem", fontWeight: "600" }}>{dateError}</p>
      )}

      {error && (
        <p style={{ color: "#f87171", marginBottom: "1rem", fontWeight: "600" }}>{error}</p>
      )}

      {weatherData.length === 0 && !loading ? (
        <p>No data found for the selected filters.</p>
      ) : (
        <div style={{ overflowX: "auto" }}>
          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              boxShadow: "0 0 10px rgba(0,0,0,0.3)",
              borderRadius: 8,
              backgroundColor: "#1f2937",
              color: "#e5e7eb",
              tableLayout: "auto", // changed to auto for flexible widths
              minWidth: 700,
            }}
            aria-label="Weather data table"
          >
            <thead style={{ backgroundColor: "#2563eb" }}>
              <tr>
                <th
                  style={{
                    padding: "0.75rem",
                    textAlign: "left",
                    ...styles.sortableHeader,
                    minWidth: 50,
                    whiteSpace: "nowrap",
                  }}
                >
                  ID
                </th>
                <th
                  style={{
                    padding: "0.75rem",
                    textAlign: "left",
                    ...styles.sortableHeader,
                    minWidth: 140,
                    whiteSpace: "nowrap",
                  }}
                  onClick={() => toggleSort("dateTime")}
                  tabIndex={0}
                  onKeyDown={(e) => (e.key === "Enter" || e.key === " ") && toggleSort("dateTime")}
                  aria-sort={sortBy === "dateTime" ? direction : "none"}
                  role="button"
                  aria-label={`Sort by Date/Time ${sortBy === "dateTime" ? direction : ""}`}
                >
                  Date/Time
                  {renderSortArrow("dateTime")}
                </th>
                <th
                  style={{
                    padding: "0.75rem",
                    textAlign: "left",
                    ...styles.sortableHeader,
                    minWidth: 120,
                    whiteSpace: "nowrap",
                  }}
                  onClick={() => toggleSort("city")}
                  tabIndex={0}
                  onKeyDown={(e) => (e.key === "Enter" || e.key === " ") && toggleSort("city")}
                  aria-sort={sortBy === "city" ? direction : "none"}
                  role="button"
                  aria-label={`Sort by City ${sortBy === "city" ? direction : ""}`}
                >
                  City
                  {renderSortArrow("city")}
                </th>
                <th
                  style={{
                    padding: "0.75rem",
                    textAlign: "right",
                    ...styles.sortableHeader,
                    minWidth: 120,
                    whiteSpace: "nowrap",
                  }}
                  onClick={() => toggleSort("temperature")}
                  tabIndex={0}
                  onKeyDown={(e) => (e.key === "Enter" || e.key === " ") && toggleSort("temperature")}
                  aria-sort={sortBy === "temperature" ? direction : "none"}
                  role="button"
                  aria-label={`Sort by Temperature ${sortBy === "temperature" ? direction : ""}`}
                >
                  Temperature (°C)
                  {renderSortArrow("temperature")}
                </th>
                <th
                  style={{
                    padding: "0.75rem",
                    textAlign: "right",
                    ...styles.sortableHeader,
                    minWidth: 120,
                    whiteSpace: "nowrap",
                  }}
                  onClick={() => toggleSort("windSpeed")}
                  tabIndex={0}
                  onKeyDown={(e) => (e.key === "Enter" || e.key === " ") && toggleSort("windSpeed")}
                  aria-sort={sortBy === "windSpeed" ? direction : "none"}
                  role="button"
                  aria-label={`Sort by Wind Speed ${sortBy === "windSpeed" ? direction : ""}`}
                >
                  Wind Speed (km/h)
                  {renderSortArrow("windSpeed")}
                </th>
                <th
                  style={{
                    padding: "0.75rem",
                    textAlign: "right",
                    ...styles.sortableHeader,
                    minWidth: 120,
                    whiteSpace: "nowrap",
                  }}
                  onClick={() => toggleSort("cloudCover")}
                  tabIndex={0}
                  onKeyDown={(e) => (e.key === "Enter" || e.key === " ") && toggleSort("cloudCover")}
                  aria-sort={sortBy === "cloudCover" ? direction : "none"}
                  role="button"
                  aria-label={`Sort by Cloud Cover ${sortBy === "cloudCover" ? direction : ""}`}
                >
                  Cloud Cover (%)
                  {renderSortArrow("cloudCover")}
                </th>
              </tr>
            </thead>
            <tbody>
              {weatherData.map((entry, idx) => (
                <tr
                  key={entry.id}
                  style={{
                    backgroundColor: idx % 2 === 0 ? "#374151" : "#1f2937",
                    cursor: "default",
                    transition: "background-color 0.3s",
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = "#2563eb")}
                  onMouseLeave={(e) =>
                    (e.currentTarget.style.backgroundColor = idx % 2 === 0 ? "#374151" : "#1f2937")
                  }
                  tabIndex={0}
                  aria-label={`Record ${entry.id} for city ${entry.city} on ${formatDateTime(
                    entry.dateTime
                  )}`}
                >
                  <td style={{ ...cellStyle, whiteSpace: "nowrap" }}>{entry.id}</td>
                  <td style={{ ...cellStyle, whiteSpace: "nowrap" }}>{formatDateTime(entry.dateTime)}</td>
                  <td style={{ ...cellStyle, whiteSpace: "nowrap" }}>{entry.city}</td>
                  <td style={{ ...cellStyle, textAlign: "right", whiteSpace: "nowrap" }}>{entry.temperature}</td>
                  <td style={{ ...cellStyle, textAlign: "right", whiteSpace: "nowrap" }}>{entry.windSpeed}</td>
                  <td style={{ ...cellStyle, textAlign: "right", whiteSpace: "nowrap" }}>{entry.cloudCover}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div
        style={{
          marginTop: "1rem",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          gap: "1rem",
        }}
      >
        <button
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={page === 0}
          style={{
            padding: "0.5rem 1rem",
            borderRadius: 4,
            border: "1px solid #374151",
            cursor: page === 0 ? "not-allowed" : "pointer",
            backgroundColor: page === 0 ? "#1f2937" : "#2563eb",
            color: page === 0 ? "#6b7280" : "white",
            fontWeight: "600",
          }}
          aria-disabled={page === 0}
          aria-label="Previous page"
        >
          Prev
        </button>
        <span style={{ fontWeight: "600", color: "#e5e7eb" }}>Page {page + 1}</span>
        <button
          onClick={() => setPage((p) => p + 1)}
          disabled={!hasNext}
          style={{
            padding: "0.5rem 1rem",
            borderRadius: 4,
            border: "1px solid #374151",
            cursor: !hasNext ? "not-allowed" : "pointer",
            backgroundColor: !hasNext ? "#1f2937" : "#2563eb",
            color: !hasNext ? "#6b7280" : "white",
            fontWeight: "600",
          }}
          aria-disabled={!hasNext}
          aria-label="Next page"
        >
          Next
        </button>
      </div>
    </div>
  );
}
