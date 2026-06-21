package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User create(String name, String email, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Имя не может быть пустым");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email не может быть пустым");
        }
        if (age <= 0) {
            throw new IllegalArgumentException(
                    "Возраст должен быть больше 0");
        }

        User user = new User(name, email, age);
        userDao.save(user);
        logger.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID должен быть больше 0");
        }
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User update(Long id, String name, String email, int age) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с ID=" + id + " не найден"));

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (age > 0) {
            user.setAge(age);
        }

        userDao.update(user);
        logger.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    public void delete(Long id) {
        userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с ID=" + id + " не найден"));

        userDao.delete(id);
        logger.info("Удалён пользователь с ID={}", id);
    }
}