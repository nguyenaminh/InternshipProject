package com.example.weather_consumer.config;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.service.WeatherDataService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.atomic.AtomicInteger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    private final WeatherDataService weatherDataService;

    public RabbitMQConfig(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
    }

    public static final String MAIN_EXCHANGE = "weather.direct";
    public static final String MAIN_QUEUE = "weather.queue";
    public static final String MAIN_ROUTING_KEY = "weather.routingKey";

    public static final String DLX_EXCHANGE = "weather.dlx";
    public static final String DLQ_QUEUE = "weather.dlq";
    public static final String DLQ_ROUTING_KEY = "weather.dlq.routingKey";

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue mainQueue() {
        return QueueBuilder.durable(MAIN_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue())
                .to(mainExchange())
                .with(MAIN_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        // Map producer class name to consumer class
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.example.iot_producer.model.WeatherData", WeatherData.class);
        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter,
            Queue mainQueue
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(mainQueue.getName());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        AtomicInteger messageCount = new AtomicInteger(0); // New counter

        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            try {
                WeatherData data = (WeatherData) jsonMessageConverter.fromMessage(message);

                if (!isValid(data)) {
                    throw new IllegalArgumentException("Invalid weather data: " + data);
                }

                weatherDataService.saveData(data);

                int count = messageCount.incrementAndGet(); // Increment the count
                logger.info("Total weather data messages received: {}", count); // Only log count

                // Channel is guaranteed non-null in MANUAL mode with ChannelAwareMessageListener
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            } catch (Exception e) {
                logger.error("Failed to process message: {}, routing to DLQ", e.getMessage());
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
        });

        return container;
    }


    private boolean isValid(WeatherData data) {
        if (data == null) return false;
        if (data.getCity() == null || data.getCity().isBlank()) return false;
        if (data.getDateTime() == null || data.getDateTime().isAfter(LocalDateTime.now().plusHours(2))) {
            return false;
        }
        return true;
    }
}
