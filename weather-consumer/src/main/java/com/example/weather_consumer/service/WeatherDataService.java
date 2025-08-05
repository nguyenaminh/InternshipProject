package com.example.weather_consumer.service;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.repository.WeatherDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
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

    // NEW: Hourly stats
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

    // NEW: Daily stats (for a given month)
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

    // NEW: Monthly stats (for a given year)
    public Map<String, Map<String, Double>> getMonthlyStats(String city, String year) {
        Year y = Year.parse(year); // expects "yyyy"
        LocalDateTime start = y.atMonth(1).atDay(1).atStartOfDay();
        LocalDateTime end = y.atMonth(12).atEndOfMonth().atTime(23, 59, 59);
        List<WeatherData> data = repository.findByCityAndDateTimeBetween(city, start, end);

        return data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDateTime().getYear() + "-" + String.format("%02d", d.getDateTime().getMonthValue()),
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            double avgTemp = list.stream()
                                    .filter(d -> d.getTemperature() != null)
                                    .mapToDouble(WeatherData::getTemperature)
                                    .average().orElse(0);

                            double avgWind = list.stream()
                                    .filter(d -> d.getWindSpeed() != null)
                                    .mapToDouble(WeatherData::getWindSpeed)
                                    .average().orElse(0);

                            double avgCloud = list.stream()
                                    .filter(d -> d.getCloudCover() != null)
                                    .mapToDouble(WeatherData::getCloudCover)
                                    .average().orElse(0);

                            return Map.of(
                                    "temperature", avgTemp,
                                    "windSpeed", avgWind,
                                    "cloudCover", avgCloud
                            );
                        })
                ));
    }

    // Helper: parse either full datetime or just a date
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

    public Map<String, Double> getLast12MonthsStats(String city) {
        LocalDate now = LocalDate.now().withDayOfMonth(1); // current month start
        LocalDate start = now.minusMonths(11); // 12 months ago
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = now.plusMonths(1).atStartOfDay().minusSeconds(1);

        List<WeatherData> data = repository.findByCityAndDateTimeBetween(city, startDateTime, endDateTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDateTime().format(formatter),
                        TreeMap::new, // keeps months sorted
                        Collectors.averagingDouble(d -> d.getTemperature() != null ? d.getTemperature() : 0)
                ));
    }

    public List<WeatherData> getLatest24Hours(String city) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);

        return repository.findByCityAndDateTimeBetweenOrderByDateTimeAsc(city, from, now);
    }

    public boolean existsByCityAndDateTime(String city, LocalDateTime dateTime) {
        return repository.existsByCityAndDateTime(city, dateTime);
    }

}
