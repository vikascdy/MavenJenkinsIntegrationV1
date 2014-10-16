package com.edifecs.servicemanager.metric.api.util;

import com.edifecs.servicemanager.metric.api.exception.ZabbixApiException;
import com.edifecs.servicemanager.metric.api.exception.ZabbixItemAlreadyExistsException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

import static java.util.Map.Entry;

/**
 * Created by sandeep.kath on 6/16/2014.
 */
public class ZabbixApiItem {

    private static final Logger logger = LoggerFactory.getLogger(ZabbixApiItem.class);
    //    private static List<Item> itemsToAdd = Collections.synchronizedList(new ArrayList<Item>());
    private static Map<Item, String> itemsToAdd = new ConcurrentHashMap<Item, String>();
    private final int INITIAL_DELAY = 5000;
    private final int PERIODIC_INTERVAL = 60000;
    private ZabbixApi zabbixApi;

    public ZabbixApiItem(ZabbixApi zabbixApi) {
        this.zabbixApi = zabbixApi;
        initBatchAdd();
    }

    private void initBatchAdd() {
        ScheduledExecutorService sExecutorService = Executors.newScheduledThreadPool(2);
        sExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                for(Entry<Item, String> entry : itemsToAdd.entrySet()) {
                    try {
                        createItem(entry.getKey());
                        itemsToAdd.remove(entry.getKey());
                    } catch (Exception e) {
                        logger.debug("error adding item, to zabbix server.", e);
                    }
                }
            }
        }, INITIAL_DELAY, PERIODIC_INTERVAL, TimeUnit.MILLISECONDS);

    }

    public String createItem(Item item) throws ZabbixApiException {
        String itemId = null;
        try {
            JSONObject params = new JSONObject();
            params.put("hostid", item.hostid);
            params.put("key_", item.key_);
            Boolean exists = Boolean.parseBoolean(zabbixApi.getString("item.exists", params));

            if (!exists) {
                params.put("description", item.description);
                params.put("key_", item.key_);
                params.put("name", item.name + " " + item.description); // temp
                params.put("delay", item.delay); //update interval in sec
                params.put("hostid", item.hostid);
                params.put("type", 7); // 0 - zabbix agent(active)
                params.put("value_type", 0); // 0 - Numeric Float
                params.put("units", item.units);


                JSONObject responseJSON = zabbixApi.getJSONObject("item.create", params);
                itemId = responseJSON.getJSONArray("itemids").getString(0);
            } else {
                throw new ZabbixItemAlreadyExistsException("Item [key_ : " + item.key_ + "] already exists");
            }
        } catch (JSONException e) {
            throw new ZabbixApiException(e.getMessage());
        }
        return itemId;
    }

    public void addItem(String name, String description, String key, String hostid,
                        String units, int delay) throws ZabbixApiException {
        itemsToAdd.put(new Item(name, key, description, units, hostid, delay), "");
    }

    protected static class Item {
        private String name;
        private String key_;
        private String description;
        private String units;
        private String hostid;
        private int delay;

        public Item(String name, String key_, String description, String units, String hostid, int delay) {
            this.name = name;
            this.key_ = key_;
            this.description = description;
            this.units = units;
            this.hostid = hostid;
            this.delay = delay;
        }
    }
}
