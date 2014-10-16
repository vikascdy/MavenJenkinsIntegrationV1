package com.edifecs.epp.security.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Credential_Type")
@NamedQueries({
		@NamedQuery(name = CredentialTypeEntity.FIND_ALL_CREDENTIAL_TYPES, query = "SELECT ct from Credential_Type as ct"),
		@NamedQuery(name = CredentialTypeEntity.FIND_CREDENTIAL_TYPES_BY_NAME, query = "SELECT ct from Credential_Type as ct WHERE ct.canonicalName=:canonicalName") })
public class CredentialTypeEntity {

	public static final String FIND_ALL_CREDENTIAL_TYPES = "CredentialType.findAll";
	public static final String FIND_CREDENTIAL_TYPES_BY_NAME = "CredentialType.findByName";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Credential_Type_Id", unique = true, nullable = false)
	private Long id;

	@Column(name = "canonical_Name")
	private String canonicalName;

	@Column(name = "identification")
	private Boolean identification;

	@Column(name = "authentication")
	private Boolean authentication;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "Parent_Credential_Type_Id")
	private CredentialTypeEntity parentCredentialType;

	@ElementCollection
	@OneToMany(mappedBy = "parentCredentialType")
	private List<CredentialTypeEntity> childCredentialTypes = new ArrayList<>();

	public List<CredentialTypeEntity> getChildCredentialTypes() {
		return childCredentialTypes;
	}

	public void setChildCredentialTypes(
			List<CredentialTypeEntity> childCredentialTypes) {
		this.childCredentialTypes = childCredentialTypes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CredentialTypeEntity getParentCredentialType() {
		return parentCredentialType;
	}

	public void setParentCredentialType(
			CredentialTypeEntity parentCredentialType) {
		this.parentCredentialType = parentCredentialType;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public Boolean isIdentification() {
		return identification;
	}

	public void setIdentification(Boolean identification) {
		this.identification = identification;
	}

	public boolean isAuthentication() {
		return authentication;
	}

	public void setAuthentication(boolean authentication) {
		this.authentication = authentication;
	}

}
