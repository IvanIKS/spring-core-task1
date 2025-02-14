package com.example.springcrm.dao;

import com.example.springcrm.exception.DeletingNonexistentUserException;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainer;
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


@Repository
public class TrainerDao implements Dao<Trainer>, UserDao {
    private final SessionFactory factory;

    public TrainerDao(@Qualifier("sessionFactory") SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Trainer> getByUsername(String username) {
        Session session = null;
        Trainer result;
        try {
            session = factory.openSession();

            Query<Trainer> query = session.createQuery(
                    "FROM Trainer t WHERE t.username = :name", Trainer.class
            );
            query.setParameter("name", username);

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
    public Optional<Trainer> get(String id) {
        Session session = null;
        Trainer result;
        try {
            session = factory.openSession();

            Query<Trainer> query = session.createQuery(
                    "FROM Trainer t WHERE t.userId LIKE :id", Trainer.class
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
    public void create(Trainer trainer) throws UserAlreadyExistsException, IllegalArgumentException {
        Session session = null;
        Transaction transaction = null;
        try {
            validateUserForDatabase(trainer);

            session = factory.openSession();
            transaction = session.beginTransaction();

            session.persist(trainer);
            transaction.commit();

        } catch (EntityExistsException | ConstraintViolationException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new UserAlreadyExistsException("Failed to create trainer: " + trainer.toString()
                    + " reason: " + ex.getMessage());
        } catch (PropertyValueException ex) {
            throw new IllegalArgumentException("Failed to create trainer: " + trainer.toString()
                    + " reason: " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(Trainer trainer) throws OutdatedUsernameException {

        Session session = null;
        Transaction transaction = null;

        try {
            validateUserForDatabase(trainer);

            session = factory.openSession();
            transaction = session.beginTransaction();

            Trainer oldTrainer = get(trainer.getUserId()).orElse(null);


            // Check if the username needs to be changed
            if (needsNameUpdate(oldTrainer, trainer)) {
                throw new OutdatedUsernameException(String.format(
                        "Trainer name %s %s has been updated to %s %s and their username should be changed",
                        oldTrainer.getFirstName(), oldTrainer.getLastName(),
                        trainer.getFirstName(), trainer.getLastName()
                ));
            }

            // Update fields
            oldTrainer = trainer.clone();

            session.merge(oldTrainer);

            transaction.commit();
        } catch (ConstraintViolationException ex) {
            throw new UserAlreadyExistsException("Failed to create trainer: " + trainer.toString());
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
    public void delete(Trainer trainer) throws DeletingNonexistentUserException {
        Session session = null;
        Transaction transaction = null;

        try {
            session = factory.openSession();
            transaction = session.beginTransaction();

            Trainer existingTrainer = get(trainer.getUsername()).orElse(null);

            session.remove(existingTrainer);

            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DeletingNonexistentUserException(ex.getMessage());
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
    public List<Trainer> getAll() {
        Session session = null;
        List<Trainer> results;
        try {
            session = factory.openSession();
            results = session.createQuery("SELECT a FROM Trainer a", Trainer.class).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get all Trainers. " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return results;
    }

    @Override
    public List<Trainer> getAllByUsername(String usernameSubstring) {
        Session session = null;
        List<Trainer> results;
        try {
            session = factory.openSession();

            Query<Trainer> query = session.createQuery(
                    "FROM Trainer t WHERE t.username LIKE :prefix", Trainer.class
            );
            query.setParameter("prefix", usernameSubstring + "%");
            results = query.getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get all by username. " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return results;
    }

    public List<Trainer> getAllExcludingTrainee(String traineeUsername) {
        return getAll()
                .stream()
                .filter(trainer ->
                        trainer.getTrainees()
                                .stream()
                                .noneMatch(trainee ->
                                        trainee.getUsername().equals(traineeUsername)))
                .toList();
    }
}
