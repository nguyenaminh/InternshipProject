package com.example.iot_producer.controller;

import com.example.iot_producer.service.WeatherFetchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produce")
public class WeatherFetchController {

    @Autowired
    private final WeatherFetchService weatherFetchService;

    public WeatherFetchController(WeatherFetchService weatherFetchService) {
        this.weatherFetchService = weatherFetchService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchCityWeather(@RequestParam String city) {
        try {
            weatherFetchService.fetchAndSend(city);
            return ResponseEntity.ok("Weather fetched and sent for city: " + city);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
