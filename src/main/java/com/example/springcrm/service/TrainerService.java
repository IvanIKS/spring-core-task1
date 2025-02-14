package com.example.springcrm.service;

import com.example.springcrm.dao.TrainerDao;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

            trainer = handleUsernameOverlap(trainer);

            trainerDao.create(trainer);
            logger.info("Trainer created with username: {}", username);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create a user, user had invalid values: " + e.getMessage());
        }
    }

    public void update(Trainer trainer) {
        try {
            trainerDao.update(trainer);
            logger.info("Trainer {} updated", trainer.getUsername());
        } catch (OutdatedUsernameException e) {
            logger.error(e.getMessage());

            String username = generateUsername(trainer.getFirstName(), trainer.getLastName());
            trainer.setUsername(username);

            updateWithNameCheck(trainer);
            logger.info("Trainer successfully updated with username: {}", username);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create a user, user had invalid values: " + e.getMessage());
        }
    }

    public Optional<Trainer> select(String username) {
        return trainerDao.getbyUsername(username);
    }

    public List<Trainer> list() {
        return trainerDao.getAll();
    }

    private Trainer handleUsernameOverlap(Trainer trainer) {
        trainer.setUserId(null);
        String username = trainer.getUsername();

        List<Trainer> alreadyRegistered = trainerDao.getAllByUsername(username);

        String lastUsername = alreadyRegistered
                .stream()
                .map(Trainer::getUsername)
                .max(String::compareTo)
                .orElse(null);

        username = nextValidUsername(lastUsername);
        trainer.setUsername(username);

        return trainer;
    }

    private void updateWithNameCheck(Trainer trainer) {
        try {
            trainerDao.update(trainer);
        } catch (UserAlreadyExistsException e) {
            logger.error(e.getMessage());

            trainer = handleUsernameOverlap(trainer);

            trainerDao.update(trainer);
        }
    }
}
