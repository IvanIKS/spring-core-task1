package com.example.springcrm;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;

import com.example.springcrm.model.Training;
import com.example.springcrm.model.TrainingType;
import com.example.springcrm.service.TraineeService;
import com.example.springcrm.service.TrainerService;
import com.example.springcrm.service.TrainingService;
import com.example.springcrm.dao.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


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

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            try {
                Trainer trainer = new Trainer("Alice", "Smith", "alice.trainer", "securepass", true, "Strength Training");

                Trainee trainee = new Trainee("John", "Doe", "john.doe", "password", true, new Date(), "123 Street");


                try {
                    TrainingType trainingType = new TrainingType("cardio");
                    session.persist(trainingType);


                    Training training1 = new Training(trainee, trainer, null, new Date(), Duration.ofHours(1));
                    Training training2 = new Training(trainee, trainer, null, new Date(), Duration.ofHours(2));

                    trainer.setTrainings(Arrays.asList(training1, training2));

                    trainee.setTrainings(Arrays.asList(training1, training2));

                    session.persist(trainer);

                    session.persist(trainee);
                    session.persist(training1);
                    session.persist(training2);


                    transaction.commit();

                    //TRAINING TYPE DOES NOT UPDATE - IT SHOULDN'T UPDATE AS PER REQUIREMENTS
                    trainingType = new TrainingType("something");
                    //trainingType.setTrainingTypeName("something");
                    session.persist(trainingType);
                    transaction.commit();

                } catch (Exception e) {
                    System.out.println("Everything OK, persisting training type shouldnt be allowed");
                }
                System.out.println("Trainer, Trainee, and Trainings saved successfully!");
                List<Trainer> trainers = session.createQuery("FROM Trainer", Trainer.class).getResultList();
                List<Trainee> trainees = session.createQuery("FROM Trainee", Trainee.class).getResultList();
                List<Training> trainings = session.createQuery("FROM Training", Training.class).getResultList();

                System.out.println("Trainers:");
                for (Trainer t : trainers) {
                    System.out.println(t.getFirstName() + " " + t.getLastName());
                }
                System.out.println("Trainees:");
                for (Trainee t : trainees) {
                    System.out.println(t.getFirstName() + " " + t.getLastName());
                }
                System.out.println("Trainings:");
                for (Training t : trainings) {
                    System.out.println(t.getTrainerName() + " " + t.getTrainingType() + " " + t.getTrainee().getFirstName() + " " + t.getTrainee().getLastName() + " " + t.getTrainer().getLastName());
                }

                List<TrainingType> trainingTypes = session.createQuery("FROM TrainingType", TrainingType.class).getResultList();
                System.out.println("Training types:");
                for (TrainingType t : trainingTypes) {
                    System.out.println(t.getTrainingTypeName());
                }


            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
            } finally {
                session.close();
                HibernateUtil.shutdown();
            }
        }
    }

}
