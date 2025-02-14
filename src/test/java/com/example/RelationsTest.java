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

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


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
    void saveTrainingWithNonExistingTrainee_NotOK() {
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

    @Test
    void trainerAddTrainee_OK() {
        Trainer trainer = new Trainer(
                "Volodymyr",
                "Petrenko",
                "Volodymyr.Petrenko",
                "tajna",
                true,
                "Yoga"
        );

        Trainee trainee = new Trainee(
                "Oksana",
                "Kovalenko",
                "Oksana.Kovalenko",
                "parol123",
                true,
                new Date(),
                "Vulytsia Shevchenka, 1"
        );
        traineeService.create(trainee);
        trainerService.create(trainer);

        trainerService.addTrainee(trainer.getUsername(), trainee);

        Trainee selectedTrainee = traineeService.select(trainee.getUsername()).get();
        Trainer selectedTrainer = trainerService.select(trainer.getUsername()).get();

        assertTrue(selectedTrainer.getTrainees().contains(trainee));
        assertTrue(selectedTrainee.getTrainers().contains(trainer));

    }

    @Test
    void getUnassignedTrainersForTrainee_OK() {
        Trainee trainee = new Trainee(
                "Oksana",
                "Kovalenko",
                "oksana.kovalenko",
                "parol123",
                true,
                new Date(),
                "Vulytsia Shevchenka, 1"
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

        traineeService.create(trainee);
        trainerService.create(trainer1);
        trainerService.create(trainer2);
        trainerService.create(trainer3);

        trainee.addTrainer(trainer1);
        trainer1.addTrainee(trainee);

        traineeService.update(trainee);
        trainerService.update(trainer1);

        List<Trainer> unassignedTrainers = trainerService.getUnassignedTrainersForTrainee(trainee.getUsername());

        assertNotNull(unassignedTrainers);
        assertEquals(2, unassignedTrainers.size());

        assertTrue(unassignedTrainers.contains(trainer2));
        assertTrue(unassignedTrainers.contains(trainer3));
    }

    @Test
    void trainerServiceGetTrainingsByCriteria_OK() {
        // Create two Training objects with different training dates.
        // training1  inside our criteria, training2 outside.
        Date trainingDate1 = new GregorianCalendar(2025, Calendar.JANUARY, 15).getTime();
        Date trainingDate2 = new GregorianCalendar(2025, Calendar.MARCH, 10).getTime();
        TrainingType trainingType = new TrainingType("Dancing");
        trainingTypeDao.create(trainingType);

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

        Training training1 = new Training(
                trainee,
                trainer,
                "Yoga",
                trainingType,
                trainingDate1,
                Duration.ofHours(1)
        );
        Training training2 = new Training(
                trainee,
                trainer,
                "Yoga",
                trainingType,
                trainingDate2,
                Duration.ofHours(1)
        );

        trainingService.create(training1);
        trainingService.create(training2);

        // Look for trainings conducted by "volodymyr.petrenko" between Jan 1, 2025 and Feb 1, 2025
        // for the trainee "oksana.kovalenko".
        Date from = new GregorianCalendar(2025, Calendar.JANUARY, 1).getTime();
        Date to = new GregorianCalendar(2025, Calendar.FEBRUARY, 1).getTime();

        // Execute the query method:
        List<Training> trainings = trainerService.getTrainingsByCriteria(
                trainer.getUsername(),
                from,
                to,
                trainee.getUsername()
        );

        // Validate the results:
        // Only training1 should be returned because training2 is outside the date range.
        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        assertEquals(training1.getId(), trainings.get(0).getId());
    }

    @Test
    void traineeServiceGetTrainingsByCriteria_OK() {
        TrainingType cardio = new TrainingType("Cardio");
        TrainingType strength = new TrainingType("Strength");
        trainingTypeDao.create(cardio);
        trainingTypeDao.create(strength);

        // Create a Trainee (Dmytro Hunko)
        Trainee trainee = new Trainee(
                "Dmytro",
                "Hunko",
                "Dmytro.Hunko",
                "parol789",
                true,
                new Date(),
                "Vulytsia Krivonosa, 12"
        );

        // Create a Trainer (Olexandr Tyhomir)
        Trainer trainer = new Trainer(
                "Olexandr",
                "Tyhomir",
                "Olexandr.Tyhomir",
                "tajna",
                true,
                "Bodybuilding"
        );

        // Persist trainee and trainer
        traineeService.create(trainee);
        trainerService.create(trainer);

        // Create two Training objects:
        // training1: date within criteria and type "Cardio"
        Date trainingDate1 = new GregorianCalendar(2025, Calendar.JANUARY, 15).getTime();
        Training training1 = new Training(
                trainee,
                trainer,
                "Cool training",
                cardio,
                trainingDate1,
                Duration.ofHours(1)
        );

        // training2: date outside criteria and type "Strength"
        Date trainingDate2 = new GregorianCalendar(2025, Calendar.MARCH, 10).getTime();
        Training training2 = new Training(
                trainee,
                trainer,
                "Cool training",
                strength,
                trainingDate2,
                Duration.ofHours(1)
        );
        // training2: date inside criteria and type "Strength"
        Date trainingDate3 = new GregorianCalendar(2025, Calendar.JANUARY, 10).getTime();
        Training training3 = new Training(
                trainee,
                trainer,
                "Cool training",
                strength,
                trainingDate3,
                Duration.ofHours(1)
        );
        // Persist both trainings
        trainingService.create(training1);
        trainingService.create(training2);

        // Date range: January 1, 2025 to February 1, 2025,
        // Trainer username: "Olexandr.Tyhomir",
        // Training type: Cardio
        Date from = new GregorianCalendar(2025, Calendar.JANUARY, 1).getTime();
        Date to = new GregorianCalendar(2025, Calendar.FEBRUARY, 1).getTime();

        // Execute the method under test
        List<Training> results = traineeService.getTrainingsByCriteria(
                trainee.getUsername(),
                from,
                to,
                trainer.getUsername(),
                cardio
        );

        // Validate the results: only training1 should be returned.
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(training1.getId(), results.get(0).getId());
    }

}
