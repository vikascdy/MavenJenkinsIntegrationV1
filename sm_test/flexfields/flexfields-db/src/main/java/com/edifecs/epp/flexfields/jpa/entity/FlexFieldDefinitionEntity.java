package com.edifecs.epp.flexfields.jpa.entity;

import com.edifecs.epp.flexfields.model.FieldType;

import javax.persistence.*;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Map;
import java.util.Set;

/**
 * Created by sandeep.kath on 5/4/2014.
 */

@Entity
@Table(name = "FlexFieldDefinition")
@NamedQueries({
        @NamedQuery(name = FlexFieldDefinitionEntity.FIND_ALL_FLEXFIELD_DEFINITIONS, query = "SELECT flexFieldDefEntity from FlexFieldDefinitionEntity as flexFieldDefEntity")
})

public class FlexFieldDefinitionEntity extends AuditObject {
    public static final String FIND_ALL_FLEXFIELD_DEFINITIONS = "FlexFieldDefinitionEntity.findAll";

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public FieldType getDataType() {
        return dataType;
    }

    public void setDataType(FieldType dataType) {
        this.dataType = dataType;
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(Integer fieldSize) {
        this.fieldSize = fieldSize;
    }

    public String getPrecisionValue() {
        return precisionValue;
    }

    public void setPrecisionValue(String precisionValue) {
        this.precisionValue = precisionValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    long version;

    @Column(unique = false, nullable = false)  //TODO: Update unique constraint
    private String name;


    private String namespace="";

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    private FieldType dataType;

    private Boolean restricted;
    private String defaultValue;
    private Integer fieldSize;
    private String precisionValue;
    private String description;

    private Boolean activeFlag;
    private String regEx;
    private String validationMessage;

    private Boolean required;

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    private String requiredPermission;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private Set<GroupFields> groupFields;

    public Map<String,String> getSelectOptions() {
        return selectOptions;
    }

    public void setSelectOptions(Map selectOptions) {
        this.selectOptions = selectOptions;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="selectOption")
    private Map<String,String> selectOptions;//  = new HashMap<String,String>();

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

}
