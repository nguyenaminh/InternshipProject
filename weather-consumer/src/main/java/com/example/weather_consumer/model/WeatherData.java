package com.example.weather_consumer.model;

public class WeatherData {
    private String date;
    private float temperature;
    private float humidity;
    private float rainfall;
    
    public WeatherData() {
    
    }

    public WeatherData(String date, float temperature, float humidity, float rainfall) {
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainfall = rainfall;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
        return "Date= " + date + ", temp: " + temperature + ", humidity: " + humidity + ", rainfall: " + rainfall;
    }
}
