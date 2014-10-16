package com.edifecs.epp.security.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Permission")
@NamedQueries({
		@NamedQuery(
				name = PermissionEntity.FIND_ALL_PERMISSIONS,
				query = "SELECT pe from Permission as pe "
						+ "ORDER BY pe.productCanonicalName, pe.categoryCanonicalName, pe.typeCanonicalName,"
						+ "pe.subTypeCanonicalName, pe.canonicalName ASC"),
		@NamedQuery(
				name = PermissionEntity.FIND_ALL_PERMISSIONS_FOR_ROLE,
				query = "SELECT pe FROM Role ro JOIN ro.permissions pe "
						+ "WHERE ro.id = :id "
						+ "ORDER BY pe.productCanonicalName, pe.categoryCanonicalName, pe.typeCanonicalName, "
						+ "pe.subTypeCanonicalName, pe.canonicalName ASC"),
		@NamedQuery(
				name = PermissionEntity.FIND_ALL_PERMISSIONS_FOR_USER,
				query = "SELECT pe FROM User us JOIN us.roles ro JOIN ro.permissions pe "
						+ "WHERE us.id = :id "
						+ "ORDER BY pe.productCanonicalName, pe.categoryCanonicalName, pe.typeCanonicalName, "
						+ "pe.subTypeCanonicalName, pe.canonicalName ASC"),
		@NamedQuery(
				name = PermissionEntity.FIND_PERMISSION_BY_NAMES,
				query = "SELECT pe from Permission as pe "
						+ "WHERE pe.productCanonicalName=:productCanonicalName "
						+ "AND pe.categoryCanonicalName=:categoryCanonicalName "
						+ "AND pe.typeCanonicalName=:typeCanonicalName "
						+ "AND pe.subTypeCanonicalName=:subTypeCanonicalName "
						+ "AND pe.canonicalName=:canonicalName"),				
		@NamedQuery(name = PermissionEntity.FIND_PERMISSION_BY_ID,
				query = "SELECT pe from Permission as pe WHERE pe.id = :id"),
		@NamedQuery(name = PermissionEntity.DELETE_PERMISSION,
				query = "DELETE from Permission as pe WHERE pe.id = :id"), })
public class PermissionEntity extends AuditEntity {
	public static final String FIND_ALL_PERMISSIONS = "Permission.findAll";
	public static final String FIND_ALL_PERMISSIONS_FOR_ROLE = "Permission.findForRole";
	public static final String FIND_ALL_PERMISSIONS_FOR_USER = "Permission.findForUser";
	public static final String FIND_PERMISSION_BY_NAMES = "Permission.findPermissionByNames";
	public static final String FIND_PERMISSION_BY_ID = "Permission.findById";
	public static final String DELETE_PERMISSION = "Permission.delete";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Permission_Id")
	private Long id;

	@Column(name = "Product_Canonical_Name", nullable = false)
	private String productCanonicalName;

	@Column(name = "Category_Canonical_Name", nullable = false)
	private String categoryCanonicalName;

	@Column(name = "Type_Canonical_Name", nullable = true)
	private String typeCanonicalName;

	@Column(name = "Sub_Type_Canonical_Name", nullable = true)
	private String subTypeCanonicalName;

	@Column(name = "Canonical_Name", nullable = false)
	private String canonicalName;

	@Column(name = "Sort_Order")
	private Long sortOrder;
	
	@Column(name = "Description")
	private String description;

	@ManyToMany(mappedBy = "permissions", cascade = CascadeType.ALL)
	private List<RoleEntity> roles = new ArrayList<RoleEntity>();

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

	public String getProductCanonicalName() {
		return productCanonicalName;
	}

	public void setProductCanonicalName(String productCanonicalName) {
		this.productCanonicalName = productCanonicalName;
	}

	public String getCategoryCanonicalName() {
		return categoryCanonicalName;
	}

	public void setCategoryCanonicalName(String categoryCanonicalName) {
		this.categoryCanonicalName = categoryCanonicalName;
	}

	public String getTypeCanonicalName() {
		return typeCanonicalName;
	}

	public void setTypeCanonicalName(String typeCanonicalName) {
		this.typeCanonicalName = typeCanonicalName;
	}

	public String getSubTypeCanonicalName() {
		return subTypeCanonicalName;
	}

	public void setSubTypeCanonicalName(String subTypeCanonicalName) {
		this.subTypeCanonicalName = subTypeCanonicalName;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
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

}
