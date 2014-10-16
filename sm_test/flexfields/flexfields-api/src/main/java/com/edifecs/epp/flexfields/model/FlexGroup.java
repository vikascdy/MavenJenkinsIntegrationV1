package com.edifecs.epp.flexfields.model;

import java.io.Serializable;
import java.util.*;

/**
 * Created by sandeep.kath on 5/8/2014.
 */
public class FlexGroup implements Serializable {
    private static final long serialVersionUID = 986238591153123345L;
    private Long id;
    private String name;
    private String description;
    private String displayName;
    private String tenantName;
    private String appName;
    private String componentName;
    private String permissionRequired;
    private Boolean restricted;
    private String entityName;
    private String namespace;

    public FlexGroup() {
        children = new HashSet<FlexGroup>();
    }

    private Collection<FlexFieldDefinition> flexFieldsCollection;

    private Set<FlexGroup> children;

    public Set<FlexGroup> getChildren() {
        return children;
    }

    public void setChildren(Set<FlexGroup> children) {
        this.children = children;
    }

    public void addChild(FlexGroup node) {
        children.add(node);
    }

    public Collection<FlexFieldDefinition> getFlexFieldsCollection() {
        return flexFieldsCollection;
    }

    public void setFlexFieldsCollection(Collection<FlexFieldDefinition> flexFieldsCollection) {
        this.flexFieldsCollection = flexFieldsCollection;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getPermissionRequired() {
        return permissionRequired;
    }

    public void setPermissionRequired(String permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public Boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

}
