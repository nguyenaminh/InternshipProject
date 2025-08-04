package com.example.iot_producer.service;

import com.example.iot_producer.config.WeatherConfig;
import com.example.iot_producer.model.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class FileReaderService {

    private final MessageSender messageSender;
    private final RestTemplate restTemplate;
    private final WeatherConfig weatherConfig;

    public FileReaderService(MessageSender messageSender, WeatherConfig weatherConfig) {
        this.messageSender = messageSender;
        this.weatherConfig = weatherConfig;
        this.restTemplate = new RestTemplate();
    }

    // Get coordinates for a given city
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

    // Fetch last 3 full hours of weather for a city
    private void fetchLastThreeHours(String city) {
        try {
            double[] coords = getCoordinatesForCity(city);
            double lat = coords[0];
            double lon = coords[1];

            String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f" +
                "&hourly=temperature_2m,windspeed_10m,cloudcover" +
                "&timezone=auto",
                lat, lon
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("hourly")) {
                Map<String, Object> hourly = (Map<String, Object>) response.get("hourly");
                List<String> times = (List<String>) hourly.get("time");
                List<?> tempsRaw = (List<?>) hourly.get("temperature_2m");
                List<?> windsRaw = (List<?>) hourly.get("windspeed_10m");
                List<?> cloudsRaw = (List<?>) hourly.get("cloudcover");

                // Round current time down to nearest hour
                LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

                // Fetch last 3 hours: now, now-1h, now-2h
                for (int back = 2; back >= 0; back--) {
                    LocalDateTime targetTime = now.minusHours(back);
                    int idx = -1;

                    for (int i = 0; i < times.size(); i++) {
                        LocalDateTime hourlyTime = LocalDateTime.parse(times.get(i), DateTimeFormatter.ISO_DATE_TIME);
                        if (hourlyTime.equals(targetTime)) {
                            idx = i;
                            break;
                        }
                    }

                    if (idx >= 0) {
                        Double temperature = tempsRaw.get(idx) != null ? ((Number) tempsRaw.get(idx)).doubleValue() : null;
                        Double windSpeed = windsRaw.get(idx) != null ? ((Number) windsRaw.get(idx)).doubleValue() : null;
                        Double cloudCover = cloudsRaw.get(idx) != null ? ((Number) cloudsRaw.get(idx)).doubleValue() : null;

                        WeatherData data = new WeatherData();
                        data.setCity(city);
                        data.setDateTime(targetTime);
                        data.setTemperature(temperature);
                        data.setWindSpeed(windSpeed);
                        data.setCloudCover(cloudCover);

                        messageSender.sendWeatherData(data);
                        System.out.println("Sent historical weather for " + city + " at " + targetTime +
                                " | Temp=" + temperature + " | Wind=" + windSpeed + " | Cloud=" + cloudCover);
                    } else {
                        System.err.println("No data found for " + city + " at " + targetTime);
                    }
                }
            } else {
                System.err.println("No hourly data found for city: " + city);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching weather for " + city + ": " + e.getMessage());
        }
    }

    // Run immediately on startup
    @PostConstruct
    public void initFetch() {
        List<String> cities = weatherConfig.getCities();
        if (cities == null || cities.isEmpty()) {
            System.err.println("No cities configured for weather fetch.");
            return;
        }
        for (String city : cities) {
            fetchLastThreeHours(city);
        }
    }
}
