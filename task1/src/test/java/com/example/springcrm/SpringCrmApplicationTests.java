package com.example.springcrm;

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