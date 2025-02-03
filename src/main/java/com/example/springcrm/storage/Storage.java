package com.example.springcrm.storage;

import java.util.List;

public interface Storage<T> {
    static boolean LOADING_FROM_FILE = true;

    void init();

    T get(String key);

    T get(T value);

    void update(T newValue);

    void delete(T value);

    List<T> getAll();

    String getNextId();

    void cleanAll();
}
