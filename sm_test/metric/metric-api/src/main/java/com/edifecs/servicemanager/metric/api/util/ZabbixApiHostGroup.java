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
public class ZabbixApiHostGroup {
    private ZabbixApi zabbixApi;

    public ZabbixApiHostGroup(ZabbixApi zabbixApi) {
        this.zabbixApi = zabbixApi;
    }

    public Map getNamesWithID(String[] filters) throws ZabbixApiException {
        Map groups = new HashMap<String, String>();

        try {
            JSONObject params = new JSONObject();
            params.put("output", "extend");
            if (filters != null) {
                JSONObject hosts = new JSONObject();
                hosts.put("name", filters);
                params.put("filter", hosts);
            }
            JSONArray responseJSON = zabbixApi.getJSONArray("hostgroup.get", params);
            int count = responseJSON.length();
            for (int i = 0; i < count; i++) {
                String hostGrpname = responseJSON.getJSONObject(i).getString("name");
                String hostGrpId = responseJSON.getJSONObject(i).getString("groupid");
                groups.put(hostGrpname, hostGrpId);
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        return groups;
    }

    public String createHostGroup(String hostGroup) throws ZabbixApiException {
        String hostGroupId = null;
        try {
            JSONObject params = new JSONObject();
            params.put("name", hostGroup);
            Boolean exists = Boolean.parseBoolean(zabbixApi.getString("hostgroup.exists", params));

            if (!exists) {
                params = new JSONObject();
                params.put("name", hostGroup);
                JSONObject responseJSON = zabbixApi.getJSONObject("hostgroup.create", params);
                hostGroupId = responseJSON.getJSONArray("groupids").getString(0);
            } else {
                hostGroupId = getNamesWithID(new String[]{hostGroup}).get(hostGroup).toString();
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        return hostGroupId;
    }
}