package com.example.springcrm.service;

import com.example.springcrm.dao.TraineeDao;
import com.example.springcrm.dao.TrainingDao;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.storage.TraineeStorage;
import com.example.springcrm.storage.TrainingStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.springcrm.SpringCrmApplicationTests.assertThatListsAreEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class TraineeServiceTest {
    @TestConfiguration
    static class TraineeServiceTestContextConfiguration {

        @Bean
        public TraineeService traineeService(TraineeDao traineeDao) {
            return new TraineeService(traineeDao);
        }

        @Bean
        public TraineeDao traineeDao(TraineeStorage traineeStorage) {
            return new TraineeDao(traineeStorage);
        }

        @Bean
        public TraineeStorage traineeStorage() {
            return new TraineeStorage();
        }
    }

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TraineeStorage traineeStorage;

    @AfterEach
    void tearDown() {
        traineeStorage.cleanAll();
    }

    @Test
    void createTrainee_OK() {
        Trainee trainee = new Trainee(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 1",
                null
                );
        traineeService.create(trainee);
        //User gets userID in a process of creation.
        assertEquals(trainee, traineeService.select(trainee.getUsername()));
    }

    @Test
    void createTraineeUsernameOverlap_OK() {
        Trainee trainee1 = new Trainee(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 1",
                null
        );

        Trainee trainee2 = new Trainee(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 76",
                null
        );

        Trainee trainee3 = new Trainee(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 176",
                null
        );

        traineeService.create(trainee1);
        traineeService.create(trainee2);
        traineeService.create(trainee3);


        List<Trainee> trainees = traineeService.list();
        assertEquals(trainees.size(), 3);
        assertThatListsAreEqual(
                List.of(trainee1, trainee2, trainee3),
                trainees);

    }

    @Test
    void updateTrainee_OK() {
        Trainee trainee = new Trainee(
                "Maria",
                "Petrenko",
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176",
                null
        );
        traineeService.create(trainee);

        trainee.setLastName("Ivanenko");
        traineeService.update(trainee);

        Trainee selected = traineeService.select(trainee.getUsername());
        assertEquals(trainee, selected);
        assertEquals("Maria.Ivanenko", selected.getUsername());
    }

    @Test
    void deleteTrainee_OK() {
        Trainee trainee = new Trainee(
                "Maria",
                "Petrenko",
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176",
                null
        );

        traineeService.create(trainee);

        traineeService.delete(trainee);
        List<Trainee> trainees = traineeService.list();
        assertEquals(0, trainees.size());
    }

    @Test
    void listTrainees_OK() {
        int numberOfTrainees = 10;

        Trainee trainee = new Trainee(
                "Maria",
                "Petrenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 176",
                null
        );

        List<Trainee> trainees = new ArrayList<>();

        for (int i = 0; i < numberOfTrainees; i++) {

            traineeService.create(trainee);
            trainees.add(trainee);
        }

        List<Trainee> listedTrainees = traineeService.list();
        assertEquals(numberOfTrainees, listedTrainees.size());

        for (Trainee t : listedTrainees) {
            assertEquals(t.getFirstName(), "Maria");
            assertEquals(t.getLastName(), "Petrenko");
        }
    }


    @Test
    void listTraineesEmptyList_OK() {
        List<Trainee> trainees = traineeService.list();
        assertEquals(0, trainees.size());
    }



}
