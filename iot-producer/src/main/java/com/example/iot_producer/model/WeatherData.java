package com.example.iot_producer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class WeatherData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stationCode;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTime;

    private float temperature;
    private float humidity;
    private float rainfall;

    public WeatherData() {}

    public WeatherData(String stationCode, LocalDateTime dateTime, float temperature, float humidity, float rainfall) {
        this.stationCode = stationCode;
        this.dateTime = dateTime;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainfall = rainfall;
    }

    // Getters and setters
    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getRainfall() {
        return rainfall;
    }

    public void setRainfall(float rainfall) {
        this.rainfall = rainfall;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "stationCode='" + stationCode + '\'' +
                ", dateTime=" + dateTime +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", rainfall=" + rainfall +
                '}';
    }
}
