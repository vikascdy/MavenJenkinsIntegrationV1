package com.edifecs.servicemanager.dashboard.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Parameter")
public class Parameter extends DashboardDataObject {

	@Id
	@Column(name = "Parameter_Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne
	@JoinColumn(name = "ParameterDef_Id")
	private ParameterDef parameterDef;

	@Column(name = "Parameter_Value", columnDefinition = "TEXT")
	private Serializable value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ParameterDef getParameterDef() {
		return parameterDef;
	}

	public void setParameterDef(ParameterDef parameterDef) {
		this.parameterDef = parameterDef;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	@Override
	public DashboardDataType type() {
		return DashboardDataType.PARAMETER;
	}

}
