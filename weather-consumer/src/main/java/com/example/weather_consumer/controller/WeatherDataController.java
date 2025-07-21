package com.example.weather_consumer.controller;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.repository.WeatherDataRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/weather")
public class WeatherDataController {
    private final WeatherDataRepository repository;

    public WeatherDataController(WeatherDataRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<WeatherData> getAllWeatherData() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<WeatherData> getById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("station/{stationCode}")
    public List<WeatherData> getByStation(@PathVariable String stationCode) {
        return repository.findByStationCode(stationCode);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @DeleteMapping("/clear")
    public void deleteAll() {
        repository.deleteAll();
    }
}
