package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.iot_producer.service.MessageSender;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class FileReaderService {
    private final MessageSender messageSender;
    private final String folderPath;
    private final Set<String> processedFiles = new HashSet<>();
    private final String[] suffixes;

    public FileReaderService(MessageSender messageSender,@Value("${app.file-reader.folder-path:data}") String folderPath, @Value("${app.file-reader.file-suffixes:.csv,.json}") String suffixString) {
        this.messageSender = messageSender;
        this.folderPath = folderPath;
        this.suffixes = suffixString.split(",");
    }
    public void scanAndRead() {
        try {
            Files.createDirectories(Paths.get(folderPath));
            File folder = new File(folderPath);
            File[] dataFiles = folder.listFiles((dir,name) -> Arrays.stream(suffixes).anyMatch(name::endsWith));

            if (dataFiles == null || dataFiles.length == 0) {
                System.out.println("No files present");
                return;
            }

            for (File file : dataFiles) {
                if (!processedFiles.contains(file.getName())) {
                    System.out.println("Currently reading: " + file.getName());
                    List<WeatherData> dataList = readFile(file);

                    for (WeatherData data : dataList) {
                        System.out.println("Date= " + data.getDate()
                                + ", temp: " + data.getTemperature()
                                + ", humidity: " + data.getHumidity()
                                + ", rainfall: " + data.getRainfall());

                        messageSender.sendWeatherData(data);
                    }

                    processedFiles.add(file.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<WeatherData> readFile(File file) {
        List<WeatherData> dataList = new ArrayList<>();
        String fileName = file.getName().toLowerCase();

        if(fileName.endsWith(".csv")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) { firstLine = false; continue; }

                    String[] parts = line.split(",");
                    if (parts.length != 4) continue;

                    String date = parts[0];
                    float temp = Float.parseFloat(parts[1]);
                    float humidity = Float.parseFloat(parts[2]);
                    float rainfall = Float.parseFloat(parts[3]);

                    WeatherData data = new WeatherData(date, temp, humidity, rainfall);
                    dataList.add(data);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(fileName.endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try (FileReader reader = new FileReader(file)) {
                dataList = objectMapper.readValue(reader, new TypeReference<List<WeatherData>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }
}