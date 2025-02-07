package com.example.springcrm.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingTest {

    @Test
    void testEquals() {
        Date date = new Date();

        Training training1 = new Training(
                "trainee123",
                "trainer456",
                "John Doe",
                new TrainingType("Yoga"),
                date,
                Duration.ofHours(1)
        );

        Training training2 = new Training(
                "trainee123",
                "trainer456",
                "John Doe",
                new TrainingType("Yoga"),
                date,
                Duration.ofHours(1)
        );

        assertTrue(training1.equals(training2));
    }

    @Test
    void testClone() {
        Training training1 = new Training(
                "trainee123",
                "trainer456",
                "John Doe",
                new TrainingType("Yoga"),
                new Date(),
                Duration.ofHours(1)
        );

        Training training2 = training1.clone();

        assertFalse(training1 == training2);
    }
}
