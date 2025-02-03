package com.example.springcrm.service;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UserService {
    private static final String RANDOM_PASSWORD_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    String generateRandomPassword(int length) {
        Random random = new Random();
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            int randomPosition = random.nextInt(RANDOM_PASSWORD_CHARACTERS.length());
            password[i] = RANDOM_PASSWORD_CHARACTERS.charAt(randomPosition);
        }
        return new String(password);
    }

    String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    String handleUsernameOverlap(String username, String lastExistingUsername) {
        if (username.equals(lastExistingUsername)) {
            return username + "1";
        }
        Pattern pattern = Pattern.compile("\\d+$");
        Matcher matcher = pattern.matcher(lastExistingUsername);

        if (matcher.find()) {
            int lastIndex = Integer.parseInt(matcher.group());
            lastIndex++;
            return username + (lastIndex);
        } else {
            throw new RuntimeException("Illegal state of program: " + username + " caused a fail in logic.");
        }

    }

}
