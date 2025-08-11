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
    public ResponseEntity<String> fetchWeather(@RequestParam String city) {
        weatherFetchService.fetchAndSendRange(city, 7); // Fetch past 7 days
        weatherFetchService.fetchLastYearData(city);  
        return ResponseEntity.ok("Triggered weather fetch for " + city);

    }
}
