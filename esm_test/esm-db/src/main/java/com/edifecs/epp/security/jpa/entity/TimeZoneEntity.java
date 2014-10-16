package com.edifecs.epp.security.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity(name = "TimeZone")
@NamedQueries ({
    @NamedQuery(name = TimeZoneEntity.FIND_ALL_TIMEZONES, query="SELECT t from TimeZone as t")
})
public class TimeZoneEntity {

    public static final String FIND_ALL_TIMEZONES = "TimeZone.findAll";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TimeZone_Code", unique = true, nullable = false)
    private Long code;

    @Column(name = "canonical_Name")
    private String canonicalName;

    @OneToOne(mappedBy = "preferredTimezone")
    private ContactEntity contact;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
