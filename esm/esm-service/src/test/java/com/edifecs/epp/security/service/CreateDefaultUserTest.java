package com.edifecs.epp.security.service;

import com.edifecs.epp.security.exception.SecurityManagerException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class CreateDefaultUserTest {

    @Test
    public void generateDefaultAccountsTest() throws SecurityManagerException, IOException {
        SecurityServiceCore service = new SecurityServiceCore();

        Properties properties = new Properties();
        properties.put("Username", "sa");
        properties.put("Password", "");
        properties.put("Driver", "org.h2.Driver");
        properties.put("URL", "jdbc:h2:mem:TestDatabase2");
        properties.put("Dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("AutoCreate", true);

        service.initializeDataStore(properties);

        Assert.assertTrue(true);
    }
}
