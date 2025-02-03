package com.example.springcrm.model;

import java.util.Objects;

public class Trainer extends User implements Cloneable {
    private String specialization;
    private String userId;

    public Trainer() {
    }

    public Trainer(String firstName,
                   String lastName,
                   String username,
                   String password,
                   boolean isActive,
                   String specialization,
                   String userId) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
        this.userId = userId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && this.getSpecialization().equals(((Trainer) obj).getSpecialization())
                && this.getUserId().equals(((Trainer) obj).getUserId());
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
    public Trainer clone()  {
        try {
            return (Trainer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
