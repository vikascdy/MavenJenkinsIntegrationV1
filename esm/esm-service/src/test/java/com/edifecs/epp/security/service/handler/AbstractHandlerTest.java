package com.edifecs.epp.security.service.handler;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.service.SecurityService;
import com.edifecs.servicemanager.api.ServiceAnnotationProcessor;
import com.edifecs.servicemanager.api.ServiceRef;
import com.edifecs.servicemanager.api.ServiceRegistry$;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Created by willclem on 5/14/2014.
 */
public class AbstractHandlerTest {

    protected static CommandCommunicator commandCommunicator;

    @BeforeClass
    public static void beforeClass() throws Exception {

        commandCommunicator = new CommandCommunicatorBuilder().initializeTestMode();
        commandCommunicator.connect();

        ServiceRegistry$.MODULE$.unregisterLocalService("securityService");

        SecurityService esm = new SecurityService();
        ServiceRef ref = ServiceAnnotationProcessor.processAnnotatedService(
                esm,
                "securityService",
                commandCommunicator);
        ref.startTestMode();

        commandCommunicator.getSecurityManager().getAuthenticationManager().loginToken(
                new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME,
                        SystemVariables.DEFAULT_ORG_NAME,
                        "admin", "admin".toCharArray()));
    }


    @AfterClass
    public static void afterClass() {
        commandCommunicator.disconnect();
    }
}
