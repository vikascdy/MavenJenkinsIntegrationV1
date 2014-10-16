package com.edifecs.servicemanager.dashboard.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Parameter_Def")
public class ParameterDef extends MetaObject {

	private String dataType;

	private Boolean isRequired = true;

	@Override
	@Id
	@Column(name = "Parameter_Def_Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return super.getId();
	}

	@Override
	@Column(name = "Parameter_Def_Name")
	public String getName() {
		return super.getName();
	}

	@Override
	@Column(name = "Parameter_Def_Description")
	public String getDescription() {
		return super.getDescription();
	}

	@Override
	@Column(name = "Parameter_Def_Category")
	public String getCategory() {
		return super.getCategory();
	}

	@Column(name = "Parameter_Def_Data_Type")
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Column(name = "Parameter_Def_Required", nullable = false)
	public Boolean isRequired() {
		return isRequired;
	}

	public void setRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public DashboardDataType type() {
		return DashboardDataType.PARAMETER_DEF;
	}

}
