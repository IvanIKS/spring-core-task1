package com.example.utils;

import com.example.springcrm.dao.Dao;
import com.example.springcrm.dao.HibernateUtil;
import com.example.springcrm.exception.DeletingNonexistentUserException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.TrainingType;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

/**
 * There is no way to save new Training Type from app other than this.
 * There is no need in Service class.
 */

public class TrainingTypeDao implements Dao<TrainingType> {
    private SessionFactory factory = HibernateUtil.getSessionFactory();

    @Override
    public Optional<TrainingType> get(String id) {
        Session session = null;
        TrainingType result;
        try {
            session = factory.openSession();

            Query<TrainingType> query = session.createQuery(
                    "FROM TrainingType t WHERE t.id LIKE :id", TrainingType.class
            );
            query.setParameter("id", (id));

            result = query.getResultList().get(0);
        } catch (IndexOutOfBoundsException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get TrainingType. " + ex.getClass() + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return Optional.of(result);
    }

    @Override
    public void create(TrainingType trainingType) {
        Session session = null;
        Transaction transaction = null;
        try {

            session = factory.openSession();
            transaction = session.beginTransaction();

            session.persist(trainingType);
            transaction.commit();

        } catch (EntityExistsException | ConstraintViolationException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new UserAlreadyExistsException("Failed to create trainingType: " + trainingType.toString()
                    + " reason: " + ex.getMessage());
        } catch (PropertyValueException ex) {
            throw new IllegalArgumentException("Failed to create trainingType: " + trainingType.toString()
                    + " reason: " + ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(TrainingType newValue) {
        /// There is no way nor need to update it from the app.
    }

    @Override
    public void delete(TrainingType value) throws DeletingNonexistentUserException {
        /// There is no way nor need to update it from the app.
    }

    @Override
    public List<TrainingType> getAll() {
        return List.of();
    }
}

