package com.edifecs.epp.security.jpa.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Site")
@NamedQueries(
	@NamedQuery(name = SiteEntity.FIND_ALL_SITES, query = "SELECT s from Site as s")
)
public class SiteEntity extends AuditEntity {

	public static final String FIND_ALL_SITES = "Site.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Site_Id")
	private Long id;

	@Column(name = "Canonical_Name")
	private String canonicalName;

	@Column(name = "Domain")
	private String domain;

	@Column(name = "Environment")
	private String environment;

	@Column(name = "Site_Description", nullable = true)
	private String description;

	@OneToMany(mappedBy = "site", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<TenantEntity> tenants = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public List<TenantEntity> getTenants() {
		return tenants;
	}

	public void setTenants(List<TenantEntity> tenants) {
		this.tenants = tenants;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
