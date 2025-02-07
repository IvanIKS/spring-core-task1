package com.example.springcrm.service;

import com.example.springcrm.dao.TrainerDao;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.storage.TrainerStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.springcrm.SpringCrmApplicationTests.assertThatListsAreEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TrainerServiceTest {
    
    private Map<String, Trainer> trainers = new HashMap<String, Trainer>();
    private TrainerStorage trainerStorage = new TrainerStorage(trainers);
    private TrainerDao trainerDao = new TrainerDao(trainerStorage); 
    private TrainerService trainerService = new TrainerService(trainerDao);  

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
    void createTrainerNullOrEmptyName_NotOK() {
        Trainer trainer1 = new Trainer(
            null,
            "Ivanenko",
            null,
            "123456",
            true,
            "Boxing",
            null
        );

        Trainer trainer2 = new Trainer(
                "Ivan",
                null,
                null,
                "123456",
                true,
                "Boxing",
                null
        );

        Trainer trainer3 = new Trainer(
                null,
                null,
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

        assertEquals(0, trainers.size());    

        //Check for empty names
        trainer1.setFirstName("");
        trainer2.setLastName("");
        trainer3.setFirstName("");
        trainer3.setLastName("");

        trainerService.create(trainer1);
        trainerService.create(trainer2);
        trainerService.create(trainer3);


        trainers = trainerService.list();
        assertEquals(0, trainers.size());    
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
    void usernameChangeOverlap_OK() {
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
            "Vitaly",
            "Ivanenko",
            null,
            "123456",
            true,
            "Boxing",
            null
        );

        trainerService.create(trainer1);
        trainerService.create(trainer2);

        trainer2.setFirstName("Ivan");

        trainerService.update(trainer2);
        assertNotEquals(trainer1.getUsername(), trainer2.getUsername());
        assertEquals("Ivan.Ivanenko1", trainer2.getUsername());
    }

    @Test
    void updateTrainerNullName_NotOK() {
        Trainer trainer1 = new Trainer(
            "Ivan",
            "Ivanenko",
            null,
            "123456",
            true,
            "Boxing",
            null
        );


        trainerService.create(trainer1);
        
        trainerService.update(new Trainer(
            null,
            "Ivanenko",
            null,
            "123456",
            true,
            "Boxing",
            null
        ));

        trainerService.update(new Trainer(
            "Ivan",
            null,
            null,
            "123456",
            true,
            "Boxing",
            null
        ));

        trainerService.update(new Trainer(
            null,
            null,
            null,
            "123456",
            true,
            "Boxing",
            null
        ));

        //Should not be updated with invalid username.

        assertEquals(trainer1, trainerService.select(trainer1.getUsername()));    
    }

    @Test
    void updateTrainerEmptyName_NotOK() {
        Trainer trainer1 = new Trainer(
            "Ivan",
            "Ivanenko",
            null,
            "123456",
            true,
            "Boxing",
            null
        );


        trainerService.create(trainer1);
        
        trainerService.update(new Trainer(
            "",
            "Ivanenko",
            null,
            "123456",
            true,
            "Boxing",
            null
        ));

        trainerService.update(new Trainer(
            "Ivan",
            "",
            null,
            "123456",
            true,
            "Boxing",
            null
        ));

        trainerService.update(new Trainer(
            "",
            "",
            null,
            "123456",
            true,
            "Boxing",
            null
        ));

        //Should not be updated with invalid username.

        assertEquals(trainer1, trainerService.select(trainer1.getUsername()));    
    }

    @Test
    void listTrainersEmptyList_OK() {
        List<Trainer> trainers = trainerService.list();
        assertEquals(0, trainers.size());
    }
}
