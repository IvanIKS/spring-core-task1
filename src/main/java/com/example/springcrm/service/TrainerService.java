package com.example.springcrm.service;

import com.example.springcrm.dao.TrainerDao;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("trainerService")
public class TrainerService extends UserService {
    private static final int RANDOM_PASSWORD_LENGTH = 10;
    private TrainerDao trainerDao;

    private final Logger logger = LoggerFactory.getLogger(TrainerService.class);


    @Autowired
    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
        logger.info("TrainerService created");
    }

    public void create(Trainer trainer) {
        String username = generateUsername(trainer.getFirstName(), trainer.getLastName());
        String password = generateRandomPassword(RANDOM_PASSWORD_LENGTH);

        trainer.setUsername(username);
        trainer.setPassword(password);
        try {
            trainerDao.create(trainer);

            logger.info("Trainer created with username: {}", username);
        } catch (UserAlreadyExistsException e) {
            logger.error(e.getMessage());

            List<Trainer> alreadyRegistered = trainerDao.getAllByUsername(username);

            String lastExistingUsername = alreadyRegistered
                    .stream()
                    .map(Trainer::getUsername)
                    .max(String::compareTo)
                    .orElse(null);

            username = handleUsernameOverlap(username, lastExistingUsername);
            trainer.setUsername(username);
            trainerDao.create(trainer);
            logger.info("Trainer created with username: {}", username);
        }
    }

    public void update(Trainer trainer) {
        try {
            trainerDao.update(trainer);
            logger.info("Trainer updated");
        } catch (OutdatedUsernameException e) {
            logger.error(e.getMessage());

            String username = generateUsername(trainer.getFirstName(), trainer.getLastName());
            trainer.setUsername(username);

            trainerDao.update(trainer);
        }
    }

    public Trainer select(String username) {
        return trainerDao.get(username);
    }

    public List<Trainer> list() {
        return trainerDao.getAll();
    }
}
