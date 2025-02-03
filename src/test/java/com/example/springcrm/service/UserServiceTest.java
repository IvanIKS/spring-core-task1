package com.example.springcrm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


//Tests for functions defined in abstract UserService class.

class UserServiceTest {
    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService() {
            @Override
            String generateRandomPassword(int length) {
                return super.generateRandomPassword(length);
            }

            @Override
            String generateUsername(String firstName, String lastName) {
                return super.generateUsername(firstName, lastName);
            }

            @Override
            String handleUsernameOverlap(String username, String lastExistingUsername) {
                return super.handleUsernameOverlap(username, lastExistingUsername);
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
                // Check if generated password consists only of permitted characters.
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
    void usernameOverlapTests() {
        Map<String[], String> expectedResults = new HashMap<>();
        expectedResults.put(new String[]{"Petro.Ivanenko", "Petro.Ivanenko"}, "Petro.Ivanenko1");
        expectedResults.put(new String[]{"Petro.Ivanenko", "Petro.Ivanenko1"}, "Petro.Ivanenko2");
        expectedResults.put(new String[]{"Petro.Ivanenko", "Petro.Ivanenko15"}, "Petro.Ivanenko16");
        expectedResults.put(new String[]{"Petro.Ivanenko", "Petro.Ivanenko1075"}, "Petro.Ivanenko1076");


        for (String[] usernames : expectedResults.keySet()) {
            String expected = expectedResults.get(usernames);
            String actual = userService.handleUsernameOverlap(usernames[0], usernames[1]);
            assertEquals(expected, actual);
        }
    }
}