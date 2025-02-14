package com.example.model;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerTest {

    @Test
    public void addSameTrainee_OK() {
        Trainer trainer = new Trainer();

        Trainee trainee = new Trainee();

        //Should not add the same trainee twice
        trainer.addTrainee(trainee);
        trainer.addTrainee(trainee);

        assertEquals(trainer.getTrainees().size(), 1);
        assertEquals(trainer.getTrainees().iterator().next(), trainee);
    }
}
