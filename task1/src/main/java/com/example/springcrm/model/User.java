package com.example.springcrm.model;

import java.util.Objects;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;

    public User() {
    }

    public User(String firstName,
                String lastName,
                String username,
                String password,
                boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public static boolean userNameHasChanged(User oldVersion, User newVersion) {
        return (!oldVersion.getFirstName().equals(newVersion.getFirstName()))
                || (!oldVersion.getLastName().equals(newVersion.getLastName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName)
                && Objects.equals(username, user.username)
                && Objects.equals(password, user.password)
                && isActive == user.isActive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, username);
    }
}
