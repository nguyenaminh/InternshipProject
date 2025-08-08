package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Service
public class WeatherFetchService {

    private final MessageSender messageSender;
    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherFetchService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void fetchAndSendRange(String city, int daysBack) {
        double lat = getLat(city);
        double lon = getLon(city);

        for (int i = 0; i < daysBack; i++) {
            LocalDateTime targetDate = LocalDateTime.now().minusDays(i);
            String dateStr = targetDate.toLocalDate().toString(); // e.g., "2025-08-07"

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
                    data.setCity(city);
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

    private static class HourlyWeatherResponse {
        public Hourly hourly;

        static class Hourly {
            public String[] time;
            public double[] temperature_2m;
            public double[] wind_speed_10m;
            public double[] cloud_cover;
        }
    }


    // Dummy coordinates — replace with actual lookup if needed
    private double getLat(String city) {
        return switch (city.toLowerCase()) {
            case "london" -> 51.5072;
            case "hanoi" -> 21.0285;
            default -> 0.0;
        };
    }

    private double getLon(String city) {
        return switch (city.toLowerCase()) {
            case "london" -> -0.1276;
            case "hanoi" -> 105.8542;
            default -> 0.0;
        };
    }

    // Inner class to map Open-Meteo response
    private static class OpenMeteoResponse {
        public CurrentWeather current;

        static class CurrentWeather {
            public double temperature_2m;
            public double wind_speed_10m;
            public double cloud_cover;
        }
    }
}
