package org.example.service;

import org.example.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User create(String name, String email, int age);

    Optional<User> findById(Long id);

    List<User> findAll();

    User update(Long id, String name, String email, int age);

    void delete(Long id);
}