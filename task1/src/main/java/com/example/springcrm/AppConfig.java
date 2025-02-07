package com.example.springcrm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.Training;


@Configuration
@ComponentScan(basePackages = "com.example.springcrm")
public class AppConfig {

    @Bean
    public Map<String, Trainee> trainees() {
        return new HashMap<>();  
    }

    @Bean
    public Map<String, Trainer> trainers() {
        return new HashMap<>();  
    }

    @Bean
    public Map<String, Training> trainings() {
        return new HashMap<>();  
    }

}