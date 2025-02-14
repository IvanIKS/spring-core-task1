package com.example.springcrm.dao;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<? extends User> getByUsername(String username);

    List<? extends User> getAllByUsername(String usernameSubstring);

    default void validateUserForDatabase(User user) throws IllegalArgumentException {
        //Validating on server side without waiting for errors sent from database.
        if (user.getFirstName() == null || user.getLastName() == null
                || user.getFirstName().isBlank() || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Invalid user: " + user.toString());
        }
    }

    default boolean existingUserChangedName(User oldVersion, User newVersion) {
        if ((oldVersion == null || newVersion == null))
            return false;
        else {
            return (!oldVersion.getFirstName().equals(newVersion.getFirstName()))
                    || (!(oldVersion.getLastName().equals(newVersion.getLastName())));
        }
    }

    default boolean needsNameUpdate(User oldVersion, User newVersion) {
        return oldVersion != null
                && existingUserChangedName(oldVersion, newVersion)
                && oldVersion.getUsername().equals(newVersion.getUsername());
    }
}
