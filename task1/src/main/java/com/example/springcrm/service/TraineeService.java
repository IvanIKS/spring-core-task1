package com.example.springcrm.service;

import com.example.springcrm.dao.TraineeDao;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;

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
        try {
            validateName(trainee);

            String username = generateUsername(trainee.getFirstName(), trainee.getLastName());
            String password = generateRandomPassword(RANDOM_PASSWORD_LENGTH);

            trainee.setUsername(username);
            trainee.setPassword(password);

            traineeDao.create(trainee);

            logger.info("Trainee created with username: {}", username);
        } catch (UserAlreadyExistsException e) {
            logger.error(e.getMessage());

            trainee = handleUsernameOverlap(trainee);
             
            traineeDao.create(trainee);
            logger.info("Trainee created with username: {}", trainee.getUsername());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    public void update(Trainee trainee) {
        try {
            validateName(trainee);

            traineeDao.update(trainee);
            logger.info("Trainee {} updated", trainee.getUsername());
        } catch (OutdatedUsernameException e) {
            logger.error(e.getMessage());

            String username = generateUsername(trainee.getFirstName(), trainee.getLastName());
            trainee.setUsername(username);

            updateWithNameCheck(trainee);

            logger.info("Trainee successfully updated with username: {}", username);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
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

    private Trainee handleUsernameOverlap(Trainee trainee) {
        String username = trainee.getUsername();

        List<Trainee> alreadyRegistered = traineeDao.getAllByUsername(username);

        String lastUsername = alreadyRegistered
                .stream()
                .map(Trainee::getUsername)
                .max(String::compareTo)
                .orElse(null);

        username = nextValidUsername(lastUsername);
        trainee.setUsername(username);

        return trainee;
    }

    private void updateWithNameCheck(Trainee trainee) {
        try {
            traineeDao.update(trainee);
        } catch (UserAlreadyExistsException e) {
            logger.error(e.getMessage());
            
            trainee = handleUsernameOverlap(trainee);

            traineeDao.update(trainee);
        }
    }


}
