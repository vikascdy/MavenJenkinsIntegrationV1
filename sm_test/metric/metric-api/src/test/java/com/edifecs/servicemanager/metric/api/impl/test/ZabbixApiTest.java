package com.edifecs.servicemanager.metric.api.impl.test;

import com.edifecs.servicemanager.metric.api.exception.ZabbixApiException;
import com.edifecs.servicemanager.metric.api.exception.ZabbixItemAlreadyExistsException;
import com.edifecs.servicemanager.metric.api.util.ZabbixApi;
import com.edifecs.servicemanager.metric.api.util.ZabbixApiHost;
import com.edifecs.servicemanager.metric.api.util.ZabbixApiHostGroup;
import com.edifecs.servicemanager.metric.api.util.ZabbixApiItem;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by sandeep.kath on 6/16/2014.
 */
public class ZabbixApiTest {

    static String hostGroupName = "SM Test Hosts";
    private static String hostname;
    private static String hostIp;
    private static int hostPort = 10050;
    private String zabbixHost;

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            hostIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Hostname : " + hostname + " Host IP : " + hostIp);
        } catch (Exception e) {
            throw new RuntimeException("Error getting hostname/ip");
        }

    }

    ZabbixApi zabbixApi = new ZabbixApi("http://zabbix01/zabbix/api_jsonrpc.php");

    @Before
    public void setUp() throws Exception {
        zabbixApi.auth("admin", "zabbix");
    }


    @Test
    public void getHostGroupTest() {
        try {
            ZabbixApiHostGroup hostgroup = new ZabbixApiHostGroup(zabbixApi);
            String[] hosts = {hostGroupName};
            Map groups = hostgroup.getNamesWithID(hosts);
            Iterator entries = groups.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (ZabbixApiException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void createHostGroupTest() {
        try {
            ZabbixApiHostGroup hostgroup = new ZabbixApiHostGroup(zabbixApi);
            String hostId = hostgroup.createHostGroup(hostGroupName);
            System.out.println("Host Group ID " + hostId);
            assertNotNull(hostId);
        } catch (ZabbixApiException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void createHostTest() {
        try {
            System.out.println("Hostname : " + hostname + " Host IP : " + hostIp);
            ZabbixApiHostGroup hostgroup = new ZabbixApiHostGroup(zabbixApi);
            String hostGrpId = hostgroup.createHostGroup(hostGroupName);
            System.out.println("Host Grp ID ^ " + hostGrpId);
            assertNotNull(hostGrpId);

            ZabbixApiHost host = new ZabbixApiHost(zabbixApi);
            String hostId = host.createHost(hostGrpId, hostname, hostIp, hostPort);
            System.out.println("Host ID " + hostId);
            assertNotNull(hostId);

        } catch (ZabbixApiException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void getHostTest() {
        try {
            ZabbixApiHost host = new ZabbixApiHost(zabbixApi);
            String[] hosts = {hostname};
            Map zHosts = host.getNamesWithID(hosts);
            Iterator entries = zHosts.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (ZabbixApiException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void createItemTest() {
        try {
            ZabbixApiHostGroup hostgroup = new ZabbixApiHostGroup(zabbixApi);
            String hostGrpId = hostgroup.createHostGroup(hostGroupName);
            System.out.println("Host Grp ID ^ " + hostGrpId);
            assertNotNull(hostGrpId);

            ZabbixApiHost host = new ZabbixApiHost(zabbixApi);
            String hostId = host.createHost(hostGrpId, hostname, hostIp, hostPort);
            System.out.println("Host ID " + hostId);
            assertNotNull(hostId);

            ZabbixApiItem item = new ZabbixApiItem(zabbixApi);
            item.addItem("Test", "Description", "key", hostId, "units", 30);
        } catch (ZabbixItemAlreadyExistsException e) {
            // true
        } catch (ZabbixApiException e) {

            fail(e.getMessage());
        }

    }




}