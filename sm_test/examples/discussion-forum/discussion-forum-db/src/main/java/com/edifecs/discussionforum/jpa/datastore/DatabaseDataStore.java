package com.edifecs.discussionforum.jpa.datastore;

import com.edifecs.discussionforum.api.datastore.IForumDataStore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by sandeep.kath on 5/18/2014.
 */
public class DatabaseDataStore {
    private static EntityManagerFactory entityManagerFactory;

    public DatabaseDataStore() throws Exception {
        Properties resourceProperties = new Properties();
        resourceProperties.put("Username", "sa");
        resourceProperties.put("Password", "");
        resourceProperties.put("Driver", "org.h2.Driver");
        resourceProperties.put("URL", "jdbc:h2:mem:ForumDatabase");
        resourceProperties.put("Dialect", "org.hibernate.dialect.H2Dialect");
        resourceProperties.put("AutoCreate", true);

        connect(resourceProperties);
    }

    public DatabaseDataStore(Properties resourceProperties) throws Exception {
        connect(resourceProperties);
    }

    private void connect(Properties resourceProperties) throws Exception {
        if (resourceProperties == null) {
            throw new Exception();
        }

        Map<String, String> properties = new HashMap<String,String>();
        properties.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");

        properties.put("hibernate.connection.username", resourceProperties.getProperty("Username"));
        properties.put("hibernate.connection.password", resourceProperties.getProperty("Password"));
        properties.put("hibernate.connection.driver_class", resourceProperties.getProperty("Driver"));
        properties.put("hibernate.connection.url", resourceProperties.getProperty("URL"));
        properties.put("hibernate.dialect", resourceProperties.getProperty("Dialect"));

        if (resourceProperties.get("AutoCreate") != null & new Boolean(resourceProperties.get("AutoCreate").toString())) {
            properties.put("hibernate.hbm2ddl.auto", "update");
        }


        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.archive.autodetection", "class ,hbm");

//        Map<String, String> properties = new HashMap<>();
//        properties.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
//        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
//        properties.put("hibernate.connection.username", "root");
//        properties.put("hibernate.connection.password", "welcome");
//        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
//        properties.put("hibernate.connection.url", "jdbc:mysql://localhost/forum");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        properties.put("hibernate.hbm2ddl.auto", "update");
//        properties.put("hibernate.show_sql", "false");
//        properties.put("hibernate.format_sql", "true");
//        properties.put("hibernate.archive.autodetection", "class ,hbm");

        entityManagerFactory = Persistence.createEntityManagerFactory("forum-db", properties);
    }

    public void disconnect() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    public static EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }


    public IForumDataStore getForumDataStore() throws Exception{
        return new ForumDataStoreDBImpl();
    }
}
