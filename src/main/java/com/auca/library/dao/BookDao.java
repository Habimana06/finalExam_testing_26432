package com.auca.library.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.Book;

public class BookDao {

    private final SessionFactory sessionFactory;

    public BookDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save a book
    public Book save(Book book) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
            return book;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // update a book
    public Book update(Book book) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(book);
            transaction.commit();
            return book;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find book by id
    public Book findById(UUID bookId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Book.class, bookId);
        }
    }

    // count books in a room
    public int countBooksInRoom(UUID roomId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(b) from Book b where b.shelf.room.roomId = :roomId", Long.class)
                    .setParameter("roomId", roomId)
                    .uniqueResult();
            return count == null ? 0 : count.intValue();
        }
    }

    // count books by status on shelf (helper)
    public List<Book> findByShelfId(UUID shelfId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book b where b.shelf.shelfId = :shelfId", Book.class)
                    .setParameter("shelfId", shelfId)
                    .getResultList();
        }
    }
}
