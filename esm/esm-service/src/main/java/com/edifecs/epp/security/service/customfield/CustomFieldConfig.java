package com.edifecs.epp.security.service.customfield;

import com.edifecs.epp.security.data.EventType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomFieldConfig {
    private final List<CustomFieldDef> customFieldDefs = new ArrayList<>();
    private final ConcurrentHashMap<String, Map<EventType, ActionType>> eventActionMap = new ConcurrentHashMap<>();

    public void addCustomFieldDef(CustomFieldDef def) {
        this.customFieldDefs.add(def);
        updateEventActionMap(def);
    }

    public List<CustomFieldDef> getCustomFieldDefs() {
        return Collections.unmodifiableList(customFieldDefs);
    }

    public Map<String, Map<EventType, ActionType>> getEventActionMap() {
        return Collections.unmodifiableMap(eventActionMap);
    }

    public void setCustomFieldDefs(List<CustomFieldDef> customFieldDefs) {
        this.customFieldDefs.clear();
        if (customFieldDefs != null) {
            this.customFieldDefs.addAll(customFieldDefs);
            eventActionMap.clear();
        }
        init();
    }

    public void init() {
        for (CustomFieldDef def : customFieldDefs) {
            def.init();
            updateEventActionMap(def);
        }
    }

    private void updateEventActionMap(CustomFieldDef def) {
        if (def.getName() == null || def.getNamespace() == null || def.getMatcher() == null) {
            throw new IllegalArgumentException();
        }
        Map<EventType, ActionType> newMap = new Hashtable<>();
        Map<EventType, ActionType> map = eventActionMap.putIfAbsent(def.getNamespace(), newMap);
        map = map == null ? newMap : map;
        map.putAll(def.getEventActionMap());
    }
}
