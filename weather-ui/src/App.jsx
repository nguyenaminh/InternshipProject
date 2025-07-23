import { useEffect, useState } from 'react';

function App() {
  const [weatherData, setWeatherData] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetch('http://localhost:8080/api/weather')
      .then((res) => {
        if (!res.ok) {
          throw new Error(`HTTP error! Status: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        setWeatherData(data);
      })
      .catch((err) => {
        console.error(err);
        setError(err.message);
      });
  }, []);

  return (
    <div style={{ padding: '2rem'}}>
      <h1>Weather Data</h1>
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      {weatherData.length === 0 ? (
        <p>No data available</p>
      ) : (
        <ul>
          {weatherData.map((entry) => (
            <li key={entry.id}>
              {entry.stationCode} | {entry.temperature}°C | {entry.humidity}% | {entry.dateTime}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default App;