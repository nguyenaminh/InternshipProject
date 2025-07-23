package com.example.weather_consumer.controller;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.service.WeatherDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
public class WeatherDataController {
    private static final List<String> VALID_SORT_FIELDS = List.of("id", "stationCode", "temperature", "humidity", "pressure", "dateTime");

    private final WeatherDataService service;

    public WeatherDataController(WeatherDataService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WeatherData>> getAllWeatherData(
    @RequestParam(required = false) String start,
    @RequestParam(required = false) String end) {
        List<WeatherData> dataList = service.getAllDataWithinRange(start,end);

        if (dataList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("/station/{stationCode}")
    public ResponseEntity<List<WeatherData>> getByStation(@PathVariable String stationCode) {
        List<WeatherData> results = service.getByStationCode(stationCode);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<WeatherData>> getPagedWeatherData(
            @RequestParam(required = false) String station,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            return ResponseEntity
                .badRequest()
                .body(Page.empty());
        }
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WeatherData> resultPage = service.getFilteredPaged(station, start, end, pageable);

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
}
