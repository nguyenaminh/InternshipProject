package com.example.weather_consumer.repository;

import com.example.weather_consumer.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findByStationCode(String stationCode);

    List<WeatherData> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}