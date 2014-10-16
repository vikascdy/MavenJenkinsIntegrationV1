package com.edifecs.servicemanager.metric.api.util;

import com.edifecs.servicemanager.metric.api.exception.ZabbixApiException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sandeep.kath on 6/16/2014.
 */
public class ZabbixApi {
    private String url;
    private String authId;

    public ZabbixApi(String url) {
        this.url = url;
    }

    public void auth(String user, String password) throws ZabbixApiException {
        authId = null;

        JSONObject params = new JSONObject();
        try {
            params.put("user", user);
            params.put("password", password);
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        authId = getString("user.authenticate", params);
    }

    public String getString(String method, JSONObject params) throws ZabbixApiException {
        JSONObject responseJson = callApi(method, params);
        try {
            return responseJson.getString("result");
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }
    }

    public JSONArray getJSONArray(String method, JSONObject params) throws ZabbixApiException {
        JSONObject responseJson = callApi(method, params);

        try {
            return responseJson.getJSONArray("result");
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }
    }

    public JSONObject getJSONObject(String method, JSONObject params) throws ZabbixApiException {
        JSONObject responseJson = callApi(method, params);

        try {
            return responseJson.getJSONObject("result");
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }
    }

    public JSONObject callApi(String method, JSONObject params) throws ZabbixApiException {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("auth", authId);
            requestJson.put("method", method);
            requestJson.put("id", 1);
            requestJson.put("jsonrpc", "2.0");
            requestJson.put("params", params);
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }

        // HTTP POST
        HttpResponse httpResponse;
        HttpPost httpPost = new HttpPost(this.url);
        String responseBody;
        try {
            httpPost.setHeader("Content-Type", "application/json-rpc");
            httpPost.setHeader("User-Agent", "Zabbitan Widget");
            httpPost.setEntity(new StringEntity(requestJson.toString()));

            DefaultHttpClient client = new DefaultHttpClient();
            httpResponse = client.execute(httpPost);
            responseBody = EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            throw new ZabbixApiException("HTTP Request Error");
        }

        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new ZabbixApiException("HTTP Error : " + responseBody);
        }

        JSONObject responseJson;
        try {
            responseJson = new JSONObject(responseBody);
        } catch (Exception e) {
            throw new ZabbixApiException(e.getMessage());
        }


        if (responseJson.has("error")) {
            String message;
            try {
                message = "API Error : " + responseJson.getJSONObject("error").toString();
            } catch (JSONException e) {
                throw new ZabbixApiException(e.getMessage());
            }
            message += "\nRequest:" + requestJson.toString();
            throw new ZabbixApiException(message);
        }

        return responseJson;
    }

}
