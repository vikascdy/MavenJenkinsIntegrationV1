package com.edifecs.epp.flexfields.jpa.helper;

import com.edifecs.epp.flexfields.datastore.IFlexFieldDefinitionDataStore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by sandeep.kath on 5/4/2014.
 */
public class DatabaseDataStore {

    private static EntityManagerFactory entityManagerFactory;

    public DatabaseDataStore() throws Exception {
        Properties resourceProperties = new Properties();
        resourceProperties.put("Username", "sa");
        resourceProperties.put("Password", "");
        resourceProperties.put("Driver", "org.h2.Driver");
        resourceProperties.put("URL", "jdbc:h2:mem:TestDatabase");
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

        Map<String, String> properties = new HashMap<>();
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

        properties.put("hibernate.cache.use_second_level_cache", "false");
        properties.put("hibernate.cache.use_query_cache", "false");
        properties.put("org.hibernate.cacheable", "false");
        properties.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
        properties.put("hibernate.archive.autodetection", "class ,hbm");
        properties.put("hibernate.c3p0.min_size", "5");
        properties.put("hibernate.c3p0.max_size", "20");
        properties.put("hibernate.c3p0.timeout", "300");
        properties.put("hibernate.c3p0.max_statements", "0");
        properties.put("hibernate.c3p0.idle_test_period", "3000");

//        Map<String, String> properties = new HashMap<>();
//        properties.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
//        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
//        properties.put("hibernate.connection.username", "sm");
//        properties.put("hibernate.connection.password", "welcome");
//        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
//        properties.put("hibernate.connection.url", "jdbc:mysql://smbox/flexfields");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        properties.put("hibernate.hbm2ddl.auto", "update");
//        properties.put("hibernate.show_sql", "false");
//        properties.put("hibernate.format_sql", "true");
//        properties.put("hibernate.archive.autodetection", "class ,hbm");

        entityManagerFactory = Persistence.createEntityManagerFactory("flexfielddb", properties);

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


    public IFlexFieldDefinitionDataStore getFlexFieldDefinitionDataStore() {
        return new FlexFieldDefinitionDataStore();
    }
}