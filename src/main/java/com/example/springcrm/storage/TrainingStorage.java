package com.example.springcrm.storage;

import com.example.springcrm.model.Training;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("trainingStorage")
public class TrainingStorage implements Storage<Training> {
    @Value("${storage.training.file}")
    private Resource trainingFile;

    private final Map<String, Training> trainings = new HashMap<String, Training>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(TrainingStorage.class);

    public TrainingStorage() {
        logger.info("Created trainingStorage instance");
    }

    @Override
    public void init() {
        if (false) {
            try (InputStream inputStream = trainingFile.getInputStream()) {
                logger.info("Loading trainings from file");
                List<Training> trainings = objectMapper.readValue(inputStream, new TypeReference<>() {
                });
                for (Training training : trainings) {
                    update(training);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load trainees from file", e);
            }
        }
    }

    @Override
    public Training get(String key) throws IllegalArgumentException {
        return trainings.get(key);
    }

    @Override
    public Training get(Training value) {
        return trainings.get(getKey(value));
    }

    @Override
    public void update(Training training) throws IllegalArgumentException {
        trainings.put(getKey(training), training.clone());
    }

    @Override
    public void delete(Training training) throws IllegalArgumentException {
        trainings.remove(getKey(training));
    }

    @Override
    public List<Training> getAll() {
        return trainings.values().stream().toList();
    }

    @Override
    public String getNextId() {
        return "" + (trainings.size() + 1);
    }

    @Override
    public void cleanAll() {
        trainings.clear();
    }

    private static String getKey(Training training) {
        return training.getId();
    }
}