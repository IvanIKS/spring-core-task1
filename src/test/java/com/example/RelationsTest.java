package com.example;


import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.Training;
import com.example.springcrm.model.TrainingType;
import com.example.utils.TrainingTypeDao;
import com.example.springcrm.dao.HibernateUtil;
import com.example.springcrm.dao.TraineeDao;
import com.example.springcrm.dao.TrainerDao;
import com.example.springcrm.dao.TrainingDao;
import com.example.springcrm.service.TraineeService;
import com.example.springcrm.service.TrainerService;
import com.example.springcrm.service.TrainingService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Class specially purposed for testing of Many to Many relationships and
 * connection scenarios that aren't limited by functionality of one class.
 */


public class RelationsTest {
    private TraineeDao traineeDao = new TraineeDao(HibernateUtil.getSessionFactory());
    private TrainerDao trainerDao = new TrainerDao(HibernateUtil.getSessionFactory());
    private TrainingDao trainingDao = new TrainingDao(HibernateUtil.getSessionFactory());
    private TraineeService traineeService = new TraineeService(traineeDao);
    private TrainerService trainerService = new TrainerService(trainerDao);
    private TrainingService trainingService = new TrainingService(trainingDao);

    private TrainingTypeDao trainingTypeDao = new TrainingTypeDao();


    @AfterEach
    public void cleanUp() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.createQuery("delete from Training").executeUpdate();
        session.createQuery("delete from TrainingType").executeUpdate();
        session.createQuery("delete from Trainee").executeUpdate();
        session.createQuery("delete from Trainer").executeUpdate();

