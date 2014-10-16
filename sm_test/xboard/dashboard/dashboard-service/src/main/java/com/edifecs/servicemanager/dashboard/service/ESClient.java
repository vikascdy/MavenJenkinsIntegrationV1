package com.edifecs.servicemanager.dashboard.service;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.edifecs.servicemanager.dashboard.XBoardException;

public class ESClient {

	private static final String NODE_NAME = "es-river-node";
	private Client client = null;
	private String clusterName;
	private String host;
	private int port;

	public Client getInstance() throws XBoardException {

		if (client == null) {
			return createConnection();
		}
		return client;
	}

	public boolean testConnection() throws XBoardException {

		client = createConnection();
		if (null != client) {
			shutdown();
			return true;
		}
		return false;
	}

	private Client createConnection() throws XBoardException {

		ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings
				.settingsBuilder().put("node.name", NODE_NAME)
				.put("cluster.name", clusterName).put("node.data", false)
				.put("client.transport.sniff", false);
		try {
			Client client = new TransportClient(elasticsearchSettings)
					.addTransportAddress(new InetSocketTransportAddress(host,
							port));

			System.out.println("sucessfully connected to es server : client : "
					+ client.toString());
			return client;

		} catch (Exception e) {
			e.printStackTrace();
			throw new XBoardException("Unable to connect to the Server : "
					+ host + "@" + port);
		}

	}

	public void shutdown() {
		if (null != client) {
			client.close();
			client = null;
		}
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
