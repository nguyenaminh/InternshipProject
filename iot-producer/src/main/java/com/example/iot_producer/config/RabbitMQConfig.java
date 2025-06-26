package com.example.iot_producer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "weather.queue";

    public static final String EXCHANGE_NAME = "weather.direct";

    public static final String ROUTING_KEY = "weather.routingKey";

    @Bean
    public Queue weatherQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange weatherExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding weatherBinding(Queue weatherQueue, DirectExchange weatherExchange) {
        return BindingBuilder
                .bind(weatherQueue)
                .to(weatherExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                        Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
