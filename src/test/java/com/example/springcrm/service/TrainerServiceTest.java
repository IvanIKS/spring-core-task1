package com.example.springcrm.service;

import com.example.springcrm.dao.TrainerDao;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.storage.TrainerStorage;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class TrainerServiceTest {
    @TestConfiguration
    static class TrainerServiceTestContextConfiguration {
        @Bean
        public TrainerService trainerService(TrainerDao trainerDao) {
            return new TrainerService(trainerDao);
        }

        @Bean
        public TrainerDao trainerDao(TrainerStorage trainerStorage) {
            return new TrainerDao(trainerStorage);
        }

        @Bean
        public TrainerStorage trainerStorage() {
            return new TrainerStorage();
        }
    }

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainerStorage trainerStorage;


    @AfterEach
    void tearDown() {
        trainerStorage.cleanAll();
    }

    @Test
    void createTrainer_OK() {
        Trainer trainer = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing",
                null
        );
        trainerService.create(trainer);
        //User gets userID in a process of creation.
        assertEquals(trainer, trainerService.select(trainer.getUsername()));
    }


    @Test
    void createTrainerUsernameOverlap_OK() {
        Trainer trainer1 = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing",
                null
        );

        Trainer trainer2 = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing",
                null
        );

        Trainer trainer3 = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing",
                null
        );

        trainerService.create(trainer1);
        trainerService.create(trainer2);
        trainerService.create(trainer3);


        List<Trainer> trainers = trainerService.list();
        assertEquals(trainers.size(), 3);
        assertThatListsAreEqual(
                List.of(trainer1, trainer2, trainer3),
                trainers);

    }


    @Test
    void updateTrainer_OK() {
        Trainer trainer = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing",
                null
        );
        trainerService.create(trainer);

        trainer.setFirstName("Vitaly");
        trainerService.update(trainer);

        Trainer selected = trainerService.select(trainer.getUsername());
        assertEquals(trainer, selected);
        assertEquals("Vitaly", selected.getFirstName());
        assertEquals("Vitaly.Ivanenko", selected.getUsername());
    }

    @Test
    void listTrainersEmptyList_OK() {
        List<Trainer> trainers = trainerService.list();
        assertEquals(0, trainers.size());
    }


}
