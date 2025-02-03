package com.example.springcrm.service;

import com.example.springcrm.dao.TraineeDao;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("traineeService")
public class TraineeService extends UserService {
    private static final int RANDOM_PASSWORD_LENGTH = 10;
    private TraineeDao traineeDao;

    private final Logger logger = LoggerFactory.getLogger(TrainerService.class);


    @Autowired
    public TraineeService(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
        logger.info("TraineeService created");
    }

    public void create(Trainee trainee) {
        String username = generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = generateRandomPassword(RANDOM_PASSWORD_LENGTH);

        trainee.setUsername(username);
        trainee.setPassword(password);
        try {
            traineeDao.create(trainee);

            logger.info("Trainee created with username: {}", username);
        } catch (UserAlreadyExistsException e) {
            logger.error(e.getMessage());

            List<Trainee> alreadyRegistered = traineeDao.getAllByUsername(username);

            String lastExistingUsername = alreadyRegistered
                    .stream()
                    .map(Trainee::getUsername)
                    .max(String::compareTo)
                    .orElse(null);

            username = handleUsernameOverlap(username, lastExistingUsername);
            trainee.setUsername(username);
            traineeDao.create(trainee);
            logger.info("Trainee created with username: {}", username);
        }
    }

    public void update(Trainee trainee) {
        try {
            traineeDao.update(trainee);
            logger.info("Trainee {} updated", trainee.getUsername());
        } catch (OutdatedUsernameException e) {
            logger.error(e.getMessage());

            String username = generateUsername(trainee.getFirstName(), trainee.getLastName());
            trainee.setUsername(username);

            traineeDao.update(trainee);
        }
    }

    public void delete(Trainee trainee) {
        traineeDao.delete(trainee);
        logger.info("Trainee {} deleted", trainee.getUsername());
    }

    public Trainee select(String username) {
        return traineeDao.get(username);
    }

    public List<Trainee> list() {
        return traineeDao.getAll();
    }


}
