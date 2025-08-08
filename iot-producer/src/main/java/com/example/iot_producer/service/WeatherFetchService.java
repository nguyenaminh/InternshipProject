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

    public void fetchAndSend(String city) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.open-meteo.com/v1/forecast")
                .queryParam("timezone", "auto")
                .queryParam("current", "temperature_2m,wind_speed_10m,cloud_cover")
                .queryParam("latitude", getLat(city)) // Replace with actual geocoding if needed
                .queryParam("longitude", getLon(city))
                .build().toString();

        var response = restTemplate.getForObject(url, OpenMeteoResponse.class);
        if (response != null && response.current != null) {
            WeatherData data = new WeatherData();
            data.setCity(city);
            data.setDateTime(LocalDateTime.now());
            data.setTemperature(response.current.temperature_2m);
            data.setWindSpeed(response.current.wind_speed_10m);
            data.setCloudCover(response.current.cloud_cover);

            messageSender.sendWeatherData(data);
        } else {
            throw new RuntimeException("Failed to fetch weather from Open-Meteo.");
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
