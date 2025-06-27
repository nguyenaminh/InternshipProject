package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.example.iot_producer.config.RabbitMQConfig;

@Service
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendWeatherData(WeatherData data) {
        System.out.println("Sending data to Exchange: " + RabbitMQConfig.EXCHANGE_NAME + " with routing key: " + RabbitMQConfig.ROUTING_KEY + " payload: " + data);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, data);
    
        System.out.println("Data sent successfully.");
    }
}
