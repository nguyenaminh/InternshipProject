package com.example.weather_consumer.controller;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.service.WeatherDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

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

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyStats(
            @RequestParam String city,
            @RequestParam String year) {
        return ResponseEntity.ok(service.getMonthlyStats(city, year));
    }

    @GetMapping("/monthly/last12")
    public ResponseEntity<?> getLast12MonthsStats(@RequestParam String city) {
        return ResponseEntity.ok(service.getLast12MonthsStats(city));
    }

    @GetMapping("/latest24h")
    public ResponseEntity<List<WeatherData>> getLatest24Hours(@RequestParam String city) {
        return ResponseEntity.ok(service.getLatest24Hours(city));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            // Try a simple DB query to ensure DB is up
            service.count(); // You need to implement this method in WeatherDataService
            return ResponseEntity.ok("Consumer is ready");
        } catch (DataAccessException e) {
            return ResponseEntity.status(503).body("Consumer DB not ready: " + e.getMessage());
        }
    }

    @GetMapping("/weekly/last7")
    public ResponseEntity<?> getLast7DaysStats(@RequestParam String city) {
        return ResponseEntity.ok(service.getLast7DaysStats(city));
    }
    
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkExists(
            @RequestParam String city,
            @RequestParam String dateTime
    ) {
        LocalDateTime dt = LocalDateTime.parse(dateTime);
        boolean exists = service.existsByCityAndDateTime(city, dt);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/exists-range")
    public ResponseEntity<Boolean> checkExistsInRange(
            @RequestParam String city,
            @RequestParam int daysBack
    ) {
        city = city.toLowerCase(); // Normalize city
        boolean exists = service.hasEnoughRecentData(city, daysBack);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists-year")
    public ResponseEntity<Boolean> checkYearlyExists(@RequestParam String city) {
        city = city.toLowerCase(); // Normalize for consistency
        boolean exists = service.existsYearlyData(city);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/latest-hour")
    public ResponseEntity<?> getLatestHourStats(@RequestParam String city) {
        Optional<WeatherData> latest = service.getLatestHourData(city);
        if (latest.isPresent()) {
            WeatherData data = latest.get();
            Map<String, Object> response = Map.of(
                    "temperature", data.getTemperature(),
                    "windSpeed", data.getWindSpeed(),
                    "cloudCover", data.getCloudCover()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
