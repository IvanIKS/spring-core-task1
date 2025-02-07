package com.example.springcrm.dao;

import com.example.springcrm.model.Training;
import com.example.springcrm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainingDao implements Dao<Training> {
    private final Storage storage;

    @Autowired
    public TrainingDao(@Qualifier("trainingStorage") Storage storage) {
        this.storage = storage;
    }

    @Override
    public Training get(String id) {
        return (Training) storage.get(id);
    }

    @Override
    public void create(Training training) throws IllegalArgumentException {
        String key = training.getId();
        if (storage.get(key) == null) {
            storage.update(training);
        } else {
            throw new IllegalArgumentException("Training already exists");
        }
    }

    @Override
    public void update(Training newValue) {
        storage.update(newValue);
    }

    @Override
    public List<Training> getAll() {
        return storage.getAll();
    }

    @Override
    public void delete(Training training) {
        storage.delete(training);
    }
}