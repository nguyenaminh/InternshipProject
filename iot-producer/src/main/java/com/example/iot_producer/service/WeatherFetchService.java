package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WeatherFetchService {

    private final MessageSender messageSender;
    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherFetchService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void fetchAndSendRange(String city, int daysBack) {
        city = city.toLowerCase(); // Normalize city

        if (hasCityData(city, daysBack)) {
            System.out.printf("Data already exists for the past %d days in city: %s%n", daysBack, city);
            return;
        }

        double[] coords = getCoordinates(city);
        double lat = coords[0];
        double lon = coords[1];

        if (lat == 0.0 && lon == 0.0) {
            System.err.println("Could not get valid coordinates for city: " + city);
            return;
        }

        for (int i = 0; i < daysBack; i++) {
            LocalDateTime targetDate = LocalDateTime.now().minusDays(i);
            String dateStr = targetDate.toLocalDate().toString();

            String url = UriComponentsBuilder.fromHttpUrl("https://api.open-meteo.com/v1/forecast")
                    .queryParam("latitude", lat)
                    .queryParam("longitude", lon)
                    .queryParam("start_date", dateStr)
                    .queryParam("end_date", dateStr)
                    .queryParam("hourly", "temperature_2m,wind_speed_10m,cloud_cover")
                    .queryParam("timezone", "auto")
                    .build()
                    .toUriString();

            var response = restTemplate.getForObject(url, HourlyWeatherResponse.class);
            if (response != null && response.hourly != null) {
                for (int j = 0; j < response.hourly.time.length; j++) {
                    WeatherData data = new WeatherData();
                    data.setCity(city); // Already normalized
                    data.setDateTime(LocalDateTime.parse(response.hourly.time[j]));
                    data.setTemperature(response.hourly.temperature_2m[j]);
                    data.setWindSpeed(response.hourly.wind_speed_10m[j]);
                    data.setCloudCover(response.hourly.cloud_cover[j]);

                    messageSender.sendWeatherData(data);
                }
            } else {
                throw new RuntimeException("Failed to fetch weather data for date: " + dateStr);
            }
        }
    }

    public void fetchLastYearData(String city) {
        city = city.toLowerCase(); // Normalize city

        if (hasCityYearlyData(city)) {
            System.out.printf("Last year data already exists for city: %s%n", city);
            return;
        }

        try {
            double[] coords = getCoordinates(city);
            double lat = coords[0];
            double lon = coords[1];

            if (lat == 0.0 && lon == 0.0) {
                System.err.println("Could not get valid coordinates for city: " + city);
                return;
            }

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

                for (int i = 0; i < times.size(); i++) {
                    LocalDateTime date = LocalDate.parse(times.get(i)).atStartOfDay().withNano(0);

                    Double avgTemp = null;
                    if (tempMax.get(i) != null && tempMin.get(i) != null) {
                        avgTemp = (((Number) tempMax.get(i)).doubleValue() + ((Number) tempMin.get(i)).doubleValue()) / 2;
                        avgTemp = Math.round(avgTemp * 100.0) / 100.0;
                    }

                    Double windSpeed = windMax.get(i) != null ? Math.round(((Number) windMax.get(i)).doubleValue() * 100.0) / 100.0 : null;
                    Double cloudCover = cloudMean.get(i) != null ? Math.round(((Number) cloudMean.get(i)).doubleValue() * 100.0) / 100.0 : null;

                    WeatherData data = new WeatherData();
                    data.setCity(city); // Already normalized
                    data.setDateTime(date);
                    data.setTemperature(avgTemp);
                    data.setWindSpeed(windSpeed);
                    data.setCloudCover(cloudCover);

                    messageSender.sendWeatherData(data);
                }

                System.out.printf("Last year data fetched for city: %s%n", city);
            }
        } catch (Exception e) {
            System.err.println("Error fetching last year data for " + city);
            e.printStackTrace();
        }
    }

    private boolean hasCityData(String city, int daysBack) {
        city = city.toLowerCase(); // Normalize before sending to consumer

        String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/api/weather/exists-range")
                .queryParam("city", city)
                .queryParam("daysBack", daysBack)
                .build()
                .toUriString();

        try {
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            System.err.println("Error checking city data existence: " + e.getMessage());
            return false;
        }
    }

    private boolean hasCityYearlyData(String city) {
        city = city.toLowerCase(); // Normalize

        String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/api/weather/exists-year")
                .queryParam("city", city)
                .build()
                .toUriString();

        try {
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            System.err.println("Error checking yearly data existence: " + e.getMessage());
            return false;
        }
    }

    // --- New method to get coordinates from OpenStreetMap Nominatim ---

    private double[] getCoordinates(String city) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://nominatim.openstreetmap.org/search")
                    .queryParam("q", city)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .build()
                    .toUriString();

            // Proper User-Agent required by Nominatim
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "WeatherMonitoringApp/1.0 (myemail@example.com)");
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            List<Map<String, Object>> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                Map<String, Object> location = body.get(0);
                double lat = Double.parseDouble((String) location.get("lat"));
                double lon = Double.parseDouble((String) location.get("lon"));
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinates for city '" + city + "': " + e.getMessage());
        }
        // fallback to zero if not found
        return new double[]{0.0, 0.0};
    }


    private static class HourlyWeatherResponse {
        public Hourly hourly;

        static class Hourly {
            public String[] time;
            public double[] temperature_2m;
            public double[] wind_speed_10m;
            public double[] cloud_cover;
        }
    }
}
