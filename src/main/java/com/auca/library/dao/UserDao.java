package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.User;

public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save a user
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find user by person id
    public User findById(UUID personId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, personId);
        }
    }

    // find user by username
    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User u where u.userName = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }
}
