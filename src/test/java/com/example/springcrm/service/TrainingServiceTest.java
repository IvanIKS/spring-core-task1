package com.example.springcrm.service;

import com.example.springcrm.dao.TrainingDao;
import com.example.springcrm.model.Training;
import com.example.springcrm.model.TrainingType;
import com.example.springcrm.storage.TrainingStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.springcrm.SpringCrmApplicationTests.assertThatListsAreEqual;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
class TrainingServiceTest {

    @TestConfiguration
    static class TrainingServiceTestContextConfiguration {

        @Bean
        public TrainingService trainingService(TrainingDao trainingDao) {
            return new TrainingService(trainingDao);
        }

        @Bean
        public TrainingDao trainingDao(TrainingStorage trainingStorage) {
            return new TrainingDao(trainingStorage);
        }

        @Bean
        public TrainingStorage trainingStorage() {
            return new TrainingStorage();
        }
    }

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TrainingStorage trainingStorage;

    private Training sampleTraining;

    @BeforeEach
    void setUp() {
        sampleTraining = new Training(
                "trainee123",
                "trainer456",
                "John Doe",
                new TrainingType("Yoga"),
                new Date(),
                Duration.ofHours(1)
        );
    }

    @AfterEach
    void tearDown() {
        trainingStorage.cleanAll();
    }

    @Test
    void saveTraining_OK() {
        trainingService.create(sampleTraining);
        Training selected = trainingService.select(sampleTraining.getId());

        assertEquals(sampleTraining, selected);
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
                    "trainee123",
                    "trainer456",
                    "John Doe",
                    new TrainingType("Yoga"),
                    newDate,
                    Duration.ofHours(1)
            );
            trainingService.create(sampleTraining);
        }

        List<Training> selectedTrainings = trainingService.list();

        for (int i = 0; i < numberOfTrainings; i++) {
            assertEquals(sampleTraining.getTraineeId(), selectedTrainings.get(i).getTraineeId());
            assertEquals(sampleTraining.getTrainerId(), selectedTrainings.get(i).getTrainerId());
            assertEquals(sampleTraining.getTrainingType(), selectedTrainings.get(i).getTrainingType());
        }
    }

    @Test
    void saveExistingTraining_NotOK() {
        trainingService.create(sampleTraining);
        trainingService.create(sampleTraining);

        //We try to test that we can't create two trainings with one training and one time.
        assertEquals(trainingService.list().size(), 1);
    }

    @Test
    void createSameTimeTrainings_OK() {
        Date currentDate = new Date();
        Training training1 = new Training(
                "trainee123",
                "trainer456",
                "John Doe",
                new TrainingType("Yoga"),
                currentDate,
                Duration.ofHours(1)
        );

        Training training2 = new Training(
                "trainee789",
                "trainer000",
                "Max Payne",
                new TrainingType("Boxing"),
                currentDate,
                Duration.ofHours(1)
        );

        trainingService.create(training1);
        trainingService.create(training2);
        List<Training> trainings = trainingService.list();
        assertEquals(trainings.size(), 2);
    }

    @Test
    void ListTrainingsEmpty_OK() {
        List<Training> trainings = trainingService.list();
        assertEquals(trainings.size(), 0);
    }

}