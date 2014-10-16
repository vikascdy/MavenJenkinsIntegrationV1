package com.edifecs.epp.security.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.edifecs.epp.security.data.RealmType;

@Entity(name = "Security_Realm")
public class SecurityRealmEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "S_Realm_Id")
	private long id;

	@Column(name = "S_Realm_Name", nullable = false)
	private String name;

	@ManyToOne
	private OrganizationEntity organization;

	@Column(name = "S_Realm_Type")
	@Enumerated(EnumType.STRING)
	private RealmType realmType;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<CustomPropertyEntity> properties = new ArrayList<CustomPropertyEntity>();

	@Column(name = "S_Realm_Enabled")
	private boolean enabled;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OrganizationEntity getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationEntity organization) {
		this.organization = organization;
	}

	public RealmType getRealmType() {
		return realmType;
	}

	public void setRealmType(RealmType realmType) {
		this.realmType = realmType;
	}

	public List<CustomPropertyEntity> getProperties() {
		return properties;
	}

	public void setProperties(List<CustomPropertyEntity> properties) {
		this.properties = properties;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
