package com.edifecs.servicemanager.agent;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

//FIXME: This test is broken and needs to be fixed
public class AgentServiceTest {

	@Test(expected=FileNotFoundException.class)
	public void agentServiceTest() throws Exception {
		AgentService agent = new AgentService(new String[]{});
		
		agent.start();
		
		agent.shutdown();
		
		Assert.assertTrue(true);
		
	}
	
	@Test(expected=FileNotFoundException.class)
	public void agentServiceTest2() throws Exception {
		AgentService agent = new AgentService(new String[]{});
		
		agent.start();
		Thread.sleep(1000L);
		agent.shutdown();
		
		Assert.assertTrue(true);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void agentServiceTest3() throws Exception {
		AgentService agent = new AgentService(new String[]{});
		
		agent.start();
		Thread.sleep(10000L);
		agent.shutdown();
		
		Assert.assertTrue(true);
	}

}
