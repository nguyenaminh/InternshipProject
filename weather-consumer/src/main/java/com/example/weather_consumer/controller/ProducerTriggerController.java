package com.example.weather_consumer.controller;

import com.example.weather_consumer.service.ProducerTriggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trigger")
public class ProducerTriggerController {

    private final ProducerTriggerService producerTriggerService;

    public ProducerTriggerController(ProducerTriggerService producerTriggerService) {
        this.producerTriggerService = producerTriggerService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchCityWeather(@RequestParam String city) {
        try {
            boolean triggered = producerTriggerService.sendCityToProducer(city);
            if (triggered) {
                return ResponseEntity.ok("Triggered producer to fetch weather for: " + city);
            } else {
                return ResponseEntity.status(500).body("Failed to trigger producer.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
