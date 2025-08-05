package com.example.weather_consumer.repository;

import com.example.weather_consumer.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    List<WeatherData> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<WeatherData> findByDateTimeAfter(LocalDateTime start);

    List<WeatherData> findByDateTimeBefore(LocalDateTime end);

    List<WeatherData> findByCity(String city);

    Page<WeatherData> findByDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<WeatherData> findByCityContainingIgnoreCaseAndDateTimeBetween(
            String city, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<WeatherData> findByCityAndDateTimeBetween(String city, LocalDateTime start, LocalDateTime end);

    boolean existsByCityAndDateTime(String city, LocalDateTime dateTime);

    List<WeatherData> findByCityAndDateTimeBetweenOrderByDateTimeAsc(
        String city,
        LocalDateTime start,
        LocalDateTime end
    );
}
