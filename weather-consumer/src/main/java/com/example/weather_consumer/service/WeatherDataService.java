package com.example.weather_consumer.service;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.repository.WeatherDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;


@Service
public class WeatherDataService {
    private final WeatherDataRepository repository;

    public WeatherDataService(WeatherDataRepository repository) {
        this.repository = repository;
    }

    public List<WeatherData> getAllData() {
        return repository.findAll();
    }

    public List<WeatherData> getAllDataWithinRange(String start, String end) {
        try {
            if(start == null || end == null) {
                return getAllData();
            }

            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);

            return repository.findByDateTimeBetween(startTime, endTime);
        } catch (DateTimeParseException e) {
            return List.of();
        }
    }

    public Optional<WeatherData> getById(Long id) {
        return repository.findById(id);
    }

    public List<WeatherData> getByStationCode(String stationCode) {
        return repository.findByStationCode(stationCode);
    }

    public boolean deleteById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public WeatherData saveData(WeatherData data) {
        return repository.save(data);
    }

    public Page<WeatherData> getAllDataPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    private LocalDateTime parseOrDefault(String input, LocalDateTime defaultValue) {
        if (input == null || input.isBlank()) return defaultValue;
        try {
            return LocalDateTime.parse(input);
        } catch (DateTimeParseException e) {
            return defaultValue;
        }
    }

    public Page<WeatherData> getFilteredPaged(String stationCode, String start, String end, Pageable pageable) {
        LocalDateTime startTime = parseOrDefault(start, LocalDateTime.of(1970, 1, 1, 0, 0));
        LocalDateTime endTime = parseOrDefault(end, LocalDateTime.now());

        if (stationCode != null && !stationCode.isBlank()) {
            return repository.findByStationCodeContainingIgnoreCaseAndDateTimeBetween(
                    stationCode, startTime, endTime, pageable);
        }

        return repository.findByDateTimeBetween(startTime, endTime, pageable);
    }
}