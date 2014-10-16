package com.edifecs.epp.security.service;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.isc.stream.MessageStream;
import com.edifecs.epp.security.data.Contact;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.handler.rest.UserHandler;
import com.edifecs.epp.security.data.CSVJsonUtil;
import com.edifecs.servicemanager.api.ServiceAnnotationProcessor;
import com.edifecs.servicemanager.api.ServiceRef;
import com.edifecs.servicemanager.api.ServiceRegistry$;
import com.google.gson.Gson;
import org.junit.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestBatchImportUsers {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testImport() throws Exception {
        CommandCommunicator cc = new CommandCommunicatorBuilder().initializeTestMode();
        cc.connect();

        ServiceRegistry$.MODULE$.unregisterLocalService("securityService");

        SecurityService esm = new SecurityService();
        ServiceRef ref = ServiceAnnotationProcessor.processAnnotatedService(
                esm,
                "securityService",
                cc);
        ref.startTestMode();

        cc.getSecurityManager().getAuthenticationManager().loginToken(
                new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME,
                SystemVariables.DEFAULT_ORG_NAME,
                "admin", "admin".toCharArray()));

        SecurityContext sc = new SecurityContext();
        sc.initDataStore(new DatabaseDataStore());
        sc.initManager(new com.edifecs.epp.security.remote.SecurityManager(null, cc));
        UserHandler handler = new UserHandler(sc);
        handler.initialize(cc, cc);

        InputStream in = getClass().getResourceAsStream("/users.csv");
        Collection<CSVJsonUtil> result = handler.validateUsersCSV(MessageStream.fromInputStream(in));

        Gson gson = new Gson();
        System.out.println("json : " + gson.toJson(result));

        List<String> csvUsers = new ArrayList<>();
        for (CSVJsonUtil json : result) {
            if (json.isValid())
                csvUsers.add(json.getLine());
        }

        handler.batchImportUsers((ArrayList<String>) csvUsers);

        for (User u : handler.getUsers(0, Integer.MAX_VALUE)
                .getResultList()) {
            System.out.println(String.format(
                    "found user with username : %s", getFirstName(u)));
        }

        cc.disconnect();
    }

    private String getFirstName(User u) {
        Contact c = u == null ? null : u.getContact();
        return c == null ? null : c.getFirstName();
    }

}
