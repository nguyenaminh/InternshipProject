package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FileReaderService {
    private final MessageSender messageSender;
    private final RestTemplate restTemplate;
    private final String apiKey;

    public FileReaderService(
            MessageSender messageSender,
            @org.springframework.beans.factory.annotation.Value("${openweather.api.key}") String apiKey
    ) {
        this.messageSender = messageSender;
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }

    public void fetchAndSend(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName.trim() + "&appid=" + apiKey + "&units=metric";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("main")) {
            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Double temperature = main.get("temp") != null ? Double.valueOf(main.get("temp").toString()) : null;
            Double humidity = main.get("humidity") != null ? Double.valueOf(main.get("humidity").toString()) : null;
            Double rainfall = 0.0;
            if (response.containsKey("rain")) {
                Map<String, Object> rain = (Map<String, Object>) response.get("rain");
                rainfall = rain.get("1h") != null ? Double.valueOf(rain.get("1h").toString()) : 0.0;
            }

            WeatherData data = new WeatherData();
            data.setStationCode(cityName.trim());
            data.setDateTime(LocalDateTime.now());
            data.setTemperature(temperature);
            data.setHumidity(humidity);
            data.setRainfall(rainfall);

            messageSender.sendWeatherData(data);
            System.out.println("Sent weather data for city: " + cityName);
        } else {
            System.err.println("Failed to fetch data for city: " + cityName);
        }
    }
}
