package com.edifecs.epp.security.jpa.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "Credential_History")
@NamedQueries({ @NamedQuery(
		name = CredentialHistoryEntity.FIND_ALL_CREDENTIAL_HISTORIES,
		query = "SELECT cr_h from Credential_History as cr_h") })
public class CredentialHistoryEntity {

	public static final String FIND_ALL_CREDENTIAL_HISTORIES = "CredentialHistory.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Credential_History_Id", unique = true, nullable = false)
	private Long id;

	@OneToOne
	@JoinColumn(name = "Credential_Type_Id")
	private CredentialTypeEntity credentialType;

	@Temporal(value = TemporalType.DATE)
	@Column(name = "History_Date_Time")
	private Date historyDateTime;

	@OneToOne
	@JoinColumn(name = "Credential_Id")
	private CredentialHistoryEntity secondaryCredentialHistory;

	@Column(name = "Credential_Key")
	private String credentialKey;

	@Column(name = "Credential_Binary")
	private byte[] credentialBinary;

	@ManyToOne
	@JoinColumn(name = "User_Id")
	private UserEntity user;

	public CredentialTypeEntity getCredentialType() {
		return credentialType;
	}

	public void setCredentialType(CredentialTypeEntity credentialType) {
		this.credentialType = credentialType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Date getHistoryDateTime() {
		return historyDateTime;
	}

	public void setHistoryDateTime(Date historyDateTime) {
		this.historyDateTime = historyDateTime;
	}

	public CredentialHistoryEntity getSecondaryCredentialHistory() {
		return secondaryCredentialHistory;
	}

	public void setSecondaryCredentialHistory(CredentialHistoryEntity secondaryCredentialHistory) {
		this.secondaryCredentialHistory = secondaryCredentialHistory;
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

}
