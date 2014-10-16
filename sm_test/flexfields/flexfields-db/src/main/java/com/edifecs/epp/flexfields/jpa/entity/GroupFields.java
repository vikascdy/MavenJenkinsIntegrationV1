package com.edifecs.epp.flexfields.jpa.entity;

import javax.persistence.*;

/**
 * Created by sandeep.kath on 5/6/2014.
 */
@Entity
@Table(name="Group_Fields", uniqueConstraints=
@UniqueConstraint(columnNames = {"field_id", "group_id"}) )

public class GroupFields extends AuditObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private FlexFieldDefinitionEntity field;
    @ManyToOne
    private FlexGroupEntity group;

    private Integer sequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FlexFieldDefinitionEntity getField() {
        return field;
    }

    public void setField(FlexFieldDefinitionEntity field) {
        this.field = field;
    }

    public FlexGroupEntity getGroup() {
        return group;
    }

    public void setGroup(FlexGroupEntity group) {
        this.group = group;
    }


    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }


}
