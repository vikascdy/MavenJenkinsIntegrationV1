package com.edifecs.epp.flexfields.jpa.entity;

import javax.persistence.*;
import java.lang.Long;
import java.lang.String;
import java.util.Set;

@Entity
@Table(name = "FlexGroup")
@NamedQueries({
        @NamedQuery(name = FlexGroupEntity.FIND_ALL_FLEX_GROUPS, query = "SELECT flexGroupEntity from FlexGroupEntity as flexGroupEntity")
})

public class FlexGroupEntity extends AuditObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    long version;

    @Column(nullable=false)
    private String name;

    private String namespace;

    private String description;

    @Column(nullable = false)
    private String displayName;

    private String tenantName;
    private String appName;
    private String componentName;
    private String permissionRequired;
    private Boolean restricted;
    @Column(nullable=false)
    private String entityName;

    public static final String FIND_ALL_FLEX_GROUPS = "FlexGroupEntity.findAll";

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

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Set<GroupFields> getGroupFields() {
        return groupFields;
    }

    public void setGroupFields(Set<GroupFields> groupFields) {
        this.groupFields = groupFields;
    }



    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<GroupFields> groupFields;

    @ManyToOne
    private FlexGroupEntity parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<FlexGroupEntity> children;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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

    public Set<FlexGroupEntity> getChildren() {
        return children;
    }

    public void setChildren(Set<FlexGroupEntity> children) {
        this.children = children;
    }

    public FlexGroupEntity getParent() {
        return parent;
    }

    public void setParent(FlexGroupEntity parent) {
        this.parent = parent;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

}