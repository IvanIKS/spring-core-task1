package com.example.springcrm.dao;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.User;

public abstract class UserUtil {
    static void validateUserForDatabase(User user) throws IllegalArgumentException {
        //Validating on server side without waiting for errors sent from database.
        if (user.getFirstName() == null || user.getLastName() == null
                || user.getFirstName().isBlank() || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Invalid user: " + user.toString());
        }
    }

    static boolean existingUserChangedName(User oldVersion, User newVersion) {
        if ((oldVersion == null || newVersion == null))
            return false;
        else {
            return (!oldVersion.getFirstName().equals(newVersion.getFirstName()))
                    || (!(oldVersion.getLastName().equals(newVersion.getLastName())));
        }
    }

    static boolean needsNameUpdate(User oldVersion, User newVersion) {
        return oldVersion != null
                && existingUserChangedName(oldVersion, newVersion)
                && oldVersion.getUsername().equals(newVersion.getUsername());
    }
}
