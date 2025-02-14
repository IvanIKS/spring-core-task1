package com.example.service;

import com.example.utils.TrainingTypeDao;
import com.example.springcrm.dao.*;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.Training;
import com.example.springcrm.model.TrainingType;
import com.example.springcrm.service.TraineeService;
import com.example.springcrm.service.TrainerService;
import com.example.springcrm.service.TrainingService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainingServiceTest {
    private TraineeDao traineeDao = new TraineeDao(HibernateUtil.getSessionFactory());
    private TrainerDao trainerDao = new TrainerDao(HibernateUtil.getSessionFactory());
    private TrainingDao trainingDao = new TrainingDao(HibernateUtil.getSessionFactory());
    private TraineeService traineeService = new TraineeService(traineeDao);
    private TrainerService trainerService = new TrainerService(trainerDao);
    private TrainingService trainingService = new TrainingService(trainingDao);

    private TrainingTypeDao trainingTypeDao = new TrainingTypeDao();

    Trainee trainee;
    Trainer trainer;
    TrainingType trainingType;
    Training sampleTraining;



    @BeforeEach
    void setUp() {
        setDefaultData();

        saveRequiredEntities();
    }

    private void saveRequiredEntities() {
        //Training depends on these types, so we need to save it.
        trainingTypeDao.create(trainingType);
        traineeService.create(trainee);
        trainerService.create(trainer);
    }

    private void setDefaultData() {
        trainee = new Trainee(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 1"
        );

        trainer = new Trainer(
                "Vitaly",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
        );

        trainingType = new TrainingType("Yoga");

        sampleTraining = new Training(
                trainee,
                trainer,
                "Yoga",
                trainingType,
                new Date(),
                Duration.ofHours(1)
        );
        sampleTraining.setId(null);
    }

    @AfterEach
    void cleanUp() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.createQuery("delete from Training").executeUpdate();
        session.createQuery("delete from TrainingType").executeUpdate();
        session.createQuery("delete from Trainee").executeUpdate();
        session.createQuery("delete from Trainer").executeUpdate();

        transaction.commit();
        session.close();

        trainee.setUserId(null);
        trainer.setUserId(null);

    }


    @Test
    void saveTraining_OK() {

        trainingService.create(sampleTraining);
        Optional<Training> selected = trainingService.select(sampleTraining.getId());

        assertEquals(Optional.of(sampleTraining), selected);
    }

    @Test
    void saveMultipleTrainings_OK() {
        //Test for the case when person goes to one trainer.
        //This simulates 10 consecutive weeks of training.
        int numberOfTrainings = 10;
        int timeInWeek = 7 * 24 * 60 * 60 * 1000;

        for (int i = 0; i < numberOfTrainings; i++) {
            Date newDate = new Date(sampleTraining.getTrainingDate().getTime() + timeInWeek);

            sampleTraining = new Training(
                    trainee,
                    trainer,
                    "SomeName",
                    trainingType,
                    newDate,
                    Duration.ofHours(1)
            );
            trainingService.create(sampleTraining);
        }

        List<Training> selectedTrainings = trainingService.list();

        for (int i = 0; i < numberOfTrainings; i++) {
            assertEquals(sampleTraining.getTrainee(), selectedTrainings.get(i).getTrainee());
            assertEquals(sampleTraining.getTrainer(), selectedTrainings.get(i).getTrainer());
            assertEquals(sampleTraining.getTrainingType(), selectedTrainings.get(i).getTrainingType());
        }
    }

    @Test
    void saveExistingTraining_NotOK() {

        Training existingTraining = new Training(
                trainee,
                trainer,
                "SomeName",
                trainingType,
                new Date(),
                Duration.ofHours(1)
        );

        trainingService.create(existingTraining);
        assertEquals(1, trainingService.list().size());

        trainingService.create(existingTraining);

        //We try to test that we can't create two trainings with one training and one time.
        assertEquals(1, trainingService.list().size());
    }

    @Test
    void createSameTimeTrainings_OK() {
        Date currentDate = new Date();
        Training training1 = new Training(
                trainee,
                trainer,
                "SomeName",
                trainingType,
                currentDate,
                Duration.ofHours(1)
        );

        trainingService.create(training1);

        List<Training> trainings = trainingService.list();
        assertEquals(1, trainings.size());

        Trainee trainee2 = new Trainee(
                "Olexandr",
                "Serhiienko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 1"
        );

        Trainer trainer2 = new Trainer(
                "Max",
                "Payne",
                null,
                "123456",
                true,
                "Boxing"
        );

        TrainingType newType = new TrainingType("Boxing");
        trainingTypeDao.create(newType);

        Training training2 = new Training(
                trainee2,
                trainer2,
                "SomeName",
                newType,
                currentDate,
                Duration.ofHours(1)
        );
        traineeService.create(trainee2);
        trainerService.create(trainer2);

        trainingService.create(training2);


        trainings = trainingService.list();
        assertEquals(2, trainings.size());
    }

    @Test
    void ListTrainingsEmpty_OK() {
        List<Training> trainings = trainingService.list();
        assertEquals(0, trainings.size());
    }


}
