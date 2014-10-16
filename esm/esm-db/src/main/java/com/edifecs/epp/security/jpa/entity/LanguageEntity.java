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

@Entity(name="Language")
@NamedQueries ({ @NamedQuery(name = LanguageEntity.FIND_ALL_LANGUAGES, query="SELECT l from Language as l") })
public class LanguageEntity {
	
	public static final String FIND_ALL_LANGUAGES = "Language.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Language_Code", unique = true, nullable = false)
	private Long code;

	@Column(name = "Canonical_Name")
	private String canonicalName;

	@OneToOne(mappedBy = "preferredLanguage")
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
