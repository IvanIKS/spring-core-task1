package com.example.springcrm.storage;

import com.example.springcrm.model.Trainee;
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

@Repository("traineeStorage")
public class TraineeStorage implements Storage<Trainee> {
    private static final String FILE_PATH = "trainees.json";

    private final Map<String, Trainee> trainees;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(TraineeStorage.class);

    @Autowired
    public TraineeStorage(Map<String, Trainee> trainees) {
        this.trainees = trainees;
        logger.info("Created traineeStorage instance");
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
                    List<Trainee> trainees = objectMapper.readValue(inputStream, new TypeReference<List<Trainee>>() {
                    });
                    for (Trainee trainee : trainees) {
                        update(trainee);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load trainees from file: " + FILE_PATH, e);
            }
        }

    }

    @Override
    public Trainee get(String key) throws IllegalArgumentException {
        return trainees.get(key);
    }

    @Override
    public Trainee get(Trainee value) {
        return trainees.get(getKey(value));
    }

    @Override
    public void update(Trainee trainee) throws IllegalArgumentException {
        trainees.put(getKey(trainee), trainee.clone());

    }

    @Override
    public void delete(Trainee trainee) throws IllegalArgumentException {
        trainees.remove(getKey(trainee));
    }

    @Override
    public List<Trainee> getAll() {
        return trainees
                .values()
                .stream()
                .toList();
    }

    @Override
    public String getNextId() {
        return "" + (trainees.size() + 1);
    }

    @Override
    public void cleanAll() {
        trainees.clear();
    }

    public List<Trainee> getAllByUsername(String usernameSubstring) {
        return trainees
                .values()
                .stream()
                .filter(trainee ->
                        trainee.getUsername()
                                .contains(usernameSubstring))
                .toList();
    }

    private static String getKey(Trainee trainee) {
        return trainee.getUsername();
    }
}
