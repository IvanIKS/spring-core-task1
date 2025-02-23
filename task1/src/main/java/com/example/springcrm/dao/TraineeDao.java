package com.example.springcrm.dao;

import com.example.springcrm.exception.OutdatedUsernameException;
import com.example.springcrm.exception.UserAlreadyExistsException;
import com.example.springcrm.model.User;
import com.example.springcrm.model.Trainee;
import com.example.springcrm.storage.Storage;
import com.example.springcrm.storage.TraineeStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraineeDao implements Dao<Trainee> {
    private final Storage<Trainee> storage;

    @Autowired
    public TraineeDao(@Qualifier("traineeStorage") Storage<Trainee> storage) {
        this.storage = storage;
    }

    @Override
    public Trainee get(String id) {
        return storage.get(id);
    }

    @Override
    public void create(Trainee trainee) throws UserAlreadyExistsException {
        if (trainee.getUserId() == null) {
            trainee.setUserId(storage.getNextId());
        }
        if (storage.get(trainee) == null) {
            storage.update(trainee);
        } else {
            throw new UserAlreadyExistsException(String.format(
                "Trainee %s %s already exists",
                trainee.getFirstName(),
                trainee.getLastName()
                ));
        }
    }

    @Override
    public void update(Trainee trainee) throws OutdatedUsernameException {
        Trainee oldVersion = storage.get(trainee);

        if (oldVersion != null 
            && User.userNameHasChanged(oldVersion, trainee)) {
            throw new OutdatedUsernameException(String.format(
                    "Trainee name %s %s has been updated to %s %s and its username should be changed",
                    oldVersion.getFirstName(), oldVersion.getLastName(),
                    trainee.getFirstName(), trainee.getLastName()
            ));
        }

        storage.update(trainee);
    }


    @Override
    public void delete(Trainee trainee) {
        storage.delete(trainee);
    }

    @Override
    public List<Trainee> getAll() {
        return storage.getAll();
    }

    public List<Trainee> getAllByUsername(String usernameSubstring) {
        return ((TraineeStorage) storage).getAllByUsername(usernameSubstring);
    }
}
