package com.example.iot_producer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class WeatherData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String city;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTime;

    private Double temperature;
    private Double windSpeed;
    private Double cloudCover;

    public WeatherData() {}

    public WeatherData(String city, LocalDateTime dateTime, Double temperature, Double windSpeed, Double cloudCover) {
        this.city = city;
        this.dateTime = dateTime;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.cloudCover = cloudCover;
    }

    // Getters and setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(Double cloudCover) {
        this.cloudCover = cloudCover;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "city='" + city + '\'' +
                ", dateTime=" + dateTime +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", cloudCover=" + cloudCover +
                '}';
    }
}
