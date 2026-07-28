package com.auca.library.dao;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.auca.library.domain.Location;

public class LocationDao {

    private final SessionFactory sessionFactory;

    public LocationDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // save a location in the database
    public Location save(Location location) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(location);
            transaction.commit();
            return location;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    // find location by id
    public Location findById(UUID locationId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Location.class, locationId);
        }
    }

    // find location by code
    public Location findByCode(String locationCode) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Location l where l.locationCode = :code", Location.class)
                    .setParameter("code", locationCode)
                    .uniqueResult();
        }
    }
}
