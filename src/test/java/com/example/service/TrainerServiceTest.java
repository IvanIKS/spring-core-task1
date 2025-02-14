package com.example.service;

import com.example.springcrm.dao.HibernateUtil;
import com.example.springcrm.dao.TrainerDao;

import com.example.springcrm.model.Trainer;
import com.example.springcrm.service.TrainerService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.SpringCrmApplicationTests.assertThatListsAreEqual;
import static org.junit.jupiter.api.Assertions.*;


public class TrainerServiceTest {

    private TrainerDao trainerDao = new TrainerDao(HibernateUtil.getSessionFactory());
    private TrainerService trainerService = new TrainerService(trainerDao);

    @AfterEach
    void cleanUpDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.createQuery("delete from Trainer").executeUpdate();
        session.createQuery("delete from Trainee").executeUpdate();
        session.createQuery("delete from User").executeUpdate();

        transaction.commit();
        session.close();
    }

    @Test
    void createTrainer_OK() {
        Trainer trainer = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
        );
        trainerService.create(trainer);
        //User gets userID in a process of creation.
        assertEquals(Optional.of(trainer), trainerService.select(trainer.getUsername()));
    }


    @Test
    void createTrainerUsernameOverlap_OK() {
        Trainer trainer1 = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
        );

        Trainer trainer2 = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
        );

        Trainer trainer3 = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
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
                "Boxing"
        );

        Trainer trainer2 = new Trainer(
                "Ivan",
                null,
                null,
                "123456",
                true,
                "Boxing"
        );

        Trainer trainer3 = new Trainer(
                null,
                null,
                null,
                "123456",
                true,
                "Boxing"
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
                "Boxing"
        );
        trainerService.create(trainer);

        trainer.setFirstName("Vitaly");
        trainerService.update(trainer);

        Trainer selected = trainerService.select(trainer.getUsername()).orElse(null);
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
                "Boxing"
        );

        Trainer trainer2 = new Trainer(
                "Vitaly",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
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
                "Boxing"
        );


        trainerService.create(trainer1);

        trainerService.update(new Trainer(
                null,
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
        ));

        trainerService.update(new Trainer(
                "Ivan",
                null,
                null,
                "123456",
                true,
                "Boxing"
        ));

        trainerService.update(new Trainer(
                null,
                null,
                null,
                "123456",
                true,
                "Boxing"
        ));

        //Should not be updated with invalid username.

        assertEquals(Optional.of(trainer1), trainerService.select(trainer1.getUsername()));
    }

    @Test
    void updateTrainerEmptyName_NotOK() {
        Trainer trainer1 = new Trainer(
                "Ivan",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
        );


        trainerService.create(trainer1);

        trainerService.update(new Trainer(
                "",
                "Ivanenko",
                null,
                "123456",
                true,
                "Boxing"
        ));

        trainerService.update(new Trainer(
                "Ivan",
                "",
                null,
                "123456",
                true,
                "Boxing"
        ));

        trainerService.update(new Trainer(
                "",
                "",
                null,
                "123456",
                true,
                "Boxing"
        ));

        //Should not be updated with invalid username.

        assertEquals(Optional.of(trainer1), trainerService.select(trainer1.getUsername()));
    }

    @Test
    void listTrainersEmptyList_OK() {
        List<Trainer> trainers = trainerService.list();
        assertEquals(0, trainers.size());
    }

    @Test
    void authenticationRequired_OK() {
        Trainer trainer = new Trainer(
                "Maria",
                "Petrenko",
                null,
                null,
                true,
                "Boxing"
        );
        trainerService.create(trainer);
        Trainer snapshot = trainer.clone();
        String password = trainer.getPassword();

        trainer.setSpecialization("Something");
        trainer.setPassword("wrongPassword"); //Not similar to autogenerated, should fail to autheticate
        trainerService.update(trainer);

        //Assert that nothing in DB was changed
        Optional<Trainer> selected = trainerService.select(trainer.getUsername());
        assertEquals(snapshot.getSpecialization(), selected.get().getSpecialization());
        assertEquals(password, selected.get().getPassword());
        assertEquals(snapshot.getPassword(), selected.get().getPassword());
        assertEquals(snapshot, selected.get());//Not changed
    }

    @Test
    void activateTrainer_notIdempotent() {
        Trainer trainer = new Trainer(
                "Maria",
                "Petrenko",
                null,
                null,
                false,
                "Crossfit");

        trainerService.create(trainer);
        assertTrue(trainerService
                .activateAccount(trainer.getUsername())
                .get()
                .isActive());

        //Should return empty optional if can't update account.
        assertEquals(Optional.empty(),
                trainerService
                        .activateAccount(trainer.getUsername()));
    }

    @Test
    void deactivateTrainer_notIdempotent() {
        Trainer trainer = new Trainer(
                "Maria",
                "Petrenko",
                null,
                null,
                true,
                "Crossfit");

        trainerService.create(trainer);
        assertFalse(trainerService
                .deactivateAccount(trainer.getUsername())
                .get()
                .isActive());

        //Should return empty optional if can't update account.
        assertEquals(Optional.empty(),
                trainerService
                        .deactivateAccount(trainer.getUsername()));
    }

    @Test
    void changePassword_OK() {
        Trainer trainer = new Trainer(
                "Dmytro",
                "Hunko",
                "Dmytro.Hunko",
                null,
                true,
                "Bodybuilding"
        );

        trainerService.create(trainer);

        String oldPassword = trainer.getPassword();

        Optional<Trainer> updatedTrainerOpt = trainerService.changePassword(
                "Dmytro.Hunko",
                oldPassword,
                "newPassword");

        assertTrue(updatedTrainerOpt.isPresent());

        Trainer updatedTrainer = updatedTrainerOpt.get();

        assertEquals("newPassword", updatedTrainer.getPassword());
        assertEquals("Dmytro.Hunko", updatedTrainer.getUsername());

        //Make sure change persists to DB.
        Trainer selectedTrainer = trainerService.select(trainer.getUsername()).get();

        assertEquals("newPassword", selectedTrainer.getPassword());
        assertEquals("Dmytro.Hunko", selectedTrainer.getUsername());
    }

    @Test
    void changePasswordToEmpty_NotOK() {
        Trainer trainer = new Trainer(
                "Dmytro",
                "Hunko",
                "Dmytro.Hunko",
                null,
                true,
                "Bodybuilding"
        );

        trainerService.create(trainer);

        String oldPassword = trainer.getPassword();

        Optional<Trainer> updatedTraineeOpt = trainerService.changePassword(
                "Dmytro.Hunko",
                oldPassword,
                "");

        assertEquals(Optional.empty(), updatedTraineeOpt);

        //Make sure password wasn't changed.
        Trainer selectedTrainer = trainerService.select(trainer.getUsername()).get();

        assertEquals(oldPassword, selectedTrainer.getPassword());
        assertEquals("Dmytro.Hunko", selectedTrainer.getUsername());

        updatedTraineeOpt = trainerService.changePassword(
                "Dmytro.Hunko",
                oldPassword,
                null);

        assertEquals(Optional.empty(), updatedTraineeOpt);

        //Make sure password wasn't changed.
        selectedTrainer = trainerService.select(trainer.getUsername()).get();

        assertEquals(oldPassword, selectedTrainer.getPassword());
        assertEquals("Dmytro.Hunko", selectedTrainer.getUsername());
    }

}
