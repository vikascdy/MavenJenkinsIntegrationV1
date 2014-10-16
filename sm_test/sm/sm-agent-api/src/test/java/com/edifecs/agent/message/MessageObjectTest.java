package com.edifecs.agent.message;

import org.junit.Assert;
import org.junit.Test;

public class MessageObjectTest {

	private static final String HOSTNAME = "hostname";
	private static final String IPADDRESS = "192.168.1.1";
	private static final String OS = "WIN";
	private static final String ARCH = "x64";
	private static final int CORES = 8;
	private static final int MHZ = 333;
	private static final int MEM = 1024;

	private static final String PROCESS = "processname";
	private static final String PID = "32434";
	
	@Test
	public void ServerDetailsTest() {
		ServerDetails serverDetails = new ServerDetails(
				HOSTNAME,
				IPADDRESS,
				OS,
				ARCH,
				CORES,
				MHZ,
				MEM);
		Assert.assertEquals(HOSTNAME, serverDetails.getHostname());
		Assert.assertEquals(IPADDRESS, serverDetails.getIpAddress());
		Assert.assertEquals(OS, serverDetails.getOs());
		Assert.assertEquals(ARCH, serverDetails.getArch());
		Assert.assertTrue(CORES == serverDetails.getCpuCores());
		Assert.assertTrue(MHZ == serverDetails.getCpuMHz());
		Assert.assertTrue(MEM == serverDetails.getMemMB());
	}
	
	@Test
	public void PIDTest() {
		PID pid = new PID(
				PROCESS,
				PID);
		Assert.assertEquals(PROCESS, pid.getProcess());
		Assert.assertEquals(PID, pid.getPid());
	}

}
