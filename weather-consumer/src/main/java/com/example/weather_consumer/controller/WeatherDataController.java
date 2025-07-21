package com.example.weather_consumer.controller;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.service.WeatherDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/weather")
public class WeatherDataController {
    private final WeatherDataService service;

    public WeatherDataController(WeatherDataService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WeatherData>> getAllWeatherData() {
        List<WeatherData> dataList = service.getAllData();
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeatherData> getById(@PathVariable Long id) {
        Optional<WeatherData> result = service.getById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/station/{stationCode}")
    public ResponseEntity<List<WeatherData>> getByStation(@PathVariable String stationCode) {
        List<WeatherData> results = service.getByStationCode(stationCode);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok("Deleted weather data with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> deleteAll() {
        service.deleteAll();
        return ResponseEntity.ok("All weather data deleted.");
    }
}
