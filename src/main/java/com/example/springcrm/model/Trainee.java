package com.example.springcrm.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class Trainee extends User implements Cloneable {

    @Column(nullable = true)
    private Date dateOfBirth;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    @ManyToMany(mappedBy = "trainees")
    private Set<Trainer> trainers = new HashSet<>();

    public Trainee() {}

    public Trainee(String firstName,
                   String lastName,
                   String username,
                   String password,
                   boolean isActive,
                   Date dateOfBirth,
                   String address) {
        super(firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Trainer> getTrainers() {
        return this.trainers;
    }

    public void addTrainer(Trainer trainer) {
        this.trainers.add(trainer);
    }

    public void addTraining(Training training) {
        this.trainings.add(training);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainee trainee = (Trainee) o;
        return super.equals(o)
                && this.getDateOfBirth().equals(trainee.getDateOfBirth())
                && this.getAddress().equals(trainee.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getFirstName(),
                this.getLastName(),
                this.getUsername(),
                this.isActive()
        );
    }

    @Override
    public Trainee clone() {
        try {
            return (Trainee) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


}
