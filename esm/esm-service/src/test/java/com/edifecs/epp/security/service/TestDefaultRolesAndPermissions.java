package com.edifecs.epp.security.service;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.security.data.Permission;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.data.UserGroup;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.service.util.SecurityJsonHelper;
import com.edifecs.servicemanager.api.ServiceAnnotationProcessor;
import com.edifecs.servicemanager.api.ServiceRef;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class TestDefaultRolesAndPermissions {

    private static SecurityService securityService;

    private static Gson gson;

    static CommandCommunicator commandCommunicator;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        gson = new Gson();

        // Initialize a commandCommunicator Instance
        commandCommunicator = new CommandCommunicatorBuilder().initializeTestMode();
        commandCommunicator.connect();

        // Initialize the esm-service
        securityService = new SecurityService();
        ServiceRef ref = ServiceAnnotationProcessor.processAnnotatedService(
                securityService,
                "securityServiceTestMode2",
                commandCommunicator);
        ref.startTestMode();

        commandCommunicator.getSecurityManager().getAuthenticationManager().loginToken(
                new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME,
                        SystemVariables.DEFAULT_ORG_NAME,
                        "admin", "admin".toCharArray()));

        User user = commandCommunicator.getService(ISecurityService.class).subjects().getUser();
        Assert.assertNotNull(user);

        Collection<UserGroup> userGroups = commandCommunicator.getService(ISecurityService.class).subjects().getUserGroups();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        commandCommunicator.disconnect();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {

        Type type = new TypeToken<List<Permission>>() {
        }.getType();
        InputStream is = this.getClass().getResourceAsStream("/security.json");
        BufferedReader buff = new BufferedReader(new InputStreamReader(is));

        System.out.println("json : " + gson.fromJson(buff, SecurityJsonHelper.class));

    }

    @Test
    public void loadSecurityJson() {
        Tenant tenant = new Tenant();
        InputStream securityJson = getClass().getResourceAsStream("/security.json");

        securityService.core.createAppRolesAndPermissions(1L, securityJson, "TEST_FILE", false);
    }

}
