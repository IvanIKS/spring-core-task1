package com.example;
import com.example.springcrm.AppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = AppConfig.class)
public class SpringCrmApplicationTests {

    @Test
    void contextLoads() {

    }

    public static void assertThatListsAreEqual(List first, List second) {
        assertTrue(first.size() == second.size()
                && first.containsAll(second)
                && second.containsAll(first));
    }
}