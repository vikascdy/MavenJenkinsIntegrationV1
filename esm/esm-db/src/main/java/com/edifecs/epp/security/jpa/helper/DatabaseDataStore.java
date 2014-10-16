package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.datastore.*;
import com.edifecs.epp.security.exception.SecurityDatabaseNotConfiguredException;
import com.edifecs.epp.security.exception.SecurityManagerException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DatabaseDataStore implements ISecurityDataStore {

    private static EntityManagerFactory entityManagerFactory;

    public DatabaseDataStore() throws SecurityManagerException {
        Properties resourceProperties = new Properties();
        resourceProperties.put("Username", "sa");
        resourceProperties.put("Password", "");
        resourceProperties.put("Driver", "org.h2.Driver");
        resourceProperties.put("URL", "jdbc:h2:mem:TestDatabase");
        resourceProperties.put("Dialect", "org.hibernate.dialect.H2Dialect");
        resourceProperties.put("AutoCreate", true);

        connect(resourceProperties);
    }

    public DatabaseDataStore(Properties resourceProperties)
            throws SecurityManagerException {
        connect(resourceProperties);
    }

    public static EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * This creates a connection to the database directly using JPS style
     * properties.
     *
     * @throws SecurityDatabaseNotConfiguredException
     */
    private void connect(Properties resourceProperties)
            throws SecurityDatabaseNotConfiguredException {
        if (resourceProperties == null) {
            throw new SecurityDatabaseNotConfiguredException();
        }

        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.provider",
                "org.hibernate.jpa.HibernatePersistenceProvider");
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");

        properties.put("hibernate.connection.username",
                resourceProperties.getProperty("Username"));
        properties.put("hibernate.connection.password",
                resourceProperties.getProperty("Password"));
        properties.put("hibernate.connection.driver_class",
                resourceProperties.getProperty("Driver"));
        properties.put("hibernate.connection.url",
                resourceProperties.getProperty("URL"));
        properties.put("hibernate.connection.url",
                resourceProperties.getProperty("URL"));
        properties.put("hibernate.dialect",
                resourceProperties.getProperty("Dialect"));

        if (resourceProperties.get("AutoCreate") != null
                & new Boolean(resourceProperties.get("AutoCreate").toString())) {
            properties.put("hibernate.hbm2ddl.auto", "update");
        }

        properties.put("hibernate.show_sql", resourceProperties.getProperty("ShowSQL", "false"));
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

        entityManagerFactory = Persistence.createEntityManagerFactory(
                "security-db", properties);
    }

    @Override
    public void disconnect() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    @Override
    public IUserGroupDataStore getUserGroupDataStore() {
        return new UserGroupDataStore();
    }

    @Override
    public IOrganizationDataStore getOrganizationDataStore() {
        return new OrganizationDataStore();
    }

    @Override
    public IPermissionDataStore getPermissionDataStore() {
        return new PermissionDataStore();
    }

    @Override
    public IRoleDataStore getRoleDataStore() {
        return new RoleDataStore();
    }

    @Override
    public ITenantDataStore getTenantDataStore() {
        return new TenantDataStore();
    }

    @Override
    public IUserDataStore getUserDataStore() {
        return new UserDataStore();
    }

    @Override
    public ISiteDataStore getSiteDataStore() {
        return new SiteDataStore();
    }

}
