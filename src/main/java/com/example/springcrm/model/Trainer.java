package com.example.springcrm.model;

import jakarta.persistence.Column;

import java.util.HashSet;
import java.util.Objects;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
public class Trainer extends User implements Cloneable {
    @Column(nullable = false)
    private String specialization;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.MERGE, orphanRemoval = false)
    private List<Training> trainings;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "trainees_trainers",
            joinColumns = @JoinColumn(name = "trainer_userId"),
            inverseJoinColumns = @JoinColumn(name = "trainee_userId")
    )
    private Set<Trainee> trainees = new HashSet<>();

    public Set<Trainee> getTrainees() {
        return trainees;
    }

    public Trainer() {
    }

    public Trainer(String firstName,
                   String lastName,
                   String username,
                   String password,
                   boolean isActive,
                   String specialization) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && this.getSpecialization().equals(((Trainer) obj).getSpecialization());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getFirstName(),
                this.getLastName(),
                this.getLastName(),
                this.getUsername(),
                this.isActive()
        );
    }

    @Override
    public Trainer clone() {
        try {
            return (Trainer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
