package com.auca.library.service;

import java.util.UUID;

import org.hibernate.SessionFactory;

import com.auca.library.dao.UserDao;
import com.auca.library.domain.User;

public class UserService {

    private UserDao userDao;

    public UserService(SessionFactory sessionFactory) {
        this.userDao = new UserDao(sessionFactory);
    }

    // save user account
    public User registerUser(User user) {
        if (user.getPersonId() == null) {
            user.setPersonId(UUID.randomUUID());
        }
        return userDao.save(user);
    }

    // authenticate user with username and password
    public boolean authenticate(String username, String rawPassword) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return false;
        }

        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            return false;
        }

        return rawPassword.equals(user.getPassword());
    }

    public User findById(UUID personId) {
        return userDao.findById(personId);
    }
}
