package com.example.springcrm.dao;

import com.example.springcrm.model.User;

import java.util.List;

public interface Dao<T> {
    T get(String key);

    void create(T t);

    void update(T newValue);

    void delete(T value);

    List<T> getAll();
}
