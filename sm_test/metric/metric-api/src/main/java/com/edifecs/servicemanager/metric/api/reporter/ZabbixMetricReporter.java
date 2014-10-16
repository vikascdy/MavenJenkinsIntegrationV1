package com.edifecs.servicemanager.metric.api.reporter;

import com.codahale.metrics.MetricRegistry;
import com.edifecs.servicemanager.metric.api.exception.MetricException;
import com.edifecs.servicemanager.metric.api.util.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

public class ZabbixMetricReporter extends IMetricReporter implements Closeable {

    private final String zabbixHost ; // configurable
    private final int zabbixServerPort; // configurable
    private final String hostGroup;
    private final int hostPort = 10050;
    private final String zabbixUsername;
    private final String zabbixPassword;
    private MetricZabbixAgent zabbixAgent;
    private MetricZabbixMetricsAgent zabbixMetricsAgent;

    @Deprecated
    public ZabbixMetricReporter(String zabbixHost, int zabbixServerPort, String hostGroup) {
        super();
        this.zabbixHost = zabbixHost;
        this.zabbixServerPort = zabbixServerPort;
        this.hostGroup = hostGroup;
        this.zabbixUsername = "admin";
        this.zabbixPassword = "zabbix";
    }

    public ZabbixMetricReporter(String zabbixHost, int zabbixServerPort, String hostGroup, String zabbixUsername, String zabbixPassword) {
        super();
        this.zabbixHost = zabbixHost;
        this.zabbixServerPort = zabbixServerPort;
        this.hostGroup = hostGroup;
        this.zabbixUsername = zabbixUsername;
        this.zabbixPassword = zabbixPassword;
    }


    public void setup(MetricRegistry metricRegistry) throws MetricException {
        try {
            //setup host
            String postUrl = String.format("http://%s/zabbix/api_jsonrpc.php", zabbixHost);
            ZabbixApi api = new ZabbixApi(postUrl);
            api.auth(zabbixUsername, zabbixPassword);


            ZabbixApiHost hostApi = new ZabbixApiHost(api);
            ZabbixApiHostGroup hostGroupApi = new ZabbixApiHostGroup(api);
            Object hostGrpId = hostGroupApi.getNamesWithID(new String[]{hostGroup}).get(hostGroup);
            if (null == hostGrpId) {
                throw new MetricException("Invalid Host Group : " + hostGroup);
            }
            String hostId = hostApi.createHost(hostGrpId.toString(), InetAddress.getLocalHost().getHostName(),
                    InetAddress.getLocalHost().getHostAddress(), hostPort);

            zabbixAgent = new MetricZabbixAgent();
            zabbixAgent.setEnableActive(true);
            zabbixAgent.setEnablePassive(false);
            zabbixAgent.setHostName(InetAddress.getLocalHost().getHostName());
            zabbixAgent.setServerAddress(InetAddress.getByName(zabbixHost));
            zabbixAgent.setServerPort(zabbixServerPort);

            zabbixMetricsAgent = new MetricZabbixMetricsAgent(metricRegistry,
                    zabbixAgent, api, hostId);


        } catch (Exception e) {
          //  e.printStackTrace();
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
