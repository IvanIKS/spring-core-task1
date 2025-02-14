package com.example.springcrm.dao;

import com.example.springcrm.exception.DeletingNonexistentUserException;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> get(String id);

    void create(T t);

    void update(T newValue);

    void delete(T value) throws DeletingNonexistentUserException;

    List<T> getAll();
}
