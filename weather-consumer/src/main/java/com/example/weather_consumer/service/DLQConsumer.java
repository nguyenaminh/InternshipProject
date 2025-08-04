package com.example.weather_consumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class DLQConsumer {

    private final Path logFolder;

    public DLQConsumer(@Value("${app.dlq.log-folder:dlq_logs}") String folderPath) {
        this.logFolder = Paths.get(folderPath);
        try {
            Files.createDirectories(logFolder);
        } catch (IOException e) {
            System.err.println("Failed to create DLQ log folder: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "weather.dlq")
    public void handleDeadLetter(Message message) {
        try {
            String body = new String(message.getBody());
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String filename = "dlq_" + timestamp + "_" + UUID.randomUUID() + ".json";

            Path logFile = logFolder.resolve(filename);
            Files.writeString(logFile, body, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            System.out.println("Logged DLQ message to " + logFile);
        } catch (IOException e) {
            System.err.println("Failed to log DLQ message: " + e.getMessage());
        }
    }
}
