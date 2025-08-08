package com.example.iot_producer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/produce/**")
                .allowedOrigins("http://localhost:5173") // frontend port
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
}