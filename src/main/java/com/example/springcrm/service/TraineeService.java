package com.example.springcrm.service;

import com.example.springcrm.dao.TraineeDao;
import com.example.springcrm.dao.TrainerDao;
import com.example.springcrm.exception.DeletingNonexistentUserException;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UnauthorisedException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("traineeService")
public class TraineeService extends UserService {
    private static final int RANDOM_PASSWORD_LENGTH = 10;
    private TraineeDao traineeDao;
    private AuthenticationService authenticationService;

    private final Logger logger = LoggerFactory.getLogger(TrainerService.class);


    @Autowired
    public TraineeService(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
        this.authenticationService = new TraineeAuthenticationService(traineeDao);
        logger.info("TraineeService created");
    }

    private static class TraineeAuthenticationService extends AuthenticationService {
        protected TraineeAuthenticationService(TraineeDao dao) {
            super(dao);
        }
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

            trainee = this.handleUsernameOverlap(trainee);

            traineeDao.create(trainee);
            logger.info("Trainee created with username: {}", trainee.getUsername());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }



    public void update(Trainee trainee) {
        try {
            authenticationService.authenticate(trainee);
            traineeDao.update(trainee);
            logger.info("Trainee {} updated", trainee.getUsername());
        } catch (OutdatedUsernameException e) {
            logger.error(e.getMessage());
            trainee = select(trainee.getUserId()).orElse(trainee);
            String username = generateUsername(trainee.getFirstName(), trainee.getLastName());
            trainee.setUsername(username);

            updateWithNameCheck(trainee);
        } catch (UnauthorisedException e) {
            logger.error("Failed to authenticate user" + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update a user, user had invalid values: " + e.getMessage());
        }
    }

    public void delete(Trainee trainee) {
        try {
            authenticationService.authenticate(trainee);
            traineeDao.delete(trainee);
            logger.info("Trainee {} deleted", trainee.getUsername());
        } catch (UnauthorisedException e) {
            logger.error("Failed to authenticate user" + e.getMessage());
        } catch (DeletingNonexistentUserException ex) {
            logger.error("Trying to delete user that does not exist in database" + ex.getMessage());
        }
    }

    public Optional<Trainee> select(String username) {
        return traineeDao.getByUsername(username);
    }

    public List<Trainee> list() {
        return traineeDao.getAll();
    }


    @Override
    public Optional<Trainee> activateAccount(String username) {
        Optional<Trainee> maybeTrainee = select(username);
        if (maybeTrainee.isPresent()) {
            Trainee trainee = maybeTrainee.get();
            if ( ! trainee.isActive()) {
                trainee.setActive(true);
                return Optional.of(trainee);
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
    public Optional<Trainee> deactivateAccount(String username) {
        Optional<Trainee> maybeTrainee = select(username);
        if (maybeTrainee.isPresent()) {
            Trainee trainee = maybeTrainee.get();
            if (trainee.isActive()) {
                trainee.setActive(false);
                return Optional.of(trainee);
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
    public Optional<Trainee> changePassword(String username, String password, String newPassword) {
        try {
            Optional<Trainee> maybeTrainee = select(username);
            if (maybeTrainee.isPresent()) {
                Trainee trainee = maybeTrainee.get();
                if (authenticationService.authenticate(trainee, password)) {
                    trainee.setPassword(newPassword);
                    update(trainee);
                }
                return Optional.of(trainee);
            } else {
                return Optional.empty();
            }
        } catch (UnauthorisedException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public List<Training> getTrainingsByCriteria(String username, Date from, Date to, String trainerUsername, TrainingType trainingType) {
        Optional<Trainee> maybeTrainee = select(username);
        if (maybeTrainee.isPresent()) {
            List<Training> trainings = maybeTrainee.get().getTrainings();
            return trainings.stream()
                    .filter(tr -> tr.getTrainingType().equals(trainingType))
                    .filter(tr -> tr.getTrainer().getUsername().equals(trainerUsername))
                    .filter(tr ->
                            tr.getTrainingDate().after(from)
                                    && tr.getTrainingDate().before(to))
                    .toList();
        } else {
            return List.of();
        }
    }

    public Trainee addTrainer(String traineeUsername, Trainer trainer) {
        Optional<Trainee> maybeTrainee = select(traineeUsername);
        if (maybeTrainee.isPresent()) {
            Trainee trainee = maybeTrainee.get();
            trainee.addTrainer(trainer);
            update(trainee);
            return trainee;
        } else {
            return null;
        }
    }

    private Trainee handleUsernameOverlap(Trainee trainee) {
        trainee.setUserId(null);
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
