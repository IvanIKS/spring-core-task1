package com.example.springcrm.service;

import com.example.springcrm.model.User;

import java.util.Random;


public abstract class UserService {
    private static final String RANDOM_PASSWORD_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

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