package com.example.springcrm.dao;

import com.example.springcrm.exception.DeletingNonexistentUserException;
import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainee;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class TraineeDao implements Dao<Trainee>, UserDao {
    private final SessionFactory factory;

    public TraineeDao(@Qualifier("sessionFactory") SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Trainee> getByUsername(String username) {
        Session session = null;
        Trainee result;
        try {
            session = factory.openSession();

            Query<Trainee> query = session.createQuery(
                    "FROM Trainee t WHERE t.username = :name", Trainee.class
            );
            query.setParameter("name", username);

            result = query.getResultList().get(0);
        } catch (IndexOutOfBoundsException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get trainee. " + ex.getClass() + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return Optional.of(result);
    }

    @Override
    public Optional<Trainee> get(String id) {
        Session session = null;
        Trainee result;
        try {
            session = factory.openSession();

            Query<Trainee> query = session.createQuery(
                    "FROM Trainee t WHERE t.userId = :id", Trainee.class
            );
            query.setParameter("id", id);

            result = query.getResultList().get(0);
        } catch (IndexOutOfBoundsException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get trainee. " + ex.getClass() + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return Optional.of(result);
    }

    @Override
    public void create(Trainee trainee) throws UserAlreadyExistsException, IllegalArgumentException {
        Session session = null;
        Transaction transaction = null;
        try {
            validateUserForDatabase(trainee);

            session = factory.openSession();
            transaction = session.beginTransaction();

            session.persist(trainee);
            transaction.commit();

        } catch (ConstraintViolationException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new UserAlreadyExistsException("Failed to create trainee: " + trainee.toString()
                    + " reason: " + ex.getMessage());
        } catch (PropertyValueException ex) {
            throw new IllegalArgumentException("Failed to create trainee: " + trainee.toString()
                    + " reason: " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(Trainee trainee) throws OutdatedUsernameException {
        Session session = null;
        Transaction transaction = null;

        try {
            validateUserForDatabase(trainee);

            session = factory.openSession();
            transaction = session.beginTransaction();

            Trainee oldTrainee = get(trainee.getUserId()).orElse(null);


            // Check if the username needs to be changed
            if (needsNameUpdate(oldTrainee, trainee)) {
                throw new OutdatedUsernameException(String.format(
                        "Trainee name %s %s has been updated to %s %s and their username should be changed",
                        oldTrainee.getFirstName(), oldTrainee.getLastName(),
                        trainee.getFirstName(), trainee.getLastName()
                ));
            }

            // Update fields
            oldTrainee = trainee.clone();

            session.merge(oldTrainee);

            transaction.commit();
        } catch (ConstraintViolationException ex) {
            throw new UserAlreadyExistsException("Failed to create trainer: " + trainee.toString());
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
    public void delete(Trainee trainee) throws DeletingNonexistentUserException {
        Session session = null;
        Transaction transaction = null;

        try {
            session = factory.openSession();
            transaction = session.beginTransaction();

            Trainee existingTrainee = getByUsername(trainee.getUsername()).orElse(null);

            session.remove(existingTrainee);

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
    public List<Trainee> getAll() {
        Session session = null;
        List<Trainee> results;
        try {
            session = factory.openSession();
            results = session.createQuery("SELECT a FROM Trainee a", Trainee.class).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get all comments. " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return results;
    }

    @Override
    public List<Trainee> getAllByUsername(String usernameSubstring) {
        Session session = null;
        List<Trainee> results;
        try {
            session = factory.openSession();

            Query<Trainee> query = session.createQuery(
                    "FROM Trainee t WHERE t.username LIKE :prefix", Trainee.class
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

}
