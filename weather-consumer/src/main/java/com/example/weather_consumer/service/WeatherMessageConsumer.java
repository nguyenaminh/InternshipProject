package com.example.weather_consumer.service;

import com.example.weather_consumer.model.WeatherData;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

@Service
public class WeatherMessageConsumer {
    
    public WeatherMessageConsumer() {
        System.out.println("WeatherMessageConsumer initialized");
    }

    @RabbitListener(queues = "weather.queue")
    public void receiveMessage(WeatherData data) {
        System.out.println("Received message from RabbitMQ: ");
        // System.out.println(data);
        
        // Process the received weather data
        System.out.println("Date: " + data.getDate());
        System.out.println("Temperature: " + data.getTemperature());
        System.out.println("Humidity: " + data.getHumidity());
        System.out.println("Rainfall: " + data.getRainfall());

    }
}
