package com.example.springcrm.dao;

import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.Trainer;
import com.example.springcrm.model.User;
import com.example.springcrm.storage.Storage;
import com.example.springcrm.storage.TrainerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainerDao implements Dao<Trainer> {
    private final Storage storage;

    @Autowired
    public TrainerDao(@Qualifier("trainerStorage") Storage storage) {
        this.storage = storage;
    }

    @Override

    public Trainer get(String id) {
        return (Trainer) storage.get(id);
    }

    @Override
    public void create(Trainer trainer) throws UserAlreadyExistsException {
        String key = trainer.getUsername();
        if (trainer.getUserId() == null) {
            trainer.setUserId(storage.getNextId());
        }
        if (storage.get(key) == null) {
            storage.update(trainer);
        } else {
            throw new UserAlreadyExistsException(String.format(
                "Trainer %s %s already exists",
                trainer.getFirstName(),
                trainer.getLastName()
                ));
        }
    }

    @Override
    public void update(Trainer trainer) throws OutdatedUsernameException, UserAlreadyExistsException{
        Trainer oldVersion = (Trainer) storage.get(trainer);

        validateTrainer(oldVersion, trainer);

        storage.update(trainer);
    }

    @Override

    public List<Trainer> getAll() {
        return storage.getAll();
    }

    @Override
    public void delete(Trainer trainer) {
        storage.delete(trainer);
    }

    public List<Trainer> getAllByUsername(String usernameSubtring) {
        return ((TrainerStorage) storage).getAllByUsername(usernameSubtring);
    }

    private void validateTrainer(Trainer oldVersion, Trainer newVersion)
                throws OutdatedUsernameException, UserAlreadyExistsException {
        if (oldVersion != null) {
            if (User.userNameHasChanged(oldVersion, newVersion)) {
                throw new OutdatedUsernameException("Trainer name has been changed and trainer username must be updated");
            }
    
            if (oldVersion.getFirstName().equals(newVersion.getFirstName())
                && oldVersion.getLastName().equals(newVersion.getLastName())
                && !oldVersion.equals(newVersion)) {
                    throw new UserAlreadyExistsException("Updating username led to username overlap, requires handling");
                }

        }
    }

}
