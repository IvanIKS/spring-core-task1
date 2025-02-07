package com.example.springcrm.model;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

public class Training implements Cloneable {
    private String traineeId;
    private String trainerId;
    private String trainerName;
    private TrainingType trainingType;
    private Date trainingDate;
    private Duration trainingDuration;

    public Training() {}

    public Training(String traineeId,
                    String trainerId,
                    String trainerName,
                    TrainingType trainingType,
                    Date trainingDate,
                    Duration trainingDuration) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainerName = trainerName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    //This is a combined id used to store in DB.
    public String getId() {
        return trainingDate.toString() + " " + traineeId + " " + trainerId;
    }

    public String getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(String traineeId) {
        this.traineeId = traineeId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
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
        return this.traineeId.equals(training.traineeId)
                && this.trainerId.equals(training.trainerId)
                && this.trainerName.equals(training.trainerName)
                && this.trainingType.equals(training.trainingType)
                && this.trainingDate.equals(training.trainingDate)
                && this.trainingDuration.equals(training.trainingDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                traineeId,
                trainerId,
                trainerName,
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
}
