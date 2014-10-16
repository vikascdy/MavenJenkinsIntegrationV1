package com.edifecs.epp.flexfields.jpa.entity;

import javax.persistence.*;

/**
 * Created by sandeep.kath on 5/5/2014.
 */

@Entity
public class FlexFieldValueEntity extends AuditObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    long version;

    @OneToOne(cascade={CascadeType.ALL})
    public GroupFields getGroupField() {
        return groupField;
    }

    public void setGroupField(GroupFields groupField) {
        this.groupField = groupField;
    }

    @ManyToOne(optional = false)
    private GroupFields groupField;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable=false)
    private long entityID;

    private String value;

    public long getEntityID() {
        return entityID;
    }

    public void setEntityID(long entityID) {
        this.entityID = entityID;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }


}

