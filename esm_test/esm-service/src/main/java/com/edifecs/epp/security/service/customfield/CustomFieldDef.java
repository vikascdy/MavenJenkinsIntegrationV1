package com.edifecs.epp.security.service.customfield;

import com.edifecs.epp.security.data.EventType;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class CustomFieldDef {
    private String namespace;
    private String ownerEntity;
    private String name;
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private final List<String> eventConfigs = new ArrayList<>();
    private String matcher;
    private String expression;
    private Map<EventType, ActionType> eventActionMap = new Hashtable<>();

    public Map<EventType, ActionType> getEventActionMap() {
        return Collections.unmodifiableMap(eventActionMap);
    }

    public List<String> getEventConfigs() {
        return Collections.unmodifiableList(eventConfigs);
    }

    public String getExpression() {
        return expression;
    }

    public String getMatcher() {
        return matcher;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getOwnerEntity() {
        return ownerEntity;
    }

    public void setEventConfigs(List<String> eventConfigs) {
        this.eventConfigs.clear();
        if (eventConfigs != null) {
            this.eventConfigs.addAll(eventConfigs);
            eventActionMap.clear();
            init();
        }
    }

    public void init() {
        for (String eventConfig : eventConfigs) {
            updateEventActionMap(eventConfig);
        }
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setOwnerEntity(String ownerEntity) {
        this.ownerEntity = ownerEntity;
    }

    private void updateEventActionMap(String eventConfig) {
        // any invalid event config is forgiven
        EventType et = null;
        ActionType at = null;
        if (!StringUtils.isBlank(eventConfig)) {
            String[] eventAction = eventConfig.split("\\.");
            if (eventAction.length == 2) {
                et = EventType.valueOf(eventAction[0]);
                at = ActionType.valueOf(eventAction[1]);
                eventActionMap.put(et, at);
                return;
            }
        }

        throw new IllegalArgumentException("Invalid event config: " + eventConfig);
    }
}
