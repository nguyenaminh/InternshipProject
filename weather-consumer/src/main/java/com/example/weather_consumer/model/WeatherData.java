package com.example.weather_consumer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;
    private String city;
    private Double temperature;
    private Double windSpeed;
    private Double cloudCover;

    public WeatherData() {}

    public WeatherData( LocalDateTime dateTime, String city, Double temperature, Double windSpeed, Double cloudCover) {
        this.dateTime = dateTime;
        this.city = city;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.cloudCover = cloudCover;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public Double getCloudCover() { return cloudCover; }
    public void setCloudCover(Double cloudCover) { this.cloudCover = cloudCover; }

    @Override
    public String toString() {
        return "WeatherData{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", dateTime=" + dateTime +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", cloudCover=" + cloudCover +
                '}';
    }
}
