package com.edifecs.servicemanager.agent.exception;

import org.junit.Test;

public class ExceptionTests {

	@Test( expected = AgentStartupException.class)
	public void agentStartupExceptionTest() throws AgentStartupException {
		throw new AgentStartupException("I want to throw this Error");
	}
	
	@Test( expected = AgentStartupException.class)
	public void agentStartupExceptionTest2() throws AgentStartupException {
		throw new AgentStartupException(new AgentStartupException());
	}
	
	@Test( expected = AgentStartupException.class)
	public void agentStartupExceptionTest3() throws AgentStartupException {
		throw new AgentStartupException("I want to throw this Error", new NoClassDefFoundError());
	}
	
	@Test( expected = InvalidCoreConfigurationException.class)
	public void invalidCoreConfigurationExceptionTest() throws InvalidCoreConfigurationException {
		throw new InvalidCoreConfigurationException("I want to throw this Error");
	}
	
	@Test( expected = InvalidCoreConfigurationException.class)
	public void invalidCoreConfigurationExceptionTest2() throws InvalidCoreConfigurationException {
		throw new InvalidCoreConfigurationException(new InvalidCoreConfigurationException());
	}
	
	@Test( expected = InvalidCoreConfigurationException.class)
	public void invalidCoreConfigurationExceptionTest3() throws InvalidCoreConfigurationException {
		throw new InvalidCoreConfigurationException("I want to throw this Error", new InvalidCoreConfigurationException());
	}
	
}
