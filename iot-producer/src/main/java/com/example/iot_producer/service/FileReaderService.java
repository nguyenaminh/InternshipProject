package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class FileReaderService {

    private final MessageSender messageSender;
    private final RestTemplate restTemplate;

    // Default city for continuous collection (can extend to a list later)
    private static final String DEFAULT_CITY = "Hanoi";

    public FileReaderService(MessageSender messageSender) {
        this.messageSender = messageSender;
        this.restTemplate = new RestTemplate();
    }

    // Get latitude & longitude for a given city
    private double[] getCoordinatesForCity(String city) {
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + city + "&count=1";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("results")) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (!results.isEmpty()) {
                Map<String, Object> first = results.get(0);
                double lat = Double.parseDouble(first.get("latitude").toString());
                double lon = Double.parseDouble(first.get("longitude").toString());
                return new double[]{lat, lon};
            }
        }
        throw new RuntimeException("Could not find coordinates for city: " + city);
    }

    // Fetch current weather for a city
    public void fetchCurrentWeather(String city) {
        try {
            double[] coords = getCoordinatesForCity(city);
            double lat = coords[0];
            double lon = coords[1];

            String url = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&hourly=relative_humidity_2m,precipitation",
                    lat, lon
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("current_weather")) {
                Map<String, Object> current = (Map<String, Object>) response.get("current_weather");

                Double temperature = current.get("temperature") != null
                        ? Double.valueOf(current.get("temperature").toString())
                        : null;

                Double humidity = null;
                Double rainfall = 0.0;

                if (response.containsKey("hourly")) {
                    Map<String, Object> hourly = (Map<String, Object>) response.get("hourly");
                    List<?> humsRaw = (List<?>) hourly.get("relative_humidity_2m");
                    List<?> rainsRaw = (List<?>) hourly.get("precipitation");

                    if (!humsRaw.isEmpty() && !rainsRaw.isEmpty()) {
                        Number humVal = (Number) humsRaw.get(0);
                        Number rainVal = (Number) rainsRaw.get(0);

                        humidity = humVal != null ? humVal.doubleValue() : null;
                        rainfall = rainVal != null ? rainVal.doubleValue() : 0.0;
                    }
                }

                WeatherData data = new WeatherData();
                data.setCity(city);
                data.setDateTime(LocalDateTime.now());
                data.setTemperature(temperature);
                data.setHumidity(humidity);
                data.setRainfall(rainfall);

                messageSender.sendWeatherData(data);
                System.out.println("Sent current weather for: " + city + " at " + LocalDateTime.now());
            } else {
                System.err.println("Failed to fetch current weather for city: " + city);
            }
        } catch (Exception e) {
            System.err.println("Error fetching current weather: " + e.getMessage());
        }
    }

    // Fetch historical weather (optional for manual runs later)
    public void fetchHistoricalWeather(String city, String startDate, String endDate) {
        try {
            double[] coords = getCoordinatesForCity(city);
            double lat = coords[0];
            double lon = coords[1];

            String url = String.format(
                    "https://archive-api.open-meteo.com/v1/era5?latitude=%f&longitude=%f&start_date=%s&end_date=%s&hourly=temperature_2m,relative_humidity_2m,precipitation",
                    lat, lon, startDate, endDate
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("hourly")) {
                Map<String, Object> hourly = (Map<String, Object>) response.get("hourly");
                List<String> times = (List<String>) hourly.get("time");
                List<?> tempsRaw = (List<?>) hourly.get("temperature_2m");
                List<?> humsRaw = (List<?>) hourly.get("relative_humidity_2m");
                List<?> rainsRaw = (List<?>) hourly.get("precipitation");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

                for (int i = 0; i < times.size(); i++) {
                    WeatherData data = new WeatherData();
                    data.setCity(city);
                    data.setDateTime(LocalDateTime.parse(times.get(i), formatter));
                    data.setTemperature(tempsRaw.get(i) != null ? ((Number) tempsRaw.get(i)).doubleValue() : null);
                    data.setHumidity(humsRaw.get(i) != null ? ((Number) humsRaw.get(i)).doubleValue() : null);
                    data.setRainfall(rainsRaw.get(i) != null ? ((Number) rainsRaw.get(i)).doubleValue() : null);

                    messageSender.sendWeatherData(data);
                }
                System.out.println("Sent " + times.size() + " historical records for: " + city);
            } else {
                System.err.println("Failed to fetch historical weather for: " + city);
            }
        } catch (Exception e) {
            System.err.println("Error fetching historical weather: " + e.getMessage());
        }
    }

    // 🔁 Scheduled task: fetch weather every 5 minutes
    @Scheduled(fixedRate = 300000) // every 300000 ms = 5 minutes
    public void scheduledWeatherFetch() {
        fetchCurrentWeather(DEFAULT_CITY);
    }
}
