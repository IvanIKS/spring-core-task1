package com.example.model;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.Training;
import com.example.springcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TrainingTest {
    Trainee trainee;
    Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee(
                "Olexandr",
                "Serhiienko",
                null,
                "123456",
                true,
                new Date(),
                "Holovna st., 1"
        );

        trainer = new Trainer(
                "Max",
                "Payne",
                null,
                "123456",
                true,
                "Boxing"
        );
    }

    @Test
    void testEquals() {

        Training training1 = new Training(
                trainee,
                trainer,
                "Yoga",
                new TrainingType("Yoga"),
                new Date(),
                Duration.ofHours(1)
        );

        Training training2 = new Training(
                trainee,
                trainer,
                "Yoga",
                new TrainingType("Yoga"),
                new Date(),
                Duration.ofHours(1)
        );

        assertTrue(training1.equals(training1));
    }

    @Test
    void testClone() {
        Training training1 = new Training(
                trainee,
                trainer,
                "Yoga",
                new TrainingType("Yoga"),
                new Date(),
                Duration.ofHours(1)
        );

        Training training2 = training1.clone();

        assertFalse(training1 == training2);
    }
}
