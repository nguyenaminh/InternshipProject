package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public MessageSender(RabbitTemplate rabbitTemplate, @Value("${app.rabbitmq.queue:weather.queue}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void sendWeatherData(WeatherData data) {
        rabbitTemplate.convertAndSend(queueName, data);
    }
}
