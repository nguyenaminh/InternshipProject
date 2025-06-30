package com.example.weather_consumer.service;

import com.example.weather_consumer.model.WeatherData;
import com.example.weather_consumer.repository.WeatherDataRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class WeatherMessageConsumer {

    private final WeatherDataRepository repository;

    public WeatherMessageConsumer(WeatherDataRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "weather.queue")
    public void receiveWeatherData(WeatherData message) {
        WeatherData dataToSave = new WeatherData();

        dataToSave.setDate(message.getDate());
        dataToSave.setTemperature(message.getTemperature());
        dataToSave.setHumidity(message.getHumidity());
        dataToSave.setRainfall(message.getRainfall());

        repository.save(dataToSave);
        System.out.println("Saved to DB: " + dataToSave);
    }

}
