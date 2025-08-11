package com.example.iot_producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IotProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotProducerApplication.class, args);
	}
}
