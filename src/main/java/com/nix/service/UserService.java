package com.nix.service;

import com.nix.model.User;

import java.util.List;

public interface UserService {

    void create(User user);

    void update(User user);

    void remove(User user);

    List<User> findAll();

    User findByLogin(String login);

    User findByEmail(String email);

}
