package com.example.springcrm.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Entity
public class Training implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne(cascade =  CascadeType.MERGE)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(nullable = false)
    private String trainingName;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "training_type_id", nullable = true)
    private TrainingType trainingType;

    @Column
    private Date trainingDate;

    @Column
    private Duration trainingDuration;


    public Training() {}

    public Training(Trainee trainee,
                    Trainer trainer,
                    String trainingName,
                    TrainingType trainingType,
                    Date trainingDate,
                    Duration trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Duration getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Duration trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return this.trainee.equals(training.trainee)
                && this.trainer.equals(training.trainer)
                && this.trainingName.equals(training.trainingName)
                && this.trainingType.equals(training.trainingType)
                && this.trainingDate.equals(training.trainingDate)
                && this.trainingDuration.equals(training.trainingDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                trainee,
                trainer,
                trainingName,
                trainingType,
                trainingDate,
                trainingDuration
        );
    }

    @Override
    public Training clone() {
        try {
            return (Training) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
