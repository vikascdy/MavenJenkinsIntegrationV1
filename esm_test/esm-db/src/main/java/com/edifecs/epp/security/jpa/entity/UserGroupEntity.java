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

@Entity(name = "Group_Table")
@NamedQueries({
		@NamedQuery(name = UserGroupEntity.FIND_ALL_GROUPS, query = "SELECT p from Group_Table as p"),
		@NamedQuery(name = UserGroupEntity.FIND_GROUP_BY_ID, query = "SELECT p from Group_Table as p WHERE p.id = :id"),
		@NamedQuery(name = UserGroupEntity.FIND_GROUP_BY_NAME, query = "SELECT p from Group_Table as p WHERE p.canonicalName = :name"),
		@NamedQuery(name = UserGroupEntity.DELETE_GROUP, query = "DELETE from Group_Table as p WHERE p.id = :id"),
		@NamedQuery(name = UserGroupEntity.FIND_GROUPS_BY_USER, query = "SELECT g FROM Group_Table g JOIN g.users gu WHERE gu.id = :userId"),
		@NamedQuery(name = UserGroupEntity.FIND_CHILD_GROUPS_BY_GROUP, query = "SELECT cg FROM Group_Table g JOIN g.childGroups cg WHERE g.id = :id"),
		@NamedQuery(name = UserGroupEntity.FIND_GROUPS_BY_TENANT, query = "SELECT g FROM Tenant t JOIN t.groups g WHERE t.id = :id"),
		@NamedQuery(name = UserGroupEntity.FIND_GROUPS_BY_ROLE, query = "SELECT g FROM Group_Table g JOIN g.roles gr WHERE gr.id = :roleId") })
public class UserGroupEntity extends AuditEntity {

	public static final String FIND_ALL_GROUPS = "UserGroup.findAll";
	public static final String FIND_GROUP_BY_ID = "UserGroup.findById";
	public static final String FIND_GROUP_BY_NAME = "UserGroup.findByName";
	public static final String FIND_GROUPS_BY_USER = "UserGroup.findByUser";
	public static final String FIND_GROUPS_BY_TENANT = "UserGroup.findByTenant";
	public static final String FIND_GROUPS_BY_ROLE = "UserGroup.findByRole";
	public static final String FIND_CHILD_GROUPS_BY_GROUP = "UserGroup.findChildByGroup";
	public static final String DELETE_GROUP = "UserGroup.delete";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Group_Id")
	private Long id;

	@Column(name = "Canonical_Name")
	private String canonicalName;

	@Column(name = "Description")
	private String description;

	@Column(name = "Maximum_Users")
	private Long maximumUsers;

	@ManyToOne
	private TenantEntity tenant;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "Group_Organizations", joinColumns = { @JoinColumn(name = "Group_Id") }, inverseJoinColumns = { @JoinColumn(name = "Organization_Id") })
	private List<OrganizationEntity> organizations = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "Group_Users", joinColumns = { @JoinColumn(name = "Group_Id") }, inverseJoinColumns = { @JoinColumn(name = "User_Id") })
	private List<UserEntity> users = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "Group_Roles", joinColumns = { @JoinColumn(name = "Group_Id") }, inverseJoinColumns = { @JoinColumn(name = "Role_Id") })
	private List<RoleEntity> roles = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "Group_Group", joinColumns = { @JoinColumn(name = "Group_Id") }, inverseJoinColumns = { @JoinColumn(name = "Child_Group_Id") })
	private List<UserGroupEntity> childGroups = new ArrayList<>();

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

	public Long getMaximumUsers() {
		return maximumUsers;
	}

	public void setMaximumUsers(Long maximumUsers) {
		this.maximumUsers = maximumUsers;
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

	public List<OrganizationEntity> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<OrganizationEntity> organizations) {
		this.organizations = organizations;
	}

	public List<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleEntity> roles) {
		this.roles = roles;
	}

	public List<UserGroupEntity> getChildGroups() {
		return childGroups;
	}

	public void setChildGroups(List<UserGroupEntity> childGroups) {
		this.childGroups = childGroups;
	}

}
