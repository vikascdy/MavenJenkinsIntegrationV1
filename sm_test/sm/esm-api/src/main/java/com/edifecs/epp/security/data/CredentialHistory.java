package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.Date;

public class CredentialHistory implements Serializable {
	private static final long serialVersionUID = 8993654976085514641L;

	public static final String FIND_ALL_CREDENTIAL_HISTORIES = "CredentialHistory.findAll";

	private long id;

	private CredentialType credentialType;

	private Date historyDateTime;

	private CredentialHistory alternativeCredentialHistory;

	private String credentialKey;

	private byte[] credentialBinary;

	public CredentialType getCredentialType() {
		return credentialType;
	}

	public void setCredentialType(CredentialType credentialType) {
		this.credentialType = credentialType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getHistoryDateTime() {
		return historyDateTime;
	}

	public void setHistoryDateTime(Date historyDateTime) {
		this.historyDateTime = historyDateTime;
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

	public CredentialHistory getAlternativeCredentialHistory() {
		return alternativeCredentialHistory;
	}

	public void setAlternativeCredentialHistory(
			CredentialHistory alternativeCredentialHistory) {
		this.alternativeCredentialHistory = alternativeCredentialHistory;
	}

	
}
