package com.edifecs.servicemanager.dashboard.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "XBoard")
public class XBoard extends DashboardDataObject {

	@Id
	@Column(name = "XBoard_Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne
	@JoinColumn(name = "XBoard_Type_Id")
	private XBoardType xBoardType;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany
	@JoinTable(name = "Dashboard_Widgets", joinColumns = { @JoinColumn(name = "Dashboard_Id") }, inverseJoinColumns = { @JoinColumn(name = "Widget_Id") })
	private List<Widget> widgets = new ArrayList<Widget>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Parameter> parameters = new ArrayList<Parameter>();

	@Column(name = "XBoard_Name", unique = true)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Widget> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<Widget> widgets) {
		this.widgets = widgets;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public XBoardType getxBoardType() {
		return xBoardType;
	}

	public void setxBoardType(XBoardType xBoardType) {
		this.xBoardType = xBoardType;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public DashboardDataType type() {
		return DashboardDataType.XBOARD;
	}

}
