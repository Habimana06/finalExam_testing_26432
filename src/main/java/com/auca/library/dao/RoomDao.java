package com.auca.library.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.Room;

public class RoomDao {

    private final SessionFactory sessionFactory;

    public RoomDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save room
    public Room save(Room room) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(room);
            transaction.commit();
            return room;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find room by id
    public Room findById(UUID roomId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Room.class, roomId);
        }
    }

    // get all rooms
    public List<Room> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Room r", Room.class).getResultList();
        }
    }
}
