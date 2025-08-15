package com.example.iot_producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "weather")
public class WeatherConfig {

    private String cities;

    public List<String> getCities() {
        if (cities == null || cities.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(cities.split(","))
                     .map(String::trim)
                     .toList();
    }

    public void setCities(String cities) {
        this.cities = cities;
    }
}
