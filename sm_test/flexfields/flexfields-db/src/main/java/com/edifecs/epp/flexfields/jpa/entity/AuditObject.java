package com.edifecs.epp.flexfields.jpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Calendar;

/**
 * Created by sandeep.kath on 5/12/2014.
 */

@MappedSuperclass
public class AuditObject {
    @Column
    protected String auditUser;

    @Column
    protected Calendar auditTimestamp;

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public Calendar getAuditTimestamp() {
        return auditTimestamp;
    }

    public void setAuditTimestamp(Calendar auditTimestamp) {
        this.auditTimestamp = auditTimestamp;
    }

    @PrePersist
    @PreUpdate
    public void updateAuditInfo() {
        setAuditUser((String) ""); //TODO
        setAuditTimestamp(Calendar.getInstance());
    }

}

