package com.edifecs.helloexample.test;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.async.Callback;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.service.SecurityService;
import com.edifecs.helloexample.api.HelloMessage;
import com.edifecs.helloexample.service.HelloExampleService;
import com.edifecs.helloexample.service.IHelloExampleService;
import com.edifecs.servicemanager.api.ServiceAnnotationProcessor;
import com.edifecs.servicemanager.api.ServiceRef;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by willclem on 5/21/2014.
 */
public class HelloExampleTest {

    private static CommandCommunicator isc;

    private static Address address;

    @BeforeClass
    public static void beforeClass() throws Exception {
        isc = new CommandCommunicatorBuilder().setProperty("akka.loglevel", "DEBUG").initializeTestMode();
        isc.connectExclusively();

        SecurityService securityService = new SecurityService();
        ServiceRef securityServiceRef = ServiceAnnotationProcessor.processAnnotatedService(
                securityService, "securityService", isc);
        securityServiceRef.startTestMode();

        isc.getSecurityManager().getAuthenticationManager().loginToken(
                new UsernamePasswordAuthenticationToken(
                    SystemVariables.DEFAULT_TENANT_NAME,
                    SystemVariables.DEFAULT_ORG_NAME,
                    "admin",
                    "admin".toCharArray()));

        HelloExampleService helloService = new HelloExampleService();
        ServiceRef service = ServiceAnnotationProcessor.processAnnotatedService(
                helloService, "hello", isc);
        service.startTestMode();

        address = service.getAddress();
    }

    @AfterClass
    public static void afterClass() {
        isc.disconnect();
    }

    @Test
    public void testGreeting() throws Exception {
        HelloMessage helloMessage = (HelloMessage)isc.sendSync(address, "hello.greeting");
        Assert.assertNotNull(helloMessage);
    }

    @Test
    public void testGreetingFromTheFuture() throws InterruptedException {
        final SynchronousQueue queue = new SynchronousQueue();

        isc.send(address, "hello.greetingFromTheFuture").as(HelloMessage.class).thenDo(new Callback<HelloMessage>() {
            @Override
            public void call(HelloMessage helloMessage) {
                try {
                    queue.put(helloMessage);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }).orCatch(new Callback<MessageException>() {
            @Override
            public void call(MessageException exception) {
                exception.printStackTrace();
        }});

        Assert.assertNotNull(queue.poll(5, TimeUnit.SECONDS));
    }

    @Test
    public void testClientGreeting() throws ServiceTypeNotFoundException {
        HelloMessage helloMessage = isc.getService(IHelloExampleService.class).getHelloExampleCommandHandler().greeting();
        Assert.assertNotNull(helloMessage);
    }

    @Test
    public void testClientGreetingFromTheFuture() throws InterruptedException, ServiceTypeNotFoundException {
        final SynchronousQueue queue = new SynchronousQueue();

        isc.getService(IHelloExampleService.class).getHelloExampleCommandHandler().greetingFromTheFuture().thenDo(
                new Callback<HelloMessage>() {
                    @Override
                    public void call(HelloMessage helloMessage) {
                        try {
                            queue.put(helloMessage);
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }
                    }
                }
        ).orCatch(new Callback<MessageException>() {
            @Override
            public void call(MessageException exception) {
                exception.printStackTrace();
            }});

        Assert.assertNotNull(queue.poll(5, TimeUnit.SECONDS));
    }

}
