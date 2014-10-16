package com.edifecs.servicemanager.metric.api.util;

import com.edifecs.servicemanager.metric.api.exception.ZabbixApiException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ti Zhou on 8/31/2014.
 */
public class ZabbixApiTemplate {
    private ZabbixApi zabbixApi;

    public ZabbixApiTemplate(ZabbixApi zabbixApi) {
        this.zabbixApi = zabbixApi;
    }

    public String createTemplate(String hostGroupId, String templateName) throws ZabbixApiException {
        String templateId = null;
        try {
            JSONObject params = new JSONObject();
            params.put("host", templateName);
            Boolean exists = Boolean.parseBoolean(zabbixApi.getString("template.exists", params));

            if (!exists) {
                params = new JSONObject();
                params.put("host", templateName);

                JSONArray groups = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupid", hostGroupId);
                groups.put(jsonObject);

                params.put("groups", groups);

                JSONObject responseJSON = zabbixApi.getJSONObject("template.create", params);
                templateId = responseJSON.getJSONArray("templateids").getString(0);
            } else {
                templateId = getNamesWithID(new String[]{templateName}).get(templateName).toString();
                addTemplateToHostGroupIfNotExists(hostGroupId, templateName, templateId);
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        return templateId;
    }

    public Map getNamesWithID(String[] filters) throws ZabbixApiException {
        Map groups = new HashMap<String, String>();

        try {
            JSONObject params = new JSONObject();
            params.put("output", "extend");
            if (filters != null) {
                JSONObject templates = new JSONObject();
                templates.put("host", filters);
                params.put("filter", templates);
            }
            JSONArray responseJSON = zabbixApi.getJSONArray("template.get", params);
            int count = responseJSON.length();
            for (int i = 0; i < count; i++) {
                String templatename = responseJSON.getJSONObject(i).getString("host");
                String template_id = responseJSON.getJSONObject(i).getString("templateid");
                groups.put(templatename, template_id);
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        return groups;
    }

    public Boolean addTemplateToHostGroupIfNotExists(String hostGroupId, String template, String templateId) throws ZabbixApiException {
        try {
            JSONObject params = new JSONObject();
            params = new JSONObject();
            params.put("output", "extend");
            JSONObject templates = new JSONObject();
            templates.put("host", template);
            params.put("filter", templates);
            params.put("groupids", new JSONArray().put(hostGroupId));

            JSONArray responseJSON = zabbixApi.getJSONArray("template.get", params);
            int count = responseJSON.length();
            if (count == 0) {
                params = new JSONObject();
                JSONArray groups = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("groupid", hostGroupId);
                groups.put(jsonObject);

                JSONArray templateArr = new JSONArray();
                JSONObject templateObj = new JSONObject();
                templateObj.put("templateid", templateId);
                templateArr.put(templateObj);
                params.put("groups", groups);
                params.put("hosts", templateArr);

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
