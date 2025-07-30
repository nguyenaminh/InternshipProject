package com.example.iot_producer.controller;

import com.example.iot_producer.service.FileReaderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherFetchController {

    private final FileReaderService fileReaderService;

    public WeatherFetchController(FileReaderService fileReaderService) {
        this.fileReaderService = fileReaderService;
    }

    @PostMapping("/fetch")
    public String fetchWeather(@RequestParam String city) {
        fileReaderService.fetchAndSend(city);
        return "Weather data fetched and sent for city: " + city;
    }
}