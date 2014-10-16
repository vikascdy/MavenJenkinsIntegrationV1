package com.edifecs.epp.security.jpa.entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.util.Date;

@MappedSuperclass
public class AuditEntity {

    @OneToOne
    @JoinColumn(name = "Created_User_Id")
    private UserEntity createdBy;

    @OneToOne
    @JoinColumn(name = "Updated_User_Id")
    private UserEntity lastUpdatedBy;

    @Column(name = "Created_Date_Time")
    private Date creationDate;

    @Column(name = "Updated_Date_Time")
    private Date lastUpdatedDate;

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public UserEntity getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(UserEntity lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
