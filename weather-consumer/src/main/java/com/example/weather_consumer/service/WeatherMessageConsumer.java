package com.example.weather_consumer.service;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.repository.WeatherDataRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WeatherMessageConsumer {

    private final WeatherDataRepository weatherDataRepository;

    public WeatherMessageConsumer(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
        System.out.println("WeatherMessageConsumer initialized");
    }

    @RabbitListener(queues = "weather.queue")
    public void receiveMessage(WeatherData data) {
        System.out.println("Received message from RabbitMQ: " + data);
        weatherDataRepository.save(data); // Save to MySQL
        System.out.println("✅ Saved to database: " + data);
    }
}
