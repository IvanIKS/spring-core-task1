package com.example.springcrm.storage;

import com.example.springcrm.model.Trainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Repository("trainerStorage")
public class TrainerStorage implements Storage<Trainer> {
    private static final String FILE_PATH = "trainers.json";

    private final Map<String, Trainer> trainers;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(TrainerStorage.class);

    @Autowired
    public TrainerStorage(Map<String, Trainer> trainers) {
        this.trainers = trainers;
        logger.info("Created trainerStorage instance");
    }

    @Override
    @PostConstruct
    public void init() {
        if (LOADING_FROM_FILE) {
            try {
                logger.info("Loading trainees from resources: " + FILE_PATH);

                Resource resource = new ClassPathResource(FILE_PATH);
                if (!resource.exists()) {
                    throw new RuntimeException("File not found in resources: " + FILE_PATH);
                }

                try (InputStream inputStream = resource.getInputStream()) {
                    List<Trainer> trainers = objectMapper.readValue(inputStream, new TypeReference<List<Trainer>>() { });
                    for (Trainer trainee : trainers) {
                        update(trainee);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load trainers from file: " + FILE_PATH, e);
            }
        }
    }

    @Override
    public Trainer get(String key) throws IllegalArgumentException {
        return (Trainer) trainers.get(key);
    }

    @Override
    public Trainer get(Trainer value) {
        return (Trainer) trainers.get(getKey(value));
    }

    @Override
    public void update(Trainer trainer) throws IllegalArgumentException {
        trainers.put(getKey(trainer), trainer.clone());
    }

    @Override
    public void delete(Trainer trainer) throws IllegalArgumentException {
        trainers.remove(getKey(trainer));
    }

    @Override
    public List<Trainer> getAll() {
        return trainers.values().stream().toList();
    }

    @Override
    public String getNextId() {
        return "" + (trainers.size() + 1);
    }

    @Override
    public void cleanAll() {
        trainers.clear();
    }


    public List<Trainer> getAllByUsername(String usernameSubstring) {
        return trainers.values().stream()
                .filter(trainer -> trainer
                        .getUsername()
                        .contains(usernameSubstring)
                )
                .toList();
    }

    private static String getKey(Trainer trainer) {
        return trainer.getUsername();
    }
}
