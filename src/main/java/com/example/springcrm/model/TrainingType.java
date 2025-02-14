package com.example.springcrm.model;

import jakarta.persistence.*;

@Entity
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true/*, updatable = false*/)
    private String trainingTypeName;


    public TrainingType() {}

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingType that = (TrainingType) o;
        return trainingTypeName.equals(that.trainingTypeName);
    }

    @Override
    public int hashCode() {
        return trainingTypeName.hashCode();
    }

    public Integer getId() {
        return id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }
}
