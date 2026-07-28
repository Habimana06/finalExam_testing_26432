package com.auca.library.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.Shelf;

public class ShelfDao {

    private final SessionFactory sessionFactory;

    public ShelfDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save shelf
    public Shelf save(Shelf shelf) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(shelf);
            transaction.commit();
            return shelf;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // update shelf
    public Shelf update(Shelf shelf) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(shelf);
            transaction.commit();
            return shelf;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find shelf by id
    public Shelf findById(UUID shelfId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Shelf.class, shelfId);
        }
    }

    // find shelves in a room
    public List<Shelf> findByRoomId(UUID roomId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Shelf s where s.room.roomId = :roomId", Shelf.class)
                    .setParameter("roomId", roomId)
                    .getResultList();
        }
    }
}
