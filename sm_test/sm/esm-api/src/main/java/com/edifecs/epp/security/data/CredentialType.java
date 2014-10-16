package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.List;

public class CredentialType implements Serializable {
	private static final long serialVersionUID = 5383201572479676242L;

	private Long id;

    private String canonicalName;

    private boolean identification;

    private boolean authentication;

    private CredentialType parentCredentialType;

    private List<CredentialType> childCredentialTypes;

    public List<CredentialType> getChildCredentialTypes() {
        return childCredentialTypes;
    }

    public void setChildCredentialTypes(
            List<CredentialType> childCredentialTypes) {
        this.childCredentialTypes = childCredentialTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CredentialType getParentCredentialType() {
        return parentCredentialType;
    }

    public void setParentCredentialType(CredentialType parentCredentialType) {
        this.parentCredentialType = parentCredentialType;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public boolean isIdentification() {
        return identification;
    }

    public void setIdentification(boolean identification) {
        this.identification = identification;
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }
}
