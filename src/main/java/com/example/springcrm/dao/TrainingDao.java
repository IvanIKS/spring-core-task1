package com.example.springcrm.dao;

import com.example.springcrm.exception.DeletingNonexistentUserException;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.Training;
import com.example.springcrm.model.TrainingType;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

import static com.example.springcrm.dao.UserUtil.needsNameUpdate;

@Repository
public class TrainingDao implements Dao<Training> {
    private final SessionFactory factory;

    public TrainingDao(@Qualifier("sessionFactory") SessionFactory factory) {
        this.factory = factory;
    }


    @Override
    public Optional<Training> get(String id) {
        Session session = null;
        Training result;
        try {
            session = factory.openSession();

            Query<Training> query = session.createQuery(
                    "FROM Training t WHERE t.id LIKE :id", Training.class
            );
            query.setParameter("id", id);

            result = query.getResultList().get(0);
        } catch (IndexOutOfBoundsException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get trainer. " + ex.getClass() + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return Optional.of(result);
    }

    @Override
    public void create(Training training) throws IllegalArgumentException {
        Session session = null;
        Transaction transaction = null;
        try {

            session = factory.openSession();
            transaction = session.beginTransaction();

            Trainee attachedTrainee = session.get(Trainee.class, training.getTrainee().getUserId());
            Trainer attachedTrainer = session.get(Trainer.class, training.getTrainer().getUserId());

            TrainingType attachedTrainingType = session.get(TrainingType.class, training.getTrainingType().getId());

            attachedTrainer.addTraining(training);
            attachedTrainee.addTraining(training);

            training.setTrainee(attachedTrainee);
            training.setTrainer(attachedTrainer);
            training.setTrainingType(attachedTrainingType);

            session.persist(training);
            transaction.commit();

        } catch (EntityExistsException | ConstraintViolationException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new UserAlreadyExistsException("Failed to create training: " + training.toString()
                    + " reason: " + ex.getMessage());
        } catch (PropertyValueException ex) {
            throw new IllegalArgumentException("Failed to create training: " + training.toString()
                    + " reason: " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(Training training) {

        Session session = null;
        Transaction transaction = null;

        try {

            session = factory.openSession();
            transaction = session.beginTransaction();

            Training oldTraining = get(training.getId()).orElse(null);

            // Update fields
            oldTraining = training.clone();

            session.merge(oldTraining);

            transaction.commit();
        } catch (ConstraintViolationException ex) {
            throw new UserAlreadyExistsException("Failed to update training to: " + training.toString());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Training> getAll() {
        Session session = null;
        List<Training> results;
        try {
            session = factory.openSession();
            results = session.createQuery("SELECT a FROM Training a", Training.class).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get all Trainings. " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return results;
    }

    @Override
    public void delete(Training training) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = factory.openSession();
            transaction = session.beginTransaction();

            Training existingTrainer = get(training.getId()).orElse(null);

            session.remove(existingTrainer);

            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

