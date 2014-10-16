# Service Development

A service is a component that is run within the cluster. To help with configuration, deployment, and maintenance of the
service within the application platform, there are annotations and configurations that can be defined for the service.

Here is a simple example:

Interface:

    package com.edifecs.helloexample.service;
    
    import com.edifecs.helloexample.handler.IHelloExampleHandler;
    import com.edifecs.servicemanager.annotations.Handler;
    import com.edifecs.servicemanager.annotations.Property;
    import com.edifecs.servicemanager.annotations.Property.PropertyType;
    import com.edifecs.servicemanager.annotations.Service;
    
    @Service(
    	name = "hello-example-service", 
    	version = "1.0", 
    	description = "Hello Example Service", 
    	properties = {@Property(name = "name", propertyType = PropertyType.STRING, description = "" , defaultValue = "world", required = true)}
    )
    public interface IHelloExampleService {
    
        @Handler
        IHelloExampleHandler getHelloExampleCommandHandler();
    }

Implementation:

    package com.edifecs.helloexample.service;
    
    import com.edifecs.helloexample.handler.HelloExampleHandler;
    import com.edifecs.helloexample.handler.IHelloExampleHandler;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    import com.edifecs.servicemanager.api.AbstractService;
    
    public class HelloExampleService extends AbstractService implements IHelloExampleService {
    	
    	private Logger logger = LoggerFactory.getLogger(HelloExampleService.class);
    
    	@Override
    	public void start() throws Exception {
    		logger.info("hello example service started.");
    	}
    
    	@Override
    	public void stop() throws Exception {
    		//TODO
    		logger.info("hello example service stopped.");
    	}
    
        @Override
        public IHelloExampleHandler getHelloExampleCommandHandler() {
            String greetee = getProperties().getProperty("name");
            return new HelloExampleHandler(greetee);
        }
    }


## Available Options

All information about the annotations can be found in the JAVADOCS

The key area's of configurations available are:

  * Service details and version information
  * Dependencies required for the Service to run.
  * Configuration properties the service needs.
  * Command Handlers associated with the service
  * Service Requirements
  * Service Guarantee Information
  * Monitoring Concerns
  * Routing Concerns

The Majority of these configurations are used at build time to compile default configurations for the application or
module. Some of these can be changed during installation and runtime.

Besides the configuration of what a service is and information to help the platform scale the service, a Service is the
starting point of a component within a module.

This can either be initialization and shutdown with no actively running threads, to spawning multiple never ending
threads. This is up to the service to define.