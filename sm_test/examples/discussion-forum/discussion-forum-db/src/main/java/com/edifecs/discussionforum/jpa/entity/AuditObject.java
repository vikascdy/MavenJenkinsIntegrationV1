package com.edifecs.discussionforum.jpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Calendar;

/**
 * Created by sandeep.kath on 5/20/2014.
 */
@MappedSuperclass
public class AuditObject {
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    @Column
    protected String createdBy;

    @Column
    protected Calendar creationDate;


    @PrePersist
    @PreUpdate
    public void updateAuditInfo() {
        setCreationDate(Calendar.getInstance());
    }

}
