package com.example.service;

import com.example.springcrm.model.User;
import com.example.springcrm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


//Tests for functions defined in abstract UserService class.
class UserServiceTest {
    UserService userService;

    @BeforeEach
    void setUp() {

        // Creating anonymous class that is not TrainerService nor TraineeService.
        userService = new UserService() {
            @Override
            public Optional<? extends User> activateAccount(String username) {
                return Optional.empty();
            }

            @Override
            public Optional<? extends User> deactivateAccount(String username) {
                return Optional.empty();
            }

            @Override
            public Optional<? extends User> changePassword(String username, String password, String newPassword) {
                return Optional.empty();
            }
        };
    }

    @Test
    void passwordGenerationTests() {
        int numberOfPasswordGenerationTests = 100;
        int expectedPasswordLength = 10;

        String permittedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < numberOfPasswordGenerationTests; i++) {
            String generatedPassword = userService.generateRandomPassword(expectedPasswordLength);
            assertEquals(expectedPasswordLength, generatedPassword.length());

            char[] chars = generatedPassword.toCharArray();
            for (char aChar : chars) {
                // Check that generated password doesn't contain characters other than permitted.
                assert (permittedCharacters.indexOf(aChar) != -1);
            }
        }
    }

    @Test
    void usernameGenerationTests() {
        Map<String[], String> expectedResults = new HashMap<>();
        expectedResults.put(new String[]{"Petro", "Ivanenko"}, "Petro.Ivanenko");
        expectedResults.put(new String[]{"Maria", "Ivanenko"}, "Maria.Ivanenko");
        expectedResults.put(new String[]{"Dar'ya", "P'yatakova"}, "Dar'ya.P'yatakova"); //What shall we do with apostrophes?


        for (String[] nameAndSurname : expectedResults.keySet()) {
            String expected = expectedResults.get(nameAndSurname);
            String actual = userService.generateUsername(nameAndSurname[0], nameAndSurname[1]);
            assertEquals(expected, actual);
        }
    }

    @Test
    void overlapHandlingTests() {
        Map<String, String> expectedResults = new HashMap<>();
        expectedResults.put( "Petro.Ivanenko", "Petro.Ivanenko1");
        expectedResults.put( "Petro.Ivanenko1", "Petro.Ivanenko2");
        expectedResults.put( "Petro.Ivanenko15", "Petro.Ivanenko16");
        expectedResults.put("Petro.Ivanenko1075", "Petro.Ivanenko1076");

        expectedResults.put("John1", "John2");
        expectedResults.put("Smith1", "Smith2");

        for (String username : expectedResults.keySet()) {
            String expected = expectedResults.get(username);
            String actual = userService.nextValidUsername(username);

            assertEquals(expected, actual);
        }
    }
}