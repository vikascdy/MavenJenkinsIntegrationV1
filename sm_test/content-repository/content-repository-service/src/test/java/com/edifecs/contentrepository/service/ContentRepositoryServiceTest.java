package com.edifecs.contentrepository.service;

import com.edifecs.contentrepository.IContentRepositoryHandler;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.service.SecurityService;
import com.edifecs.servicemanager.api.ServiceAnnotationProcessor;
import com.edifecs.servicemanager.api.ServiceRef;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ContentRepositoryServiceTest {

    static ContentRepositoryService cr;
    static IContentRepositoryHandler handler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        CommandCommunicator commandCommunicator = new CommandCommunicatorBuilder().initializeTestMode();
        commandCommunicator.connect();

        SecurityService securityService = new SecurityService();
        ServiceRef ref = ServiceAnnotationProcessor.processAnnotatedService(
                securityService,
                "securityServiceCRTestMode",
                commandCommunicator);
        ref.startTestMode();

        commandCommunicator.getSecurityManager().getAuthenticationManager().loginToken(
                new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME,
                        SystemVariables.DEFAULT_ORG_NAME,
                        "admin", "admin".toCharArray()));

        cr = new ContentRepositoryService();
        ServiceRef crref = ServiceAnnotationProcessor.processAnnotatedService(
                cr,
                "contentRepositoryTestMode",
                commandCommunicator);
        crref.startTestMode();

        ContentRepositoryServiceTest.handler = cr.getContentRepositoryHandler();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        cr.stop();
    }

    @Test
    public void isAvailableTest() throws ContentRepositoryException {
        Assert.assertTrue(handler.isAvailable());
    }

    @Test
    public void addFileTest() throws ContentRepositoryException {
        SecureRandom random = new SecureRandom();
        String s = new BigInteger(130, random).toString(32);

        handler.createFolder("/test" + s + "/");
        Assert.assertNotNull(handler.addFile("/test" + s + "/", "sampleFileName", this
                .getClass().getResourceAsStream("/test-file.txt")));
        Assert.assertNotNull(handler.addFile("/test" + s + "/", "sampleFileName2", this
                .getClass().getResourceAsStream("/test-file.txt")));
    }

}
