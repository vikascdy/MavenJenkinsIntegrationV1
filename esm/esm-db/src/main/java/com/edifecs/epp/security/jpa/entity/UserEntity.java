package com.edifecs.epp.security.jpa.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity implementation class for Entity: User
 */
@Entity(name = "User")
@Table(name = "User_Entity")
@NamedQueries({
		@NamedQuery(name = UserEntity.FIND_ALL_USERS, query = "SELECT u FROM User as u"),
		@NamedQuery(name = UserEntity.FIND_USER_BY_ID, query = "SELECT u FROM User as u WHERE u.id = :userId"),
		@NamedQuery(name = UserEntity.FIND_USER_BY_CREDENTIAL_KEY, query = "SELECT u FROM User u JOIN u.credentials c WHERE c.credentialKey = :credentialKey AND u.organization.tenant.domain = :domain"),
		@NamedQuery(name = UserEntity.FIND_USER_BY_ALT_CREDENTIAL_KEY, query = "SELECT u FROM User as u JOIN u.credentials c  WHERE c.credentialKey = :credentialKey"),
		@NamedQuery(name = UserEntity.FIND_USER_BY_EMAIL, query = "SELECT u FROM User u WHERE u.contact.emailAddress = :email"),
		@NamedQuery(name = UserEntity.VERIFY_USER_BY_EMAIL, query = "SELECT u FROM User u JOIN u.contact c WHERE lower(c.emailAddress) = :email"),
		@NamedQuery(name = UserEntity.FIND_USERS_BY_ROLE, query = "SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId"),
		@NamedQuery(name = UserEntity.SEARCH_USERS_BY_ROLE, query = "SELECT u FROM User u JOIN u.roles r JOIN u.credentials c WHERE r.id = :roleId AND c.credentialKey LIKE :name"),
		@NamedQuery(name = UserEntity.SEARCH_USERS_BY_NAME, query = "SELECT u FROM User u JOIN u.credentials c WHERE c.credentialKey LIKE :name"),
		@NamedQuery(name = UserEntity.SEARCH_USERS_BY_FNAME_MNAME_LNAME, query = "SELECT u FROM User u JOIN u.contact c JOIN u.credentials cr WHERE lower(c.firstName) LIKE :name OR lower(c.middleName) LIKE :name OR lower(c.lastName) LIKE :name OR lower(cr.credentialKey) LIKE :name"),
		@NamedQuery(name = UserEntity.FIND_USERS_BY_GROUP, query = "SELECT gu FROM Group_Table g JOIN g.users gu WHERE g.id = :groupId"),
		@NamedQuery(name = UserEntity.FIND_USERS_BY_ORGANIZATION, query = "SELECT u FROM User u JOIN u.organization o WHERE o.id = :organizationId"),
		@NamedQuery(name = UserEntity.FIND_USERS_BY_PERMISSION, query = "SELECT u FROM User u JOIN u.roles r JOIN r.permissions pe "
				+ "WHERE pe.productCanonicalName=:productCanonicalName "
				+ "AND pe.categoryCanonicalName=:categoryCanonicalName "
				+ "AND pe.typeCanonicalName=:typeCanonicalName "
				+ "AND pe.subTypeCanonicalName=:subTypeCanonicalName "
				+ "AND pe.canonicalName=:canonicalName"),
		@NamedQuery(name = UserEntity.DELETE_USER, query = "DELETE FROM User as u WHERE u.id = :userId") })
@Where(clause = "deleted = '0'")
public class UserEntity extends AuditEntity {

	public static final String FIND_ALL_USERS = "User.findAll";
	public static final String FIND_USER_BY_ID = "User.findById";
	public static final String FIND_USER_BY_CREDENTIAL_KEY = "User.findByCredentialKey";
	public static final String FIND_USER_BY_ALT_CREDENTIAL_KEY = "User.findByAltCredentialKey";
	public static final String FIND_USER_BY_EMAIL = "User.findByEmail";
	public static final String VERIFY_USER_BY_EMAIL = "User.verifyByEmail";
	public static final String FIND_USERS_BY_ROLE = "User.findByRole";
	public static final String SEARCH_USERS_BY_ROLE = "User.searchByRole";
	public static final String SEARCH_USERS_BY_NAME = "User.searchByName";
	public static final String SEARCH_USERS_BY_FNAME_MNAME_LNAME = "User.searchByFirstOrMiddleOrLastName";
	public static final String FIND_USERS_BY_GROUP = "User.findAllByGroup";
	public static final String FIND_USERS_BY_ORGANIZATION = "User.findAllByOrganization";
	public static final String FIND_USERS_BY_PERMISSION = "User.findAllByPermission";
	public static final String DELETE_USER = "User.delete";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "User_Id", unique = true, nullable = false)
	private Long id;

	@Column(name = "Human_User", nullable = false)
	private boolean humanUser;

	@Column(name = "Active", nullable = false)
	private boolean active;

	@Column(name = "Deleted", nullable = false)
	private boolean deleted;

	@Column(name = "Suspended", nullable = false)
	private boolean suspended;

	@Column(name = "Change_Password_At_FirstLogin")
	private boolean changePasswordAtFirstLogin;

	@Column(name = "Last_Login_Date_Time", nullable = true)
	private Date lastLoginDateTime;

	@Column(name = "User_Created_Date_Time", nullable = true)
	private Date createdDateTime;

	@Column(name = "User_Modified_Date_Time", nullable = true)
	private Date modifiedDateTime;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Contact_Id")
	private ContactEntity contact;

	@ManyToOne
	private OrganizationEntity organization;

	@ManyToMany(mappedBy = "users")
	private List<UserGroupEntity> groups = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "User_Roles", joinColumns = { @JoinColumn(name = "User_Id") }, inverseJoinColumns = { @JoinColumn(name = "Role_Id") })
	private List<RoleEntity> roles = new ArrayList<>();

	@ElementCollection
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<CredentialEntity> credentials = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<CredentialHistoryEntity> credentialHistories = new ArrayList<>();

	@OneToOne(mappedBy = "belongsToUser", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private UserSessionEntity userSession;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isHumanUser() {
		return humanUser;
	}

	public void setHumanUser(boolean humanUser) {
		this.humanUser = humanUser;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getLastLoginDateTime() {
		return lastLoginDateTime;
	}

	public void setLastLoginDateTime(Date lastLoginDateTime) {
		this.lastLoginDateTime = lastLoginDateTime;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getModifiedDateTime() {
		return modifiedDateTime;
	}

	public void setModifiedDateTime(Date modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}

	public ContactEntity getContact() {
		return contact;
	}

	public void setContact(ContactEntity contact) {
		this.contact = contact;
	}

	public OrganizationEntity getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationEntity organization) {
		this.organization = organization;
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

	public List<CredentialEntity> getCredentials() {
		return credentials;
	}

	public void setCredentials(List<CredentialEntity> credentials) {
		this.credentials = credentials;
	}

	public List<CredentialHistoryEntity> getCredentialHistories() {
		return credentialHistories;
	}

	public void setCredentialHistories(
			List<CredentialHistoryEntity> credentialHistories) {
		this.credentialHistories = credentialHistories;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public UserSessionEntity getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSessionEntity userSession) {
		this.userSession = userSession;
	}

	public boolean isChangePasswordAtFirstLogin() {
		return changePasswordAtFirstLogin;
	}

	public void setChangePasswordAtFirstLogin(boolean changePasswordAtFirstLogin) {
		this.changePasswordAtFirstLogin = changePasswordAtFirstLogin;
	}

}
