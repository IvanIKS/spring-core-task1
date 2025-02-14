package com.example.springcrm.dao;

import com.example.springcrm.model.Trainee;
import com.example.springcrm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<? extends User> getByUsername(String username);

    List<? extends User> getAllByUsername(String usernameSubstring);
}
