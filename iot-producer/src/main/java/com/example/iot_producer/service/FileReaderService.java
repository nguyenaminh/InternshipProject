package com.example.iot_producer.service;

import com.example.iot_producer.model.WeatherData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class FileReaderService {
    private final MessageSender messageSender;
    private final String folderPath;
    private final Set<String> processedFiles = new HashSet<>();
    private final String[] suffixes;

    public FileReaderService(MessageSender messageSender, 
                             @Value("${app.file-reader.folder-path:data}") String folderPath,
                             @Value("${app.file-reader.file-suffixes:.csv,.json}") String suffixString) {
        this.messageSender = messageSender;
        this.folderPath = folderPath;
        this.suffixes = suffixString.split(",");
    }

    public void scanAndRead() {
        try {
            Files.createDirectories(Paths.get(folderPath));
            File folder = new File(folderPath);
            File[] dataFiles = folder.listFiles((dir, name) -> Arrays.stream(suffixes).anyMatch(name::endsWith));

            if (dataFiles == null || dataFiles.length == 0) {
                System.out.println("No files present");
                return;
            }

            for (File file : dataFiles) {
                if (!processedFiles.contains(file.getName())) {
                    List<WeatherData> dataList = readFile(file);

                    for (WeatherData data : dataList) {
                        messageSender.sendWeatherData(data);
                    }

                    System.out.println("Sent " + dataList.size() + " message(s) from file: " + file.getName());
                    processedFiles.add(file.getName());
                }
            }
        } catch (IOException e) {
            System.err.println("Error during scanning: " + e.getMessage());
        }
    }

    private List<WeatherData> readFile(File file) {
        List<WeatherData> dataList = new ArrayList<>();
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".csv")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

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
                System.err.println("Failed to read CSV: " + e.getMessage());
            }

        } else if (fileName.endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try (FileReader reader = new FileReader(file)) {
                // Try reading as a list
                dataList = objectMapper.readValue(reader, new TypeReference<List<WeatherData>>() {});
            } catch (MismatchedInputException ex) {
                // Fallback: single object
                try (FileReader reader = new FileReader(file)) {
                    WeatherData singleData = objectMapper.readValue(reader, WeatherData.class);
                    dataList.add(singleData);
                } catch (IOException e) {
                    System.err.println("Failed to read single-object JSON: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Failed to read JSON file: " + e.getMessage());
            }
        }

        return dataList;
    }
}
