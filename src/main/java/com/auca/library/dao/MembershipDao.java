package com.auca.library.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.Membership;
import com.auca.library.domain.Status;

public class MembershipDao {

    private final SessionFactory sessionFactory;

    public MembershipDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save membership
    public Membership save(Membership membership) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(membership);
            transaction.commit();
            return membership;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find membership by id
    public Membership findById(UUID membershipId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Membership.class, membershipId);
        }
    }

    // find active (approved or pending) membership for user
    public Membership findActiveByUserId(UUID userId) {
        try (Session session = sessionFactory.openSession()) {
            List<Membership> list = session.createQuery(
                    "from Membership m where m.reader.personId = :userId and m.membershipStatus in (:statuses)",
                    Membership.class)
                    .setParameter("userId", userId)
                    .setParameterList("statuses", List.of(Status.APPROVED, Status.PENDING))
                    .getResultList();
            if (list.isEmpty()) {
                return null;
            }
            return list.get(0);
        }
    }

    // find approved membership for user
    public Membership findApprovedByUserId(UUID userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Membership m where m.reader.personId = :userId and m.membershipStatus = :status",
                    Membership.class)
                    .setParameter("userId", userId)
                    .setParameter("status", Status.APPROVED)
                    .uniqueResult();
        }
    }

    // update membership
    public Membership update(Membership membership) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(membership);
            transaction.commit();
            return membership;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }
}
