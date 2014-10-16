package com.edifecs.epp.security.jpa.entity;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Credential")
@NamedQueries({
		@NamedQuery(name = CredentialEntity.FIND_ALL_CREDENTIALS, query = "SELECT c FROM Credential as c"),
		@NamedQuery(name = CredentialEntity.FIND_CREDENTIAL_BY_KEY, query = "SELECT c FROM User u JOIN u.credentials " +
                "c WHERE c.credentialKey = :credentialKey AND u.organization.tenant.domain = :domain"),
		@NamedQuery(name = CredentialEntity.FIND_CREDENTIAL_BY_USER_AND_TYPE, query = "SELECT c FROM Credential as c WHERE c.user.id=:id AND c.credentialType.canonicalName=:credentialTypeName"),
		@NamedQuery(name = CredentialEntity.FIND_CREDENTIALS_BY_USER, query = "SELECT c FROM Credential as c WHERE c.user.id=:id") })
public class CredentialEntity {

	public static final String FIND_ALL_CREDENTIALS = "Credential.findAll";
	public static final String FIND_CREDENTIAL_BY_KEY = "Credential.findByCredentialKey";
	public static final String FIND_CREDENTIALS_BY_USER = "Credential.findByUser";
	public static final String FIND_CREDENTIAL_BY_USER_AND_TYPE = "Credential.findByUserAndType";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Credential_Id", unique = true, nullable = false)
	private Long id;

	@OneToOne
	@JoinColumn(name = "Credential_Type_Id")
	private CredentialTypeEntity credentialType;

	@Column(name = "Last_Changed_Date_Time")
	private Date lastChangesDateTime;

	@Column(name = "Expires_Date_Time")
	private Date expiresDateTime;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "Secondary_Credential_Id")
	private CredentialEntity secondaryCredential;

	// FIXME: A Credential Key is only somewhat Unique. They key can be the same
	// in certain scenarios.
	@Column(name = "Credential_Key", nullable = false)
	private String credentialKey;

	@Column(name = "Credential_Binary" )
	@Lob
	private byte[] credentialBinary;

	@ManyToOne
	@JoinColumn(name = "User_Id")
	private UserEntity user;

	@Column(name = "Credential_Expired")
	private boolean expired;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CredentialTypeEntity getCredentialType() {
		return credentialType;
	}

	public void setCredentialType(CredentialTypeEntity credentialType) {
		this.credentialType = credentialType;
	}

	public Date getLastChangesDateTime() {
		return lastChangesDateTime;
	}

	public void setLastChangesDateTime(Date lastChangesDateTime) {
		this.lastChangesDateTime = lastChangesDateTime;
	}

	public Date getExpiresDateTime() {
		return expiresDateTime;
	}

	public void setExpiresDateTime(Date expiresDateTime) {
		this.expiresDateTime = expiresDateTime;
	}

	public CredentialEntity getSecondaryCredential() {
		return secondaryCredential;
	}

	public void setSecondaryCredential(CredentialEntity secondaryCredential) {
		this.secondaryCredential = secondaryCredential;
	}

	public String getCredentialKey() {
		return credentialKey;
	}

	public void setCredentialKey(String credentialKey) {
		this.credentialKey = credentialKey;
	}

	public byte[] getCredentialBinary() {
		return credentialBinary;
	}

	public void setCredentialBinary(byte[] credentialBinary) {
		this.credentialBinary = credentialBinary;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public boolean isExpired() {
		if (null == expiresDateTime) {
			return false;
		}
		Calendar cal = Calendar.getInstance();
		return (cal.getTime().after(expiresDateTime));
	}

}
