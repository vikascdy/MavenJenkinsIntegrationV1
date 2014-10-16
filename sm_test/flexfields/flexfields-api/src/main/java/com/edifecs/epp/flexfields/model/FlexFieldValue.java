package com.edifecs.epp.flexfields.model;

import java.io.Serializable;

/**
 * Created by sandeep.kath on 5/2/2014.
 */
public class FlexFieldValue implements Serializable {
    private Long id;

    private String entityName;

    private long entityID;

    private Long flexFieldDefinitionId;

    private Long flexGroupId;

    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public long getEntityID() {
        return entityID;
    }

    public void setEntityID(long entityID) {
        this.entityID = entityID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getFlexFieldDefinitionId() {
        return flexFieldDefinitionId;
    }

    public void setFlexFieldDefinitionId(Long flexFieldDefinitionId) {
        this.flexFieldDefinitionId = flexFieldDefinitionId;
    }

    public Long getFlexGroupId() {
        return flexGroupId;
    }

    public void setFlexGroupId(Long flexGroupId) {
        this.flexGroupId = flexGroupId;
    }
}
