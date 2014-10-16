package com.edifecs.epp.security.jpa.entity;

import com.edifecs.core.configuration.configuration.Scope;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity(name = "Property")
@NamedQueries({ 
	@NamedQuery(name = PropertyEntity.FIND_ALL_PROPERTIES, 
			    query = "SELECT p from Property as p"),
	@NamedQuery(name = PropertyEntity.FIND_PROPERTIES_OF_CURRENT_USER, 
			    query = "SELECT p from Property as p where p.ownerId = :currentUserId and p.ownerScope = \'USER\'"),
	@NamedQuery(name = PropertyEntity.FIND_PROPERTIES_OF_CURRENT_USER_AND_MODULE, 
			    query = "SELECT p from Property as p where p.ownerId = :currentUserId and p.ownerScope = \'USER\' and p.moduleName = :moduleName")
})
public class PropertyEntity extends AuditEntity {
	
	public static final String FIND_ALL_PROPERTIES = "Property.findAllProperties";
	public static final String FIND_PROPERTIES_OF_SCOPE = "Property.findPropertiesOfScope";
	public static final String FIND_PROPERTIES_OF_CURRENT_USER = "Property.findPropertiesOfCurrentUser";
	public static final String FIND_PROPERTIES_OF_CURRENT_USER_AND_MODULE = "Property.findPropertiesOfCurrentUserAndModule";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Property_Id")
	private Long id;
	
	@Column(name = "Property_Name")
	private String name;

	@Column(name = "Property_Value")
	private String value;
	
	@Column(name = "Property_Owner_Id", nullable = false)
	private Long ownerId;
	  
	@Column(name = "Property_Owner_Scope", nullable = false)
	@Enumerated(EnumType.STRING)
	private Scope ownerScope;
	
	@Column(name = "Property_Module_Name")
	private String moduleName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public void setOwnerScope(Scope scope) {
		this.ownerScope = scope;
	}
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
}
