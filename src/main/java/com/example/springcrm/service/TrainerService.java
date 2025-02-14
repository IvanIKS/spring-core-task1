package com.example.springcrm.service;

import com.example.springcrm.dao.TrainerDao;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UnauthorisedException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.Training;
import com.example.springcrm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("trainerService")
public class TrainerService extends UserService {
    private static final int RANDOM_PASSWORD_LENGTH = 10;
    private TrainerDao trainerDao;
    private AuthenticationService authenticationService;
    private final Logger logger = LoggerFactory.getLogger(TrainerService.class);


    @Autowired
    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
        this.authenticationService = new TrainerAuthenticationService(trainerDao);
        logger.info("TrainerService created");
    }

    private static class TrainerAuthenticationService extends AuthenticationService {
        protected TrainerAuthenticationService(TrainerDao dao) {
            super(dao);
        }
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
            authenticationService.authenticate(trainer);
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
        return trainerDao.getByUsername(username);
    }

    public List<Trainer> list() {
        return trainerDao.getAll();
    }

    @Override
    public Optional<Trainer> activateAccount(String username) {
        Optional<Trainer> maybeTrainer = select(username);
        if (maybeTrainer.isPresent()) {
            Trainer trainer = maybeTrainer.get();
            if (!trainer.isActive()) {
                trainer.setActive(true);
                return Optional.of(trainer);
            } else {
                logger.info("Trainer already activated");
                return Optional.empty();
            }
        } else {
            logger.info("Trainer not found");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Trainer> deactivateAccount(String username) {
        Optional<Trainer> maybeTrainer = select(username);
        if (maybeTrainer.isPresent()) {
            Trainer trainer = maybeTrainer.get();
            if (trainer.isActive()) {
                trainer.setActive(false);
                return Optional.of(trainer);
            } else {
                logger.info("Trainer already deactivated");
                return Optional.empty();
            }
        } else {
            logger.info("Trainer not found");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Trainer> changePassword(String username, String password, String newPassword) {
        try {
            Optional<Trainer> maybeTrainer = select(username);
            if (maybeTrainer.isPresent()) {
                Trainer trainer = maybeTrainer.get();
                if (authenticationService.authenticate(trainer, password)) {
                    trainer.setPassword(newPassword);
                    update(trainer);
                }
                return Optional.of(trainer);
            } else {
                return Optional.empty();
            }
        } catch (UnauthorisedException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
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

    public List<Training> getTrainingsByCriteria(String username, Date from, Date to, String traineeUsername) {
        Optional<Trainer> maybeTrainer = select(username);
        if (maybeTrainer.isPresent()) {
            List<Training> trainings = maybeTrainer.get().getTrainings();
            return trainings.stream()
                    .filter(tr -> tr.getTrainee().getUsername().equals(traineeUsername))
                    .filter(tr ->
                            tr.getTrainingDate().after(from)
                                    && tr.getTrainingDate().before(to))
                    .toList();
        } else {
            return List.of();
        }
    }

    public Trainer addTrainee(String trainerUsername, Trainee trainee) {
        Optional<Trainer> maybeTrainer = select(trainerUsername);
        if (maybeTrainer.isPresent()) {
            Trainer trainer = maybeTrainer.get();
            trainee.addTrainer(trainer);
            update(trainer);
            return trainer;
        } else {
            return null;
        }
    }

    public List<Trainer> notAssignedTrainers(String traineeUsername) {
        return trainerDao.getAllExcludingTrainee(traineeUsername);
    }

    /*
    14. Get Trainee Trainings List by trainee username and criteria (from date, to date, trainer
    name, training type).
    15. Get Trainer Trainings List by trainer username and criteria (from date, to date, trainee
    name).
    16. Add training.
    17. Get trainers list that not assigned on trainee by trainee's username.
    18. Update Tranee's trainers list
    19. Test.
    *
    *
    *
    * */

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
