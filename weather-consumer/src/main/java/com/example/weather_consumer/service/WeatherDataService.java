package com.example.weather_consumer.service;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.repository.WeatherDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;

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
        LocalDateTime startTime = parseToDateTime(start, true);
        LocalDateTime endTime = parseToDateTime(end, false);

        if (startTime != null && endTime != null) {
            return repository.findByDateTimeBetween(startTime, endTime);
        } else if (startTime != null) {
            return repository.findByDateTimeAfter(startTime);
        } else if (endTime != null) {
            return repository.findByDateTimeBefore(endTime);
        }
        return getAllData();
    }

    public Optional<WeatherData> getById(Long id) {
        return repository.findById(id);
    }

    public List<WeatherData> getByCity(String city) {
        return repository.findByCity(city);
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

    public Page<WeatherData> getFilteredPaged(String city, String start, String end, Pageable pageable) {
        LocalDateTime startTime = parseToDateTime(start, true);
        LocalDateTime endTime = parseToDateTime(end, false);

        if (city != null && !city.isBlank()) {
            return repository.findByCityContainingIgnoreCaseAndDateTimeBetween(
                    city, startTime, endTime, pageable);
        }
        return repository.findByDateTimeBetween(startTime, endTime, pageable);
    }

    public List<WeatherData> getLatest3Hours() {
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime threeHoursAgo = now.minusHours(3);

        return repository.findByDateTimeBetween(threeHoursAgo, now);
    }

    // ✅ NEW: Hourly stats
    public Map<Integer, Double> getHourlyStats(String city, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<WeatherData> data = repository.findByCityAndDateTimeBetween(city, start, end);

        return data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDateTime().getHour(),
                        Collectors.averagingDouble(d -> d.getTemperature() != null ? d.getTemperature() : 0)
                ));
    }

    // ✅ NEW: Daily stats (for a given month)
    public Map<Integer, Double> getDailyStats(String city, String month) {
        YearMonth ym = YearMonth.parse(month); // expects "yyyy-MM"
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
        List<WeatherData> data = repository.findByCityAndDateTimeBetween(city, start, end);

        return data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDateTime().getDayOfMonth(),
                        Collectors.averagingDouble(d -> d.getTemperature() != null ? d.getTemperature() : 0)
                ));
    }

    // ✅ NEW: Monthly stats (for a given year)
    public Map<Integer, Double> getMonthlyStats(String city, String year) {
        Year y = Year.parse(year); // expects "yyyy"
        LocalDateTime start = y.atMonth(1).atDay(1).atStartOfDay();
        LocalDateTime end = y.atMonth(12).atEndOfMonth().atTime(23, 59, 59);
        List<WeatherData> data = repository.findByCityAndDateTimeBetween(city, start, end);

        return data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDateTime().getMonthValue(),
                        Collectors.averagingDouble(d -> d.getTemperature() != null ? d.getTemperature() : 0)
                ));
    }

    // 🔑 Helper: parse either full datetime or just a date
    private LocalDateTime parseToDateTime(String input, boolean isStart) {
        if (input == null || input.isBlank()) return isStart 
            ? LocalDateTime.of(1970, 1, 1, 0, 0) 
            : LocalDateTime.now();

        try {
            // Try ISO datetime first
            return LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                // Then fallback to just a date
                LocalDate date = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
                return isStart ? date.atStartOfDay() : date.atTime(23, 59, 59);
            } catch (DateTimeParseException ex) {
                return isStart 
                    ? LocalDateTime.of(1970, 1, 1, 0, 0) 
                    : LocalDateTime.now();
            }
        }
    }
}
