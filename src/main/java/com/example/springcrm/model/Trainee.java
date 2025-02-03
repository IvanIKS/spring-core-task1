package com.example.springcrm.model;

import java.util.Date;
import java.util.Objects;

public class Trainee extends User implements Cloneable {
    private Date dateOfBirth;
    private String address;
    private String userId;

    public Trainee() {}

    public Trainee(String firstName,
                   String lastName,
                   String username,
                   String password,
                   boolean isActive,
                   Date dateOfBirth,
                   String address,
                   String userId) {
        super(firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainee trainee = (Trainee) o;
        return super.equals(o)
                && this.getDateOfBirth().equals(trainee.getDateOfBirth())
                && this.getAddress().equals(trainee.getAddress())
                && this.getUserId().equals(trainee.getUserId());
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
