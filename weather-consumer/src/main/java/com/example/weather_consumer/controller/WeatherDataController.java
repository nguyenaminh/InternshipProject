package com.example.weather_consumer.controller;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.service.WeatherDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
public class WeatherDataController {
    private static final List<String> VALID_SORT_FIELDS =
            List.of("id", "city", "temperature", "windSpeed", "cloudCover", "dateTime");

    private final WeatherDataService service;

    public WeatherDataController(WeatherDataService service) {
        this.service = service;
    }

    // Existing endpoints
    @GetMapping
    public ResponseEntity<List<WeatherData>> getAllWeatherData(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        List<WeatherData> dataList = service.getAllDataWithinRange(start, end);

        if (dataList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<WeatherData>> getByCity(@PathVariable String city) {
        List<WeatherData> results = service.getByCity(city);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<WeatherData>> getPagedWeatherData(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            return ResponseEntity.badRequest().body(Page.empty());
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WeatherData> resultPage = service.getFilteredPaged(city, start, end, pageable);

        if (resultPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<WeatherData> getById(@PathVariable Long id) {
        Optional<WeatherData> result = service.getById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id:[0-9]+}")
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

    // --- NEW Endpoints for Hourly/Daily/Monthly ---

    @GetMapping("/hourly")
    public ResponseEntity<?> getHourlyStats(
            @RequestParam String city,
            @RequestParam String date) {
        return ResponseEntity.ok(service.getHourlyStats(city, LocalDate.parse(date)));
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyStats(
            @RequestParam String city,
            @RequestParam String month) {
        return ResponseEntity.ok(service.getDailyStats(city, month));
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyStats(
            @RequestParam String city,
            @RequestParam String year) {
        return ResponseEntity.ok(service.getMonthlyStats(city, year));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<WeatherData>> getLatest3Hours() {
        return ResponseEntity.ok(service.getLatest3Hours());
    }
}
