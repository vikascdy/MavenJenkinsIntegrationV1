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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "Widget")
public class Widget extends DashboardDataObject {

	@Id
	@Column(name = "Widget_Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne
	@JoinColumn(name = "Widget_Type_Id")
	private WidgetType widgetType;

	@ManyToOne
	@JoinColumn(name = "Dataset_Id")
	private Dataset dataset;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Parameter> parameters = new ArrayList<Parameter>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(mappedBy = "widgets")
	private List<XBoard> xboards = new ArrayList<XBoard>();

	@Column(name = "Widget_Name", unique = true)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public WidgetType getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(WidgetType widgetType) {
		this.widgetType = widgetType;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<XBoard> getXboards() {
		return xboards;
	}

	public void setXboards(List<XBoard> xboards) {
		this.xboards = xboards;
	}

	@Override
	public DashboardDataType type() {
		return DashboardDataType.WIDGET;
	}

}
