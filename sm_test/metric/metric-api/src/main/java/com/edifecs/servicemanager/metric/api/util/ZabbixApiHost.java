package com.edifecs.servicemanager.metric.api.util;

import com.edifecs.servicemanager.metric.api.exception.ZabbixApiException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sandeep.kath on 6/16/2014.
 */
public class ZabbixApiHost {
    private ZabbixApi zabbixApi;

    public ZabbixApiHost(ZabbixApi zabbixApi) {
        this.zabbixApi = zabbixApi;
    }

    public String createHost(String hostGroupId, String hostName, String ip, int port) throws ZabbixApiException {
        String hostId = null;
        try {
            JSONObject params = new JSONObject();
            params.put("host", hostName);
            Boolean exists = Boolean.parseBoolean(zabbixApi.getString("host.exists", params));

            if (!exists) {
                params = new JSONObject();

                params.put("host", hostName);
                JSONArray interfaces = new JSONArray();
                JSONObject intrface = new JSONObject();
                intrface.put("ip", ip);
                intrface.put("useip", 1);
                intrface.put("main", 1);
                intrface.put("type", 1);
                intrface.put("port", 10050);
                intrface.put("dns", "");
                interfaces.put(intrface);
                params.put("interfaces", interfaces);

                JSONArray groups = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupid", hostGroupId);
                groups.put(jsonObject);

                params.put("groups", groups);

                JSONObject responseJSON = zabbixApi.getJSONObject("host.create", params);
                hostId = responseJSON.getJSONArray("hostids").getString(0);
            } else {
                hostId = getNamesWithID(new String[]{hostName}).get(hostName).toString();
                addHostToHostGroupIfNotExists(hostGroupId, hostName, hostId);
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        return hostId;
    }

    public Map getNamesWithID(String[] filters) throws ZabbixApiException {
        Map groups = new HashMap<String, String>();

        try {
            JSONObject params = new JSONObject();
            params.put("output", "extend");
            if (filters != null) {
                JSONObject hosts = new JSONObject();
                hosts.put("host", filters);
                params.put("filter", hosts);
            }
            JSONArray responseJSON = zabbixApi.getJSONArray("host.get", params);
            int count = responseJSON.length();
            for (int i = 0; i < count; i++) {
                String hostname = responseJSON.getJSONObject(i).getString("host");
                String host_id = responseJSON.getJSONObject(i).getString("hostid");
                groups.put(hostname, host_id);
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        return groups;
    }

    public Boolean addHostToHostGroupIfNotExists(String hostGroupId, String host, String hostId) throws ZabbixApiException {
        try {
            JSONObject params = new JSONObject();
            params = new JSONObject();
            params.put("output", "extend");
            JSONObject hosts = new JSONObject();
            hosts.put("host", host);
            params.put("filter", hosts);
            params.put("groupids", new JSONArray().put(hostGroupId));

            JSONArray responseJSON = zabbixApi.getJSONArray("host.get", params);
            int count = responseJSON.length();
            if (count == 0) {
                params = new JSONObject();
                JSONArray groups = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupid", hostGroupId);
                groups.put(jsonObject);

                JSONArray hostArr = new JSONArray();
                JSONObject hostObj = new JSONObject();
                hostObj.put("hostid", hostId);
                hostArr.put(hostObj);
                params.put("groups", groups);
                params.put("hosts", hostArr);

                JSONObject resp = zabbixApi.getJSONObject("hostgroup.massadd", params);
                return resp.getJSONArray("groupids").get(0).equals(hostGroupId);
            } else {
                return false;
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }
    }
}
