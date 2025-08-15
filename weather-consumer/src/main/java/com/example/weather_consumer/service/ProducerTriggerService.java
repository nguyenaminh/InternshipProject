package com.example.weather_consumer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class ProducerTriggerService {

    @Value("${producer.api.url}")
    private String producerBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendCityToProducer(String city) {
        String triggerUrl = producerBaseUrl + "/api/produce/fetch?city=" + city;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(triggerUrl, null, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error calling producer: " + e.getMessage());
            return false;
        }
    }
}
