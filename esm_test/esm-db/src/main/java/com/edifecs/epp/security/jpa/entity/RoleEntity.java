package com.edifecs.epp.security.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Role")
@NamedQueries({
		@NamedQuery(name = RoleEntity.FIND_ALL_ROLES, query = "SELECT r from Role as r"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_ID, query = "SELECT r from Role as r WHERE r.id = :id"),
		@NamedQuery(name = RoleEntity.FIND_CHILD_ROLES, query = "SELECT rr from Role r JOIN r.roles rr WHERE r.id = :id"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_USER_ID, query = "SELECT ru from User u JOIN u.roles ru WHERE u.id = :id"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_ORGANIZATION_ID, query = "SELECT ro from Organization o JOIN o.roles ro WHERE o.id = :id"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_TENANT_ID, query = "SELECT tr from Tenant t JOIN t.roles tr WHERE t.id = :id"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_NAME_TENANT_ID, query = "SELECT tr from Tenant t JOIN t.roles tr " +
                "WHERE t.id = :tenantId AND lower(tr.canonicalName) = :roleName"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_GROUP_ID, query = "SELECT rg from Group_Table g JOIN g.roles rg WHERE g.id = :id"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_ROLE_NAME, query = "SELECT r from Role as r WHERE r.canonicalName = :roleName"),
		@NamedQuery(name = RoleEntity.FIND_ROLE_BY_ROLE_DOMAIN_NAME, query = "SELECT r from Role as r WHERE r" +
                ".canonicalName = :roleName AND r.tenant.domain = :domain"),
		@NamedQuery(name = RoleEntity.DELETE_ROLE, query = "DELETE from Role as r WHERE r.id = :id") })
public class RoleEntity extends AuditEntity {
	public static final String FIND_ALL_ROLES = "Role.findAll";
	public static final String FIND_CHILD_ROLES = "Role.findAllChildRoles";
	public static final String FIND_ROLE_BY_ID = "Role.findById";
	public static final String FIND_ROLE_BY_USER_ID = "Role.findByUserId";
	public static final String FIND_ROLE_BY_ORGANIZATION_ID = "Role.findByOrgId";
	public static final String FIND_ROLE_BY_TENANT_ID = "Role.findByTenantId";
	public static final String FIND_ROLE_BY_NAME_TENANT_ID = "Role.findByNameAndTenantId";
	public static final String FIND_ROLE_BY_GROUP_ID = "Role.findByGroupId";
	public static final String FIND_ROLE_BY_ROLE_NAME = "Role.findByRoleName";
	public static final String FIND_ROLE_BY_ROLE_DOMAIN_NAME = "Role.findByRoleDomainName";
	public static final String DELETE_ROLE = "Role.delete";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Role_Id")
	private Long id;

	@Column(name = "Canonical_Name", nullable = false)
	private String canonicalName;

	@Column(name = "Role_Description", nullable = true)
	private String description;

	@Column(name = "Role_Editible")
	private boolean readOnly;

	@ManyToOne
	private TenantEntity tenant;

	@ManyToMany(mappedBy = "roles", cascade = CascadeType.DETACH)
	private List<UserEntity> users = new ArrayList<UserEntity>();

	@ManyToMany(mappedBy = "roles")
	private List<UserGroupEntity> groups = new ArrayList<UserGroupEntity>();

	// FIXME: Rename Roles to childRoles
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "Role_Role", joinColumns = { @JoinColumn(name = "Role_Id") }, inverseJoinColumns = { @JoinColumn(name = "Child_Role_Id") })
	private List<RoleEntity> roles = new ArrayList<RoleEntity>();

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "Role_Role", joinColumns = { @JoinColumn(name = "Child_Role_Id") }, inverseJoinColumns = { @JoinColumn(name = "Role_Id") })
	private List<RoleEntity> parentRoles = new ArrayList<RoleEntity>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "Role_Permissions", joinColumns = { @JoinColumn(name = "Role_Id") }, inverseJoinColumns = { @JoinColumn(name = "Permission_Id") })
	private List<PermissionEntity> permissions = new ArrayList<PermissionEntity>();

	public List<PermissionEntity> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionEntity> permissions) {
		this.permissions = permissions;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	public List<UserGroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(List<UserGroupEntity> groups) {
		this.groups = groups;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleEntity> roles) {
		this.roles = roles;
	}

	public List<RoleEntity> getParentRoles() {
		return parentRoles;
	}

	public void setParentRoles(List<RoleEntity> parentRoles) {
		this.parentRoles = parentRoles;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public TenantEntity getTenant() {
		return tenant;
	}

	public void setTenant(TenantEntity tenant) {
		this.tenant = tenant;
	}

}
