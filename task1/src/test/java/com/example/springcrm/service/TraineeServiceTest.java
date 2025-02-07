package com.example.springcrm.service;

import com.example.springcrm.dao.TraineeDao;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.storage.TraineeStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.springcrm.SpringCrmApplicationTests.assertThatListsAreEqual;
import static org.junit.jupiter.api.Assertions.*;

public class TraineeServiceTest {

    Map<String, Trainee> trainees = new HashMap<String, Trainee>();
    private TraineeStorage traineeStorage = new TraineeStorage(trainees);
    private TraineeDao traineeDao = new TraineeDao(traineeStorage); 
    private TraineeService traineeService = new TraineeService(traineeDao);    
    
    

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
    void createTraineeNullOrEmptyName_NotOK() {
        Trainee trainee1 = new Trainee(
                null,
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 176",
                null
        );

        Trainee trainee2 = new Trainee(
            "Ivan",
            null,
            null,
            "123456",
            true,
            new Date(),
            "Holovna st., 176",
            null
    );

    Trainee trainee3 = new Trainee(
            null,
            null,
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

        //Should not create
        List<Trainee> trainees = traineeService.list();
        assertEquals(0, trainees.size());    

        //Check for empty names
        trainee1.setFirstName("");
        trainee2.setLastName("");
        trainee3.setFirstName("");
        trainee3.setLastName("");

        traineeService.create(trainee1);
        traineeService.create(trainee2);
        traineeService.create(trainee3);


        trainees = traineeService.list();
        assertEquals(0, trainees.size());    
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
    void updateTraineeNullName_NotOK() {
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
        
        traineeService.update(new Trainee(
            null,
            "Petrenko",
            "Maria.Petrenko",
            "123456",
            true,
            new Date(),
            "Holovna st., 176",
            null
        ));

        traineeService.update(new Trainee(
            "Maria",
            null,
            "Maria.Petrenko",
            "123456",
            true,
            new Date(),
            "Holovna st., 176",
            null
        ));

        traineeService.update(new Trainee(
            null,
            null,
            "Maria.Petrenko",
            "123456",
            true,
            new Date(),
            "Holovna st., 176",
            null
        ));

        //Should not be updated with invalid username.

        assertEquals(trainee, traineeService.select(trainee.getUsername()));    
    }

    @Test
    void updateTraineeEmptyName_NotOK() {
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
        
        traineeService.update(new Trainee(
            "",
            "Petrenko",
            "Maria.Petrenko",
            "123456",
            true,
            new Date(),
            "Holovna st., 176",
            null
        ));

        traineeService.update(new Trainee(
            "Maria",
            "",
            "Maria.Petrenko",
            "123456",
            true,
            new Date(),
            "Holovna st., 176",
            null
        ));

        traineeService.update(new Trainee(
            "",
            "",
            "Maria.Petrenko",
            "123456",
            true,
            new Date(),
            "Holovna st., 176",
            null
        ));

        //Should not be updated with invalid username.

        assertEquals(trainee, traineeService.select(trainee.getUsername()));    
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
    void deleteNotExisting_OK(){
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

        try {
            traineeService.delete(trainee);
        } catch (Exception e) {
            fail();
        }    
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

        for (int i = 0; i < listedTrainees.size(); i++) {
            assertEquals(listedTrainees.get(i).getFirstName(), "Maria");
            assertEquals(listedTrainees.get(i).getLastName(), "Petrenko");
            assertEquals(listedTrainees.get(i).isActive(), true);
            assertEquals(listedTrainees.get(i).getAddress(), "Holovna st., 176");
        }
    }

    @Test
    void listTraineesEmptyList_OK() {
        List<Trainee> trainees = traineeService.list();
        assertEquals(0, trainees.size());
    }
}
