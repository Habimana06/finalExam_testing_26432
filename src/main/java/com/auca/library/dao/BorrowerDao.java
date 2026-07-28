package com.auca.library.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.Borrower;

public class BorrowerDao {

    private final SessionFactory sessionFactory;

    public BorrowerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save borrower record
    public Borrower save(Borrower borrower) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(borrower);
            transaction.commit();
            return borrower;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // update borrower record
    public Borrower update(Borrower borrower) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(borrower);
            transaction.commit();
            return borrower;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find borrower by id
    public Borrower findById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Borrower.class, id);
        }
    }

    // count active borrows (not yet returned)
    public int countActiveBorrows(UUID readerId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(b) from Borrower b where b.reader.personId = :readerId and b.returnDate is null",
                    Long.class)
                    .setParameter("readerId", readerId)
                    .uniqueResult();
            return count == null ? 0 : count.intValue();
        }
    }

    // list active borrows for a reader
    public List<Borrower> findActiveByReader(UUID readerId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Borrower b where b.reader.personId = :readerId and b.returnDate is null",
                    Borrower.class)
                    .setParameter("readerId", readerId)
                    .getResultList();
        }
    }
}
