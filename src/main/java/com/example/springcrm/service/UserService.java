package com.example.springcrm.service;

import com.example.springcrm.dao.UserDao;
import com.example.springcrm.exception.UnauthorisedException;
import com.example.springcrm.model.User;


import java.util.Optional;
import java.util.Random;


public abstract class UserService {
    private static final String RANDOM_PASSWORD_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    protected abstract static class AuthenticationService {
        protected UserDao userDao;

        protected AuthenticationService(UserDao userDao) {
            this.userDao = userDao;
        }

        protected boolean authenticate(User user) throws UnauthorisedException {
            Optional<? extends User> existingAccount = userDao.getByUsername(user.getUsername());
            if (existingAccount.isEmpty()) {
                return false;
            }
            if (existingAccount.get().getPassword().equals(user.getPassword())) {
                return true;
            } else {
                throw new UnauthorisedException("Password does not match!");
            }
        }

        protected boolean authenticate(User user, String password) throws UnauthorisedException {
            Optional<? extends User> existingAccount = userDao.getByUsername(user.getUsername());
            if (existingAccount.isEmpty()) {
                return false;
            }
            if (existingAccount.get().getPassword().equals(password)) {
                return true;
            } else {
                throw new UnauthorisedException("Password does not match!");
            }
        }
    }

    public abstract Optional<? extends User> activateAccount(String username);

    public abstract Optional<? extends User> deactivateAccount(String username);

    public abstract Optional<? extends User> changePassword(String username, String password, String newPassword);

    public String generateRandomPassword(int length) {
        Random random = new Random();
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            int randomPosition = random.nextInt(RANDOM_PASSWORD_CHARACTERS.length());
            password[i] = RANDOM_PASSWORD_CHARACTERS.charAt(randomPosition);
        }
        return new String(password);
    }

    void validateName(User user) throws IllegalArgumentException {
        if (user != null) {
            if (user.getFirstName() == null
                    || user.getLastName() == null
                    || user.getFirstName().isEmpty()
                    || user.getLastName().isEmpty()) {
                throw new IllegalArgumentException(String.format(
                        "Invalid or empty name or username. Name: %s; Username: %s",
                        user.getFirstName(),
                        user.getLastName()
                ));
            }
        }
    }

    public String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    public static String nextValidUsername(String lastUsedUsername) {
        char[] chars = lastUsedUsername.toCharArray();
        int lastLetterIndex = chars.length - 1;

        //To determine where name ends and index starts
        while ((chars[lastLetterIndex] >= '0'
                && chars[lastLetterIndex] <= '9')
                && lastLetterIndex > 0) {
            lastLetterIndex--;
        }

        String name;
        int nextIndex;

        if (lastLetterIndex == lastUsedUsername.length() - 1) {
            //If last used username has no index in the end:
            name = lastUsedUsername;
            nextIndex = 1;
        } else {
            int firstNumberIndex = lastLetterIndex + 1;
            String lastIndex = lastUsedUsername.substring(firstNumberIndex);

            name = lastUsedUsername.substring(0, firstNumberIndex);
            nextIndex = Integer.parseInt(lastIndex) + 1;
        }

        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append(nextIndex);

        return result.toString();
    }


}