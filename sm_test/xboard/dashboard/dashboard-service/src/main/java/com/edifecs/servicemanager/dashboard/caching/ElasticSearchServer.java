package com.edifecs.servicemanager.dashboard.caching;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.node.internal.InternalSettingsPerparer;
import org.elasticsearch.plugins.PluginManager;

import com.edifecs.core.configuration.helper.SystemVariables;

public class ElasticSearchServer {

	private static final String CLUSTER_NAME = "edifecs-es";

	private final Node node;
	private final String dataDirectory;
	private Client client;

	ImmutableSettings.Builder elasticsearchSettings;

	public ElasticSearchServer() {

		String data = SystemVariables.TEMP_PATH + "ES-Data";
		this.dataDirectory = data;

		System.out.println("Set data path for elastic search : " + data);

		// TODO : confiugre path.conf to SM/dist
		elasticsearchSettings = ImmutableSettings.settingsBuilder()
				.put("http.enabled", "true").put("cluster.name", CLUSTER_NAME)
				.put("node.name", "es-node-1").put("index.number_of_shards", 1)
				.put("index.number_of_replicas", 0)
				.put("path.data", dataDirectory);

		initPlugins();

		node = NodeBuilder.nodeBuilder().loadConfigSettings(false)
				.settings(elasticsearchSettings.build()).node();
		client = node.client();

		waitUntilReady();
	}

	private void initPlugins() {

		// head
		String HEAD_PLUGIN = "mobz/elasticsearch-head";

		Tuple<Settings, Environment> initialSettings = InternalSettingsPerparer
				.prepareSettings(elasticsearchSettings.build(), true);

		if (!initialSettings.v2().pluginsFile().exists()) {
			FileSystemUtils.mkdirs(initialSettings.v2().pluginsFile());
		}

		String url = null;
		PluginManager pluginManager = new PluginManager(initialSettings.v2(),
				url);

		//TODO : check if exists before downloading.
		try {
			pluginManager.downloadAndExtract(HEAD_PLUGIN, true);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Head Plugin Already installed ");
		}

	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void shutdown() {
		node.close();
	}

	public void waitUntilReady() {
		int counter = 0;
		ClusterHealthStatus status = ClusterHealthStatus.RED;

		System.out.println("Waiting for elasticsearch cluster status...");

		while (ClusterHealthStatus.RED.equals(status) && counter < 10) {
			if (counter > 0) {
				System.out.println("Elasticsearch cluster status is "
						+ status.name() + " still waiting...");
			}

			ClusterHealthResponse response = client.admin().cluster()
					.prepareHealth().setWaitForYellowStatus().execute()
					.actionGet();
			status = response.getStatus();
			counter++;
		}

		if (ClusterHealthStatus.RED.equals(status)) {
			System.out.println("Elasticsearch cluster timed out, status: "
					+ status.name());
		} else {
			System.out.println("Elasticsearch cluster ready");
		}
	}
}
