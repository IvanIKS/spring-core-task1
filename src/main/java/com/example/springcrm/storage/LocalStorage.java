package com.example.springcrm.storage;

import java.util.List;

@Deprecated
public interface LocalStorage<T> {

    void init();

    T get(String key);

    T get(T value);

    void update(T newValue);

    void delete(T value);

    List<T> getAll();

    String getNextId();

    void cleanAll();
}
