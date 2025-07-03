package com.example.iot_producer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

public class WeatherData implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime dateTime;
    private float temperature;
    private float humidity;
    private float rainfall;
    
    public WeatherData() {
    
    }

    public WeatherData(LocalDateTime dateTime, float temperature, float humidity, float rainfall) {
        this.dateTime = dateTime;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainfall = rainfall;
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
        return "Date= " + dateTime + ", temp: " + temperature + ", humidity: " + humidity + ", rainfall: " + rainfall;
    }
}