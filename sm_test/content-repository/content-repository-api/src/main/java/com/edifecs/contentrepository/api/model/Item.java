package com.edifecs.contentrepository.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abhising on 29-07-2014.
 */
public class Item implements Serializable {

    private String id;

    private String name;

    private String itemType;

    private String description;

    private Map<String, Object> properties = new HashMap<>();

    private Map<String, Collection<Item>> associations = new HashMap<>();

    private String version;

    private String createdBy;

    private Date dateCreated;

    private String modifiedBy;

    private Date dateModified;

    public Item() {
    }

    public Item(String name, String itemType, String description, Map<String, Object> properties, Map<String,
            Collection<Item>> associations) {
        this.name = name;
        this.itemType = itemType;
        this.description = description;
        this.properties = properties;
        this.associations = associations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Collection<Item>> getAssociations() {
        return associations;
    }

    public void setAssociations(Map<String, Collection<Item>> associations) {
        this.associations = associations;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Item otherItem = (Item) object;
            if (this.getId().equals(otherItem.getId()) && this.getName().equalsIgnoreCase(otherItem.getName()) &&
                    this.getItemType().equalsIgnoreCase(otherItem.getItemType())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 21;
        hash += this.getId().hashCode();
        return hash;
    }
}
