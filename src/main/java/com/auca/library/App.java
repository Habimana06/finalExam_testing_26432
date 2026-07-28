package com.auca.library;

import com.auca.library.util.HibernateUtil;

public class App {

    public static void main(String[] args) {
        // start hibernate to create tables
        HibernateUtil.buildSessionFactory("application.properties");
        System.out.println("AUCA Library Management System started.");
    }
}
