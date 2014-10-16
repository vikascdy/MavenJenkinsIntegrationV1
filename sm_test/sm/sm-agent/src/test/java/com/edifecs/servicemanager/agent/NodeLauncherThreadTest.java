package com.edifecs.servicemanager.agent;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class NodeLauncherThreadTest {

	@Test
	public void mergeJVMOptionTest() {

		NodeLauncherThread node = new NodeLauncherThread(null, null, null);

		List<String> a = new ArrayList<String>();
		a.add("-Djava.net.preferIPv4Stack=true");
		a.add("-Xmx4096M");
		a.add("-Xms4096M");
		
		List<String> b = new ArrayList<String>();
		b.add("-Xms1024M");
		b.add("-Xmx1024M");
		b.add("-XX:MaxPermSize=256M");

		List<String> properties = node.mergeJVMOptions(a, b);

		Assert.assertTrue(properties.size() == 4);
	}

}
