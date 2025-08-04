import { useEffect, useState } from "react";

export default function DataTable() {
  const [weatherData, setWeatherData] = useState([]);
  const [city, setCity] = useState("");
  const [startDateTime, setStartDateTime] = useState("");
  const [endDateTime, setEndDateTime] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [error, setError] = useState("");
  const [hasNext, setHasNext] = useState(false);

  const fetchFilteredData = () => {
    const query = new URLSearchParams({
      ...(city && { city }),
      ...(startDateTime && { start: startDateTime }),
      ...(endDateTime && { end: endDateTime }),
      page,
      size,
      sortBy: "dateTime",
      direction: "desc",
    });

    fetch(`http://localhost:8080/api/weather/filter?${query.toString()}`)
      .then((res) => {
        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        setWeatherData(data.content || []);
        setHasNext(!data.last);
        setError("");
      })
      .catch((err) => {
        console.error(err);
        setWeatherData([]);
        setError("Failed to fetch filtered data");
      });
  };

  useEffect(() => {
    fetchFilteredData();
  }, [page, size]);

  const handleFilter = (e) => {
    e.preventDefault();
    setPage(0);
    fetchFilteredData();
  };

  return (
    <div>
      <h2>Weather Data Records</h2>

      <form onSubmit={handleFilter} style={{ marginBottom: "1rem" }}>
        <input
          type="text"
          placeholder="City"
          value={city}
          onChange={(e) => setCity(e.target.value)}
        />
        <input
          type="date"
          value={startDateTime}
          onChange={(e) => setStartDateTime(e.target.value)}
        />
        <input
          type="date"
          value={endDateTime}
          onChange={(e) => setEndDateTime(e.target.value)}
        />
        <select value={size} onChange={(e) => setSize(Number(e.target.value))}>
          <option value={3}>3</option>
          <option value={5}>5</option>
          <option value={10}>10</option>
        </select>
        <button type="submit">Filter</button>
      </form>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {weatherData.length === 0 ? (
        <p>No data found.</p>
      ) : (
        <table border="1" cellPadding="8" style={{ width: "100%", marginTop: "1rem" }}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Date/Time</th>
              <th>City</th>
              <th>Temperature (°C)</th>
              <th>Wind Speed (km/h)</th>
              <th>Cloud Cover (%)</th>
            </tr>
          </thead>
          <tbody>
            {weatherData.map((entry) => (
              <tr key={entry.id}>
                <td>{entry.id}</td>
                <td>{new Date(entry.dateTime).toLocaleString()}</td>
                <td>{entry.city}</td>
                <td>{entry.temperature}</td>
                <td>{entry.windSpeed}</td>
                <td>{entry.cloudCover}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <div style={{ marginTop: "1rem" }}>
        <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
          Prev
        </button>
        <span style={{ margin: "0 1rem" }}>Page {page + 1}</span>
        <button onClick={() => setPage((p) => p + 1)} disabled={!hasNext}>
          Next
        </button>
      </div>
    </div>
  );
}
