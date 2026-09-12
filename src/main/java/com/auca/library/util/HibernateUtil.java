package com.auca.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.auca.library.domain.Book;
import com.auca.library.domain.Borrower;
import com.auca.library.domain.Location;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;
import com.auca.library.domain.User;

public class HibernateUtil {

    // Builds SessionFactory from properties file
    public static SessionFactory buildSessionFactory(String propertiesFile) {
        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream(propertiesFile)) {

            if (input == null) {
                throw new IllegalArgumentException(propertiesFile + " not found on the classpath");
            }

            Properties properties = new Properties();
            properties.load(input);
            applyEnvironmentOverrides(properties);

            return new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Location.class)
                    .addAnnotatedClass(Book.class)
                    .addAnnotatedClass(Borrower.class)
                    .addAnnotatedClass(Membership.class)
                    .addAnnotatedClass(MembershipType.class)
                    .addAnnotatedClass(Shelf.class)
                    .addAnnotatedClass(Room.class)
                    .buildSessionFactory();

        } catch (IOException e) {
            throw new RuntimeException("Could not read " + propertiesFile, e);
        }
    }

    private static void applyEnvironmentOverrides(Properties properties) {
        overrideFromEnv(properties, "hibernate.connection.url", "DB_URL");
        overrideFromEnv(properties, "hibernate.connection.username", "DB_USER");
        overrideFromEnv(properties, "hibernate.connection.password", "DB_PASSWORD");
    }

    private static void overrideFromEnv(Properties properties, String propertyKey, String envKey) {
        String value = System.getenv(envKey);
        if (value != null && !value.isBlank()) {
            properties.setProperty(propertyKey, value);
        }
    }
}
