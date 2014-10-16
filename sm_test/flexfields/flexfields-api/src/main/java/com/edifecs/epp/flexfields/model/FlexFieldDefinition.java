package com.edifecs.epp.flexfields.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sandeep.kath on 5/1/2014.
 */
public class FlexFieldDefinition implements Serializable {
    private static final long serialVersionUID = 98648591153123345L;
    private Long id;

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    private Boolean activeFlag;
    private FieldType dataType;
    private String defaultValue;
    private String description;
    private String displayName;
    private Integer fieldSize;
    private String name;
    private String precisionValue;
    private String regEx;
    private Boolean restricted;
    private Boolean required;
    private String namespace;
    private String requiredPermission;
    private String validationMessage;


    private FlexFieldValue flexFieldValue;

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Map getSelectOptions() {
        return selectOptions;
    }

    public void setSelectOptions(Map selectOptions) {
        this.selectOptions = selectOptions;
    }

    private Map selectOptions;

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FieldType getDataType() {
        return dataType;
    }

    public void setDataType(FieldType dataType) {
        this.dataType = dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public Integer getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(Integer fieldSize) {
        this.fieldSize = fieldSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrecisionValue() {
        return precisionValue;
    }

    public void setPrecisionValue(String precisionValue) {
        this.precisionValue = precisionValue;
    }

    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    public Boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public FlexFieldValue getFlexFieldValue() {
        return flexFieldValue;
    }

    public void setFlexFieldValue(FlexFieldValue flexFieldValue) {
        this.flexFieldValue = flexFieldValue;
    }
}
