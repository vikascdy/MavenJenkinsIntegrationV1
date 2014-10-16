package com.edifecs.epp.security.data;

import com.edifecs.core.configuration.configuration.Scope;

public class Property extends com.edifecs.core.configuration.configuration.Property {

	private static final long serialVersionUID = 4831951608570123016L;

	private Long id;
	
	private Long ownerId;

    //FIXME: Fix This Dependency
	private Scope ownerScope;
	
	private String moduleName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Scope getOwnerScope() {
		return ownerScope;
	}

	public void setOwnerScope(Scope ownerScope) {
		this.ownerScope = ownerScope;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

}
