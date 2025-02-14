package com.example.service;

import com.example.springcrm.dao.HibernateUtil;
import com.example.springcrm.dao.TraineeDao;
import com.example.springcrm.exception.DeletingNonexistentUserException;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.service.TraineeService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeServiceTest {
    private TraineeDao traineeDao = new TraineeDao(HibernateUtil.getSessionFactory());
    private TraineeService traineeService = new TraineeService(traineeDao);

    @AfterEach
    void cleanUpDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.createQuery("delete from Trainee").executeUpdate();
        session.createQuery("delete from Trainer").executeUpdate();
        session.createQuery("delete from User").executeUpdate();

        transaction.commit();
        session.close();
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
                "Holovna st., 1"
        );
        traineeService.create(trainee);
        //User gets userID in a process of creation.
        assertEquals(Optional.of(trainee), traineeService.select("Ivan.Ivanenko"));
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
                "Holovna st., 1");

        Trainee trainee2 = new Trainee(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 76"
        );

        Trainee trainee3 = new Trainee(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        );

        traineeService.create(trainee1.clone());
        traineeService.create(trainee2.clone());
        traineeService.create(trainee3.clone());


        List<Trainee> trainees = traineeService.list();
        assertEquals(3, trainees.size());
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
                "Holovna st., 176"
        );

        Trainee trainee2 = new Trainee(
                "Ivan",
                null,
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        );

        Trainee trainee3 = new Trainee(
                null,
                null,
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        );

        traineeService.create(trainee1);
        traineeService.create(trainee2);
        traineeService.create(trainee3);

        //Should not create
        List<Trainee> trainees = traineeService.list();
        assertEquals(0, trainees.size());


        trainee1.setUserId(null);
        trainee2.setUserId(null);
        trainee3.setUserId(null);

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
                "Holovna st., 176"
        );
        traineeService.create(trainee);

        trainee.setLastName("Ivanenko");
        traineeService.update(trainee);

        Trainee selected = traineeService.select(trainee.getUsername()).orElse(null);
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
                "Holovna st., 176"
        );

        traineeService.create(trainee);

        traineeService.update(new Trainee(
                null,
                "Petrenko",
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        ));

        traineeService.update(new Trainee(
                "Maria",
                null,
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        ));

        traineeService.update(new Trainee(
                null,
                null,
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        ));

        //Should not be updated with invalid username.

        assertEquals(Optional.of(trainee), traineeService.select(trainee.getUsername()));
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
                "Holovna st., 176"
        );

        traineeService.create(trainee);

        traineeService.update(new Trainee(
                "",
                "Petrenko",
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        ));

        traineeService.update(new Trainee(
                "Maria",
                "",
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        ));

        traineeService.update(new Trainee(
                "",
                "",
                "Maria.Petrenko",
                "123456",
                true,
                new Date(),
                "Holovna st., 176"
        ));

        //Should not be updated with invalid username.

        assertEquals(Optional.of(trainee), traineeService.select(trainee.getUsername()));
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
                "Holovna st., 176"
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
                "Holovna st., 176"
        );

        //I thought that simply ignoring deleting of nonexisting users isn't enough
        assertThrows(DeletingNonexistentUserException.class,
                () -> traineeDao.delete(trainee));

        //But service should handle it
        try {
            traineeService.delete(trainee);
        } catch (Exception e) {
            fail("Service had thrown an exception");
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
                "Holovna st., 176"
        );

        List<Trainee> trainees = new ArrayList<>();

        for (int i = 0; i < numberOfTrainees; i++) {
            trainee.setUserId(null);
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


    @Test
    void authenticationRequired_OK() {
        Trainee trainee = new Trainee(
                "Maria",
                "Petrenko",
                null,
                null,
                true,
                new Date(),
                "Holovna st., 176"
        );
        traineeService.create(trainee);
        Trainee snapshot = trainee.clone();
        String password = trainee.getPassword();

        trainee.setAddress("Something");
        trainee.setPassword("wrongPassword"); //Not similar to autogenerated, should fail to autheticate
        traineeService.update(trainee);

        //Assert that nothing in DB was changed
        Optional<Trainee> selected = traineeService.select(trainee.getUsername());
        assertEquals(snapshot.getAddress(), selected.get().getAddress());
        assertEquals(password, selected.get().getPassword());
        assertEquals(snapshot.getPassword(), selected.get().getPassword());
        assertEquals(snapshot, selected.get());//Not changed


        trainee.setPassword("wrongPassword"); //Not similar to autogenerated, should fail to authenticate
        traineeService.delete(trainee);

        //Assert that nothing in DB was changed
        selected = traineeService.select(trainee.getUsername());
        assertEquals(snapshot.getAddress(), selected.get().getAddress());
        assertEquals(snapshot.getPassword(), selected.get().getPassword());
        assertEquals(snapshot, selected.get());//Not changed
    }

    @Test
    void activateTrainee_notIdempotent() {
        Trainee trainee = new Trainee(
                "Maria",
                "Petrenko",
                null,
                null,
                false,
                new Date(),
                "Holovna st., 176"
        );
        traineeService.create(trainee);
        assertTrue(traineeService
                .activateAccount(trainee.getUsername())
                .get()
                .isActive());

        //Should return empty optional if can't update account.
        assertEquals(Optional.empty(),
                traineeService
                        .activateAccount(trainee.getUsername()));
    }

    @Test
    void deactivateTrainee_notIdempotent() {
        Trainee trainee = new Trainee(
                "Maria",
                "Petrenko",
                null,
                null,
                true,
                new Date(),
                "Holovna st., 176"
        );
        traineeService.create(trainee);
        assertFalse(traineeService
                .deactivateAccount(trainee.getUsername())
                .get()
                .isActive());

        //Should return empty optional if can't update account.
        assertEquals(Optional.empty(),
                traineeService
                        .deactivateAccount(trainee.getUsername()));
    }

}
