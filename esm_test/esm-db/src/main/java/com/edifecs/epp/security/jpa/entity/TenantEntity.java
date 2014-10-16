package com.edifecs.epp.security.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Tenant
 */
@Entity(name = "Tenant")
@NamedQueries({
        @NamedQuery(name = TenantEntity.FIND_ALL_TENANTS, query = "SELECT t from Tenant as t"),
        @NamedQuery(name = TenantEntity.FIND_TENANT_BY_NAME, query = "SELECT t from Tenant as t where lower(t.canonicalName) = :name"),
        @NamedQuery(name = TenantEntity.FIND_TENANT_BY_DOMAIN, query = "SELECT t from Tenant as t where t.domain = :domain"),
        @NamedQuery(name = TenantEntity.FIND_ALL_TENANTS_BY_SITE_ID, query = "SELECT t from Site as s join s.tenants t where s.id = :siteId"),
        @NamedQuery(name = TenantEntity.FIND_TENANT_BY_USER_ID, query = "SELECT t from Tenant as t join t" +
                ".organizations o join o.users u where u.id = :userId"),
        @NamedQuery(name = TenantEntity.FIND_TENANT_BY_USER_NAME, query = "SELECT t from Tenant as t join t" +
                ".organizations o join o.users u JOIN u.credentials c where lower(c.credentialKey) = :userName"),
                @NamedQuery(name = TenantEntity.FIND_TENANT_BY_TENANT_USER_NAME, query = "SELECT t from Tenant as t join t" +
                        ".organizations o join o.users u JOIN u.credentials c where t.id = :tenantId AND lower(c.credentialKey) = :userName")})
public class TenantEntity extends AuditEntity {

    public static final String FIND_ALL_TENANTS = "Tenant.findAll";
    public static final String FIND_TENANT_BY_USER_ID = "Tenant.findTenantByUserId";
    public static final String FIND_TENANT_BY_USER_NAME = "Tenant.findTenantByUserName";
    public static final String FIND_TENANT_BY_TENANT_USER_NAME = "Tenant.findTenantByTenantUserName";
    public static final String FIND_TENANT_BY_NAME = "Tenant.findTenantByName";
    public static final String FIND_TENANT_BY_DOMAIN = "Tenant.findTenantByDomain";
    public static final String FIND_ALL_TENANTS_BY_SITE_ID = "Tenant.findAllTenantsBySiteId";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Tenant_Id")
    private Long id;

    @Column(name = "Canonical_Name", unique = true)
    private String canonicalName;

    @Column(name = "Domain", unique = true)
    private String domain;

    @Column(name = "Logo")
    @Lob
    private String logo;

    @Column(name="Landing_Page")
    private String landingPage;

    @Column(name = "Tenant_Description")
    private String description;

    @ManyToOne(optional = false)
    private SiteEntity site;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "Password_Policy_Id")
    private PasswordPolicyEntity passwordPolicy;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<OrganizationEntity> organizations = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<UserGroupEntity> groups = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<RoleEntity> roles = new ArrayList<>();

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

    public SiteEntity getSite() {
        return site;
    }

    public void setSite(SiteEntity site) {
        this.site = site;
    }

    public List<OrganizationEntity> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganizationEntity> organizations) {
        this.organizations = organizations;
    }

    public List<UserGroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroupEntity> groups) {
        this.groups = groups;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PasswordPolicyEntity getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(PasswordPolicyEntity passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }
}
