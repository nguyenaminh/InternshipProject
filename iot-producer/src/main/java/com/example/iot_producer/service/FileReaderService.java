package com.example.iot_producer.service;

import com.example.iot_producer.config.WeatherConfig;
import com.example.iot_producer.model.WeatherData;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
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

    // --- helper to check if weather data exists in Consumer DB ---
    private boolean weatherDataExists(String city, LocalDateTime dateTime) {
        try {
            LocalDateTime start = dateTime.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = dateTime.withMinute(59).withSecond(59).withNano(999999999);

            String url = String.format(
                "http://consumer:8080/api/weather/filter?city=%s&start=%s&end=%s&page=0&size=1",
                city,
                start,
                end
            );

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("content")) {
                List<?> content = (List<?>) response.getBody().get("content");
                return !content.isEmpty();
            }
        } catch (Exception e) {
            System.err.println("Error checking existing data for " + city + " at " + dateTime + ": " + e.getMessage());
        }
        return false;
    }

    private void fetchLast24Hours(String city) {
        try {
            double[] coords = getCoordinatesForCity(city);
            double lat = coords[0], lon = coords[1];

            String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f" +
                "&past_days=1" +
                "&hourly=temperature_2m,windspeed_10m,cloudcover" +
                "&timezone=auto",
                lat, lon
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || !response.containsKey("hourly")) return;

            Map<String, Object> hourly = (Map<String, Object>) response.get("hourly");
            List<String> times = (List<String>) hourly.get("time");
            List<?> tempsRaw = (List<?>) hourly.get("temperature_2m");
            List<?> windsRaw = (List<?>) hourly.get("windspeed_10m");
            List<?> cloudsRaw = (List<?>) hourly.get("cloudcover");

            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime from = now.minusHours(23);

            int skipped = 0;
            int sent = 0;

            for (int i = 0; i < times.size(); i++) {
                LocalDateTime recordTime = LocalDateTime.parse(times.get(i), DateTimeFormatter.ISO_DATE_TIME);

                if (!recordTime.isBefore(from) && !recordTime.isAfter(now) && recordTime.getMinute() == 0) {

                    if (weatherDataExists(city, recordTime)) {
                        skipped++;
                        continue;
                    }

                    Double temperature = tempsRaw.get(i) != null ? Math.round(((Number) tempsRaw.get(i)).doubleValue() * 100.0) / 100.0 : null;
                    Double windSpeed  = windsRaw.get(i)  != null ? Math.round(((Number) windsRaw.get(i)).doubleValue()  * 100.0) / 100.0 : null;
                    Double cloudCover = cloudsRaw.get(i) != null ? Math.round(((Number) cloudsRaw.get(i)).doubleValue() * 100.0) / 100.0 : null;

                    WeatherData data = new WeatherData();
                    data.setCity(city);
                    data.setDateTime(recordTime);
                    data.setTemperature(temperature);
                    data.setWindSpeed(windSpeed);
                    data.setCloudCover(cloudCover);

                    messageSender.sendWeatherData(data);
                    sent++;
                }
            }

            System.out.println("[Fetch] City: " + city + " | Sent: " + sent + " | Skipped (already in DB): " + skipped);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching weather for " + city + ": " + e.getMessage());
        }
    }

    private void fetchLastYearData(String city) {
        try {
            double[] coords = getCoordinatesForCity(city);
            double lat = coords[0];
            double lon = coords[1];

            String url = String.format(
                "https://archive-api.open-meteo.com/v1/archive?latitude=%f&longitude=%f" +
                "&start_date=%s&end_date=%s" +
                "&daily=temperature_2m_max,temperature_2m_min,windspeed_10m_max,cloudcover_mean" +
                "&timezone=auto",
                lat, lon,
                LocalDate.now().minusMonths(12).withDayOfMonth(1),
                LocalDate.now().minusDays(2)
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("daily")) {
                Map<String, Object> daily = (Map<String, Object>) response.get("daily");
                List<String> times = (List<String>) daily.get("time");
                List<?> tempMax = (List<?>) daily.get("temperature_2m_max");
                List<?> tempMin = (List<?>) daily.get("temperature_2m_min");
                List<?> windMax = (List<?>) daily.get("windspeed_10m_max");
                List<?> cloudMean = (List<?>) daily.get("cloudcover_mean");

                int skippedCount = 0;
                int savedCount = 0;

                for (int i = 0; i < times.size(); i++) {
                    LocalDateTime date = LocalDate.parse(times.get(i)).atStartOfDay().withNano(0);

                    if (weatherDataExists(city, date)) {
                        skippedCount++;
                        continue;
                    }

                    Double avgTemp = null;
                    if (tempMax.get(i) != null && tempMin.get(i) != null) {
                        avgTemp = (((Number) tempMax.get(i)).doubleValue() + ((Number) tempMin.get(i)).doubleValue()) / 2;
                        avgTemp = Math.round(avgTemp * 100.0) / 100.0;
                    }

                    Double windSpeed = windMax.get(i) != null ? Math.round(((Number) windMax.get(i)).doubleValue() * 100.0) / 100.0 : null;
                    Double cloudCover = cloudMean.get(i) != null ? Math.round(((Number) cloudMean.get(i)).doubleValue() * 100.0) / 100.0 : null;

                    WeatherData data = new WeatherData();
                    data.setCity(city);
                    data.setDateTime(date);
                    data.setTemperature(avgTemp);
                    data.setWindSpeed(windSpeed);
                    data.setCloudCover(cloudCover);

                    messageSender.sendWeatherData(data);
                    savedCount++;
                }

                System.out.printf("Backfill completed for city: %s — Saved: %d, Skipped: %d%n", city, savedCount, skippedCount);
            }
        } catch (Exception e) {
            System.err.println("Error fetching last year data for " + city);
            e.printStackTrace();
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

        boolean consumerReady = false;
        int attempts = 0;

        while (!consumerReady && attempts < 20) {
            try {
                restTemplate.getForObject("http://consumer:8080/api/weather/health", String.class);
                consumerReady = true;
                System.out.println("Consumer is ready!");
            } catch (Exception e) {
                attempts++;
                System.out.println("Waiting for consumer... attempt " + attempts);
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        }

        if (!consumerReady) {
            System.err.println("Consumer not available after 20 attempts. Aborting startup data fetch.");
            return;
        }

        for (String city : cities) {
            fetchLast24Hours(city);
            fetchLastYearData(city);
        }
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes (in milliseconds)
    public void refreshWeatherEvery5Minutes() {
        List<String> cities = weatherConfig.getCities();
        if (cities == null || cities.isEmpty()) {
            System.err.println("No cities configured for scheduled weather refresh.");
            return;
        }

        System.out.println("Scheduled fetch: " + cities);
        for (String city : cities) {
            fetchLast24Hours(city); // already avoids duplicates inside
        }
    }

    // public void triggerFetchForCity(String city) {
    //     System.out.println("Triggering fetch for city: " + city);
    //     fetchLast24Hours(city);
    // }
}
