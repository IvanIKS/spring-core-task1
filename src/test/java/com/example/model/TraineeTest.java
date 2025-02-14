package com.example.model;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraineeTest {
    @Test
    public void addSameTrainer_OK() {
        Trainee trainee = new Trainee();

        Trainer trainer = new Trainer();

        //Should not add the same trainee twice
        trainee.addTrainer(trainer);
        trainee.addTrainer(trainer);

        assertEquals(trainee.getTrainers().size(), 1);
    }
}
