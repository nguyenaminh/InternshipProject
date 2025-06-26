package com.example.weather_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@EnableRabbit
@SpringBootApplication
public class WeatherConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherConsumerApplication.class, args);
	}

}
