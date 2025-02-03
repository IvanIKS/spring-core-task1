package com.example.springcrm;

import com.example.springcrm.service.TraineeService;
import com.example.springcrm.service.TrainerService;
import com.example.springcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringCrmApplication {
    private static final Logger logger = LoggerFactory.getLogger(SpringCrmApplication.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public SpringCrmApplication(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;

        logger.info("SpringCrmApplication started");
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

            TraineeService traineeService = context.getBean(TraineeService.class);
            TrainerService trainerService = context.getBean(TrainerService.class);
            TrainingService trainingService = context.getBean(TrainingService.class);

            new SpringCrmApplication(traineeService, trainerService, trainingService);
        }
    }
}
