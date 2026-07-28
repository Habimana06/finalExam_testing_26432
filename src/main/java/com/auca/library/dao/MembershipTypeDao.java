package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.MembershipType;

public class MembershipTypeDao {

    private final SessionFactory sessionFactory;

    public MembershipTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save membership type
    public MembershipType save(MembershipType membershipType) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(membershipType);
            transaction.commit();
            return membershipType;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find membership type by id
    public MembershipType findById(UUID membershipTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(MembershipType.class, membershipTypeId);
        }
    }

    // find by name like Gold, Silver, Striver
    public MembershipType findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from MembershipType m where m.membershipName = :name", MembershipType.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }
}
