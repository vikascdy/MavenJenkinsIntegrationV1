package com.edifecs.epp.security.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Country")
@NamedQueries({ @NamedQuery(name = CountryEntity.FIND_ALL_COUNTRIES, query = "SELECT co from Country as co") })
public class CountryEntity {

	public static final String FIND_ALL_COUNTRIES = "Country.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Country_Code", unique = true, nullable = false)
	private Long code;

	@Column(name = "canonical_Name")
	private String canonicalName;

	@OneToOne(mappedBy = "country")
	private AddressEntity address;

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
