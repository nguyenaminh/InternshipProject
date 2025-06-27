package com.example.iot_producer;

import com.example.iot_producer.service.FileReaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IotProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotProducerApplication.class, args);
	}

	@Bean
	CommandLineRunner run(FileReaderService fileReaderService) {
		return args -> {
			while(true) {
				fileReaderService.scanAndRead();
				Thread.sleep(10000);
			}
		};
	}

}
