package com.edifecs.servicemanager.metric.api.reporter;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

import com.codahale.metrics.MetricRegistry;
import com.edifecs.servicemanager.metric.api.exception.MetricException;
import com.edifecs.servicemanager.metric.api.util.*;

public class ZabbixDockerMetricReporter extends IMetricReporter implements Closeable {

    //TODO: how to externalize this?
    private static final String USER_NAME = "admin";
    private static final String PASSWD = "zabbix";
    private final String zabbixHost; // configurable
    private final int zabbixWebPort;  //configurable
    private final int zabbixServerPort; // configurable
    private final String hostGroup;
    private final int hostPort = 10050;
    private MetricZabbixAgent zabbixAgent;
    private MetricZabbixMetricsAgent zabbixMetricsAgent;

    public ZabbixDockerMetricReporter(String zabbixHost, int zabbixWebPort, int zabbixServerPort, String hostGroup) {
        super();
        this.zabbixHost = zabbixHost;
        this.zabbixServerPort = zabbixServerPort;
        this.zabbixWebPort = zabbixWebPort;
        this.hostGroup = hostGroup;
    }

    public void setup(MetricRegistry metricRegistry) throws MetricException {
        try {
            //setup host
            String postUrl = String.format("http://%s:%s/api_jsonrpc.php", zabbixHost, zabbixWebPort);
            ZabbixApi api = new ZabbixApi(postUrl);
            api.auth("Admin", "zabbix");
            ZabbixApiHost hostApi = new ZabbixApiHost(api);
            ZabbixApiTemplate templateApi = new ZabbixApiTemplate(api);
            ZabbixApiHostGroup hostGroupApi = new ZabbixApiHostGroup(api);
            hostGroupApi.createHostGroup("ALL.cart.zabbix-test-tool.0.0.1.ALL");
            Object hostGrpId = hostGroupApi.getNamesWithID(new String[]{hostGroup}).get(hostGroup);
            if (null == hostGrpId) {
                throw new MetricException("Invalid Host Group : " + hostGroup);
            }
            //String hostId = hostApi.createHost(hostGrpId.toString(), "zabbix-test-tool", InetAddress.getLocalHost().getHostAddress(), hostPort);
            String hostId = hostApi.createHost(hostGrpId.toString(), "zabbix-test-tool", "127.0.0.1", 10050);
            String templateId = templateApi.createTemplate(hostGrpId.toString(), "zabbix-test-tool.Template");

            zabbixAgent = new MetricZabbixAgent();
            zabbixAgent.setEnableActive(true);
            zabbixAgent.setEnablePassive(false);
            zabbixAgent.setHostName("zabbix-test-tool");
            zabbixAgent.setServerAddress(InetAddress.getByName(zabbixHost));
            zabbixAgent.setServerPort(zabbixServerPort);

            //TODO this one might need to use the template id instead later.
            zabbixMetricsAgent = new MetricZabbixMetricsAgent(metricRegistry,
                    zabbixAgent, api, hostId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MetricException(e);
        }
    }

    public void start() throws MetricException {
        try {
            zabbixMetricsAgent.start();
            zabbixAgent.start();
        } catch (Exception e) {
            throw new MetricException("Unable to start Zabbix reporter", e);
        }
    }

    public void stop() throws MetricException {
        try {
            zabbixMetricsAgent.stop();
            zabbixAgent.stop();
        } catch (Exception e) {
            throw new MetricException("Unable to stop Zabbix reporter", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            stop();
        } catch (MetricException e) {
            // FIXME
            throw new IOException(e);
        }

    }
}
