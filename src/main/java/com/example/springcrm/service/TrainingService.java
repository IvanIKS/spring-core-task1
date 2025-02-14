package com.example.springcrm.service;

import com.example.springcrm.dao.TrainingDao;
import com.example.springcrm.model.Training;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("trainingService")
public class TrainingService  {
    private TrainingDao trainingDao;
    private final Logger logger = LoggerFactory.getLogger(TrainingService.class);


    @Autowired
    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
        logger.info("TrainingService created");
    }

    public void create(Training training) {
        try {
            trainingDao.create(training);
            logger.info("Training created");
        } catch (EntityExistsException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    public Optional<Training> select(String id) {
        return trainingDao.get(id);
    }

    public List<Training> list() {
        return trainingDao.getAll();
    }
}
