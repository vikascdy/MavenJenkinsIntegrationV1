package com.edifecs.epp.security.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Region")
@NamedQueries ({
    @NamedQuery(name = RegionEntity.FIND_ALL_REGIONS, query="SELECT r from Region as r")
})
public class RegionEntity {

    public static final String FIND_ALL_REGIONS = "Region.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Region_Code", unique = true, nullable = false)
    private Long code;

    @Column(name = "canonical_Name")
    private String canonicalName;

    @OneToOne(mappedBy = "region", fetch = FetchType.LAZY)
    private AddressEntity address;

    public RegionEntity() {
        super();
        // TODO Auto-generated constructor stub
    }

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
