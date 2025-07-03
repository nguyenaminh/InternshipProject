package com.example.weather_consumer.config;

import com.example.weather_consumer.model.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // Main queue/exchange/routing key
    public static final String MAIN_EXCHANGE = "weather.direct";
    public static final String MAIN_QUEUE = "weather.queue";
    public static final String MAIN_ROUTING_KEY = "weather.routingKey";

    // DLQ setup
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

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.example.iot_producer.model.WeatherData", WeatherData.class);
        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter, Queue mainQueue) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(mainQueue.getName());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // Manual ack

        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            try {
                // Deserialize message
                WeatherData data = (WeatherData) jsonMessageConverter.fromMessage(message);

                // ✅ Validate format only (DO NOT SAVE)
                if (data.getDateTime() == null ||
                    data.getTemperature() < -100 || data.getTemperature() > 100 ||
                    data.getHumidity() < 0 || data.getHumidity() > 100 ||
                    data.getRainfall() < 0) {
                    throw new IllegalArgumentException("Invalid weather data format");
                }

                // ✅ Forward to Spring-managed @RabbitListener (it will handle saving)
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            } catch (Exception e) {
                // ❌ Invalid message — route to DLQ
                System.err.println("Failed to process message: Wrong format");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
        });

        return container;
    }
}
