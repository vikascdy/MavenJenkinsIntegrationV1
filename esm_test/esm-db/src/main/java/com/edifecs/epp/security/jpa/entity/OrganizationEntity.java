package com.edifecs.epp.security.jpa.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity implementation class for Entity: Organization
 */
@Entity(name = "Organization")
@NamedQueries({
        @NamedQuery(name = OrganizationEntity.FIND_ALL_ORGANIZATIONS, query = "SELECT o from Organization as o"),
        @NamedQuery(name = OrganizationEntity.FIND_ORGANIZATION_BY_ID, query = "SELECT o from Organization as o WHERE o.id = :id"),
        @NamedQuery(name = OrganizationEntity.FIND_ORGANIZATIONS_BY_USER_ID, query = "SELECT ou from Organization as o JOIN o.users ou WHERE o.id = :id"),
        @NamedQuery(name = OrganizationEntity.FIND_ORGANIZATIONS_BY_GROUP_ID, query = "SELECT go from Group_Table as g JOIN g.organizations go WHERE g.id = :id"),
        @NamedQuery(name = OrganizationEntity.FIND_ORGANIZATION_BY_NAME, query = "SELECT o from Organization as o WHERE o.canonicalName = :name"),
        @NamedQuery(name = OrganizationEntity.FIND_ORGANIZATION_IN_DOMAIN_BY_NAME, query = "SELECT o from Organization as o WHERE o.canonicalName = :organizationName AND o.tenant.domain = :domain"),
        @NamedQuery(name = OrganizationEntity.FIND_CHILD_ORGANIZATIONS_BY_ORGANIZATION_ID, query = "SELECT co from Organization as o JOIN o.childOrganizations co WHERE o.id = :organizationId"),
        @NamedQuery(name = OrganizationEntity.FIND_TENANT_BY_ORGANIZATION_ID, query = "SELECT t from Tenant t JOIN t.organizations o WHERE o.id = :organizationId"),
        @NamedQuery(name = OrganizationEntity.FIND_ORGANIZATIONS_BY_TENANT_ID, query = "SELECT o from Tenant t JOIN t.organizations o WHERE t.id = :id"),
        @NamedQuery(name = OrganizationEntity.FIND_ORGANIZATION_BY_NAME_TENANT_ID, query = "SELECT o from Tenant t JOIN t" +
                ".organizations o WHERE lower(o.canonicalName) = :organizationName AND t.id = :tenantId"),
        @NamedQuery(name = OrganizationEntity.DELETE_ORGANIZATION, query = "DELETE from Organization as o WHERE o.id = :id")})
public class OrganizationEntity extends AuditEntity {

    public static final String FIND_ALL_ORGANIZATIONS = "Organization.findAll";
    public static final String FIND_ORGANIZATION_BY_ID = "Organization.findById";
    public static final String FIND_ORGANIZATIONS_BY_USER_ID = "Organization.findByUserId";
    public static final String FIND_ORGANIZATIONS_BY_GROUP_ID = "Organization.findByGroupId";
    public static final String FIND_ORGANIZATION_IN_DOMAIN_BY_NAME = "Organization.findOrganizationForTenantByName";
    public static final String FIND_ORGANIZATION_BY_NAME = "Organization.findByName";
    public static final String FIND_CHILD_ORGANIZATIONS_BY_ORGANIZATION_ID = "Organization.findChildOrganizationsByOrganizationId";
    public static final String FIND_TENANT_BY_ORGANIZATION_ID = "Organization.findTenantByOrganizationId";
    public static final String FIND_ORGANIZATIONS_BY_TENANT_ID = "Organization.findOrganizationsByTenant";
    public static final String FIND_ORGANIZATION_BY_NAME_TENANT_ID = "Organization.findOrganizationByNameAndTenant";
    public static final String DELETE_ORGANIZATION = "Organization.delete";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Organization_Id", unique = true, nullable = false)
    private Long id;

    @Column(name = "Canonical_Name")
    private String canonicalName;

    @Column(name = "Description")
    private String description;

    @ManyToOne(optional = false)
    private TenantEntity tenant;

    @ManyToOne(cascade = CascadeType.ALL)
    private OrganizationEntity parentOrganization;

    @OneToMany(mappedBy = "parentOrganization", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrganizationEntity> childOrganizations = new ArrayList<>();

    @ManyToMany(mappedBy = "organizations")
    private List<UserGroupEntity> groups = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "Organization_Roles", joinColumns = {@JoinColumn(name = "Organization_Id")}, inverseJoinColumns = {@JoinColumn(name = "Role_Id")})
    private List<RoleEntity> roles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.EAGER)
    private List<SecurityRealmEntity> securityRealms = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<UserEntity> users = new ArrayList<>();

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

    public TenantEntity getTenant() {
        return tenant;
    }

    public void setTenant(TenantEntity tenant) {
        this.tenant = tenant;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<SecurityRealmEntity> getSecurityRealms() {
        return securityRealms;
    }

    public void setSecurityRealms(List<SecurityRealmEntity> securityRealms) {
        this.securityRealms = securityRealms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<OrganizationEntity> getChildOrganizations() {
        return childOrganizations;
    }

    public void setChildOrganizations(
            List<OrganizationEntity> childOrganizations) {
        this.childOrganizations = childOrganizations;
    }

    public OrganizationEntity getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(OrganizationEntity parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

}
