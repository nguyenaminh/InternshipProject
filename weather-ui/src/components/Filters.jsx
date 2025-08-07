import { useState } from "react";

export default function Filters({ onChange }) {
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = async () => {
    if (!searchTerm.trim()) return;

    setIsLoading(true);
    try {
      const res = await fetch(
        `https://geocoding-api.open-meteo.com/v1/search?name=${encodeURIComponent(searchTerm)}&count=1`
      );
      const json = await res.json();
      const location = json.results?.[0];

      if (!location) {
        alert("Location not found");
        return;
      }

      // Emit the selected location info
      onChange({
        locationName: `${location.name}, ${location.country}`,
        latitude: location.latitude,
        longitude: location.longitude,
        timezone: location.timezone,
      });
    } catch (error) {
      console.error("Geocoding failed:", error);
      alert("Failed to fetch location data.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSearch();
  };

  return (
    <div className="p-4 flex gap-2">
      <input
        type="text"
        placeholder="Enter city or country"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        onKeyDown={handleKeyDown}
        className="border px-3 py-2 rounded w-64"
      />
      <button
        onClick={handleSearch}
        disabled={isLoading}
        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
      >
        {isLoading ? "Searching..." : "Search"}
      </button>
    </div>
  );
}
