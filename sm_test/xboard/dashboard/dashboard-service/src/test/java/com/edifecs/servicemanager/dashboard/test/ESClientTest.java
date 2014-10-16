package com.edifecs.servicemanager.dashboard.test;

import org.junit.Test;

import com.edifecs.servicemanager.dashboard.service.ESClient;

public class ESClientTest {

	@Test
	public void test() throws Exception {

		ESClient esClient = new ESClient();
		esClient.setClusterName("elasticsearch");
		esClient.setHost("127.0.0.1");
		esClient.setPort(9300);

		System.out.println("connected : " + esClient.testConnection());

		esClient.shutdown();
	}

}
