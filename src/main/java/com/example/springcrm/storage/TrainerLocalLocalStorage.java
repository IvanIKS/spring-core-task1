package com.example.springcrm.storage;

import com.example.springcrm.model.Trainer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Repository("trainerStorage")
public class TrainerLocalLocalStorage implements LocalStorage<Trainer> {
    private static final String FILE_PATH = "trainers.json";
    @Value("storage.loading.from.file")
    private static boolean LOADING_FROM_FILE;

    private final Map<String, Trainer> trainers = new HashMap<String, Trainer>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(TrainerLocalLocalStorage.class);

    public TrainerLocalLocalStorage() {
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
                    List<Trainer> trainers = objectMapper.readValue(inputStream, new TypeReference<>() {
                    });
                    for (Trainer trainee : trainers) {
                        update(trainee);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load trainers from file: " + FILE_PATH, e);
            }
        } else {
            logger.info("Initializing trainerStorage from file disabled");
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
