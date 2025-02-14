package com.example;

import com.example.springcrm.dao.*;

import com.example.springcrm.exception.DeletingNonexistentUserException;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.Training;
import com.example.springcrm.model.TrainingType;
import com.example.springcrm.service.TraineeService;
import com.example.springcrm.service.TrainerService;
import com.example.springcrm.service.TrainingService;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javax.persistence.EntityExistsException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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


    /**
     * There is no way to save new Training Type from app other than this.
     */
    private static class TrainingTypeDao implements Dao<TrainingType> {
        private SessionFactory factory = HibernateUtil.getSessionFactory();

        @Override
        public Optional<TrainingType> get(String id) {
            Session session = null;
            TrainingType result;
            try {
                session = factory.openSession();

                Query<TrainingType> query = session.createQuery(
                        "FROM TrainingType t WHERE t.id LIKE :id", TrainingType.class
                );
                query.setParameter("id", (id));

                result = query.getResultList().get(0);
            } catch (IndexOutOfBoundsException ex) {
                return Optional.empty();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to get TrainingType. " + ex.getClass() + ex.getMessage());
            } finally {
                if (session != null) {
                    session.close();
                }
            }
            return Optional.of(result);
        }

        @Override
        public void create(TrainingType trainingType) {
            Session session = null;
            Transaction transaction = null;
            try {

                session = factory.openSession();
                transaction = session.beginTransaction();

                session.persist(trainingType);
                transaction.commit();

            } catch (EntityExistsException | ConstraintViolationException ex) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new UserAlreadyExistsException("Failed to create trainingType: " + trainingType.toString()
                        + " reason: " + ex.getMessage());
            } catch (PropertyValueException ex) {
                throw new IllegalArgumentException("Failed to create trainingType: " + trainingType.toString()
                        + " reason: " + ex.getMessage());
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        }

        @Override
        public void update(TrainingType newValue) {
            /// There is no way nor need to update it from the app.
        }

        @Override
        public void delete(TrainingType value) throws DeletingNonexistentUserException {
            /// There is no way nor need to update it from the app.
        }

        @Override
        public List<TrainingType> getAll() {
            return List.of();
        }
    }

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
                trainingType,
                new Date(),
                Duration.ofHours(1)
        );
        sampleTraining.setId(null);
    }

    @AfterEach
    void cleanUpDatabase() {
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
                trainingType,
                new Date(),
                Duration.ofHours(1)
        );
        trainingTypeDao.create(trainingType);

        traineeService.create(trainee);
        trainerService.create(trainer);

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
                trainingType,
                currentDate,
                Duration.ofHours(1)
        );
        traineeService.create(trainee);
        trainerService.create(trainer);
        trainingTypeDao.create(trainingType);
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