        transaction.commit();
        session.close();
    }

    @Test
    void manyToMany_OK() {
        Trainee trainee1 = new Trainee(
                "Oksana",
                "Kovalenko",
                "oksana.kovalenko",
                "parol123",
                true,
                new Date(),
                "Vulytsia Shevchenka, 1" // address
        );

        Trainee trainee2 = new Trainee(
                "Andriy",
                "Shevchenko",
                "andriy.shevchenko",
                "parol456",
                true,
                new Date(),
                "Vulytsia Franko, 5"
        );

        Trainee trainee3 = new Trainee(
                "Iryna",
                "Melnyk",
                "iryna.melnyk",
                "parol789",
                true,
                new Date(),
                "Vulytsia Krivonosa, 12"
        );

        Trainer trainer1 = new Trainer(
                "Volodymyr",
                "Petrenko",
                "volodymyr.petrenko",
                "tajna",
                true,
                "Yoga"
        );

        Trainer trainer2 = new Trainer(
                "Natalia",
                "Bondarenko",
                "natalia.bondarenko",
                "tajna",
                true,
                "Pilates"
        );

        Trainer trainer3 = new Trainer(
                "Roman",
                "Tkachenko",
                "roman.tkachenko",
                "tajna",
                true,
                "CrossFit"
        );

        traineeService.create(trainee1);
        traineeService.create(trainee2);
        traineeService.create(trainee3);

        trainerService.create(trainer1);
        trainerService.create(trainer2);
        trainerService.create(trainer3);

        // Establish the many-to-many associations:
        // For Trainees: set their corresponding Trainers
        trainee1.addTrainer(trainer1);
        trainee1.addTrainer(trainer2);

        trainee2.addTrainer(trainer2);
        trainee2.addTrainer(trainer3);

        trainee3.addTrainer(trainer3);
        trainee3.addTrainer(trainer1);

        // For Trainers: set their corresponding Trainees
        trainer1.addTrainee(trainee1);
        trainer1.addTrainee(trainee2);

        trainer2.addTrainee(trainee2);
        trainer2.addTrainee(trainee3);

        trainer3.addTrainee(trainee3);
        trainer3.addTrainee(trainee1);

        traineeService.update(trainee1);
        traineeService.update(trainee2);
        traineeService.update(trainee3);

        trainerService.update(trainer1);
        traineeService.update(trainee2);
        traineeService.update(trainee3);

        assertEquals(Set.of(trainer1, trainer2), trainee1.getTrainers());
        assertEquals(Set.of(trainer2, trainer3), trainee2.getTrainers());
        assertEquals(Set.of(trainer3, trainer1), trainee3.getTrainers());

        assertEquals(Set.of(trainee1, trainee2), trainer1.getTrainees());
        assertEquals(Set.of(trainee2, trainee3), trainer2.getTrainees());
        assertEquals(Set.of(trainee3, trainee1), trainer3.getTrainees());
    }

    @Test
    void cascadeDeleteTrainings_OK() {
        TrainingType dancing = new TrainingType("Dancing");

        Trainee trainee = new Trainee(
                "Iryna",
                "Melnyk",
                "iryna.melnyk",
                "parol789",
                true,
                new Date(),
                "Vulytsia Krivonosa, 12"
        );

        Trainer trainer = new Trainer(
                "Natalia",
                "Bedich",
                "natalia.bondarenko",
                "tajna",
                true,
                "Dancing"
        );

        traineeService.create(trainee);
        trainerService.create(trainer);

        //This simulates 10 consecutive weeks of training.
        int numberOfTrainings = 10;
        int timeInWeek = 7 * 24 * 60 * 60 * 1000;
        Date startDate = new Date();

        Training training;

        for (int i = 0; i < numberOfTrainings; i++) {
            Date newDate = new Date(startDate.getTime() + timeInWeek);

            training = new Training(
                    trainee,
                    trainer,
                    "Evening dancing",
                    dancing,
                    newDate,
                    Duration.ofHours(2)
            );
            trainingService.create(training);
        }

        assertEquals(10, trainingService.list().size());

        traineeService.delete(trainee);
        assertEquals(0, trainingService.list().size());
    }

    @Test
    void durationPersistence_OK() {
        TrainingType dancing = new TrainingType("Dancing");

        Trainee trainee = new Trainee(
                "Iryna",
                "Melnyk",
                "iryna.melnyk",
                "parol789",
                true,
                new Date(),
                "Vulytsia Krivonosa, 12"
        );

        Trainer trainer = new Trainer(
                "Natalia",
                "Bedich",
                "natalia.bondarenko",
                "tajna",
                true,
                "Dancing"
        );

        traineeService.create(trainee);
        trainerService.create(trainer);

        Date startDate = new Date();

        Training training = new Training(
                trainee,
                trainer,
                "Evening dancing",
                dancing,
                startDate,
                Duration.ofHours(2)
        );
        trainingService.create(training);

        Training selected = trainingService.select(training.getId()).get();

        assertEquals(training.getTrainingDuration(),
                selected.getTrainingDuration());
    }

    @Test
    void datePersistence_OK() {
        TrainingType dancing = new TrainingType("Dancing");

        Trainee trainee = new Trainee(
                "Iryna",
                "Melnyk",
                "iryna.melnyk",
                "parol789",
                true,
                new Date(),
                "Vulytsia Krivonosa, 12"
        );

        Trainer trainer = new Trainer(
                "Natalia",
                "Bedich",
                "natalia.bondarenko",
                "tajna",
                true,
                "Dancing"
        );

        traineeService.create(trainee);
        trainerService.create(trainer);

        Date startDate = new Date();

        Training training = new Training(
                trainee,
                trainer,
                "Evening dancing",
                dancing,
                startDate,
                Duration.ofHours(2)
        );
        trainingService.create(training);

        Training selectedTraining = trainingService.select(training.getId()).get();

        assertEquals(training.getTrainingDate(),
                selectedTraining.getTrainingDate());

        Trainee selectedTrainee = traineeService.select(trainee.getUsername()).get();
        assertEquals(trainee.getDateOfBirth(),
                selectedTrainee.getDateOfBirth());
    }

    @Test
    void foreignKeyConnections_OK() {
        TrainingType dancing = new TrainingType("Dancing");

        Trainee trainee = new Trainee(
                "Iryna",
                "Melnyk",
                "iryna.melnyk",
                "parol789",
                true,
                new Date(),
                "Vulytsia Krivonosa, 12"
        );

        Trainer trainer = new Trainer(
                "Natalia",
                "Bedich",
                "natalia.bondarenko",
                "tajna",
                true,
                "Dancing"
        );

        traineeService.create(trainee);
        trainerService.create(trainer);

        Training training = new Training(
                trainee,
                trainer,
                "Evening dancing",
                dancing,
                new Date(),
                Duration.ofHours(2)
        );
        trainingService.create(training);

        Training selectedTraining = trainingService.select(training.getId()).get();

        assertEquals(trainee, selectedTraining.getTrainee());

        assertEquals(trainer, selectedTraining.getTrainer());
    }

    @Test
    void nonExistingTrainee_NotOK() {
        TrainingType dancing = new TrainingType("Dancing");

        Trainee trainee = new Trainee(
                "Iryna",
                "Melnyk",
                "iryna.melnyk",
                "parol789",
                true,
                new Date(),
                "Vulytsia Krivonosa, 12"
        );

        Trainer trainer = new Trainer(
                "Natalia",
                "Bedich",
                "natalia.bondarenko",
                "tajna",
                true,
                "Dancing"
        );

        //Do not persist them to the database.


        Training training = new Training(
                trainee,
                trainer,
                "Evening dancing",
                dancing,
                new Date(),
                Duration.ofHours(2)
        );
        trainingService.create(training);

        //Should not create training when trainee and/or trainer aren't in database.
        assertEquals(Optional.empty(), trainingService.select(training.getId()));

        //For "or" cases:

        //Existing trainee, non-existing trainer.
        traineeService.create(trainee);
        trainingService.create(training);
        assertEquals(Optional.empty(), trainingService.select(training.getId()));

        traineeService.delete(trainee);

        //Existing trainer, non-existing trainee.
        trainerService.create(trainer);
        trainingService.create(training);
        assertEquals(Optional.empty(), trainingService.select(training.getId()));
    }

}
