package com.edifecs.servicemanager.dashboard.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "XBoard_Type")
public class XBoardType extends MetaObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ParameterDef> properties = new ArrayList<ParameterDef>();

	//private List<XBoard> xBoards = new ArrayList<XBoard>();

	@Override
	@Id
	@Column(name = "XBoard_Type_Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return super.getId();
	}

	@Override
	@Column(name = "XBoard_Type_Name")
	public String getName() {
		return super.getName();
	}

	@Override
	@Column(name = "XBoard_Type_Description")
	public String getDescription() {
		return super.getDescription();
	}

	@Override
	@Column(name = "XBoard_Type_Category")
	public String getCategory() {
		return super.getCategory();
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	public List<ParameterDef> getProperties() {
		return properties;
	}

	public void setProperties(List<ParameterDef> properties) {
		this.properties = properties;
	}

	@Override
	public DashboardDataType type() {
		return DashboardDataType.XBOARD;
	}

}
