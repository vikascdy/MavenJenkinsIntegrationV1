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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "Datasource")
public class Datasource extends DashboardDataObject {

	@Id
	@Column(name = "Datasource_Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "Datastore_Type_Id")
	private DatastoreType datastoreType;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Parameter> parameters = new ArrayList<Parameter>();

	@Column(name = "Datasource_Name", unique = true)
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

	public DatastoreType getDatastoreType() {
		return datastoreType;
	}

	public void setDatastoreType(DatastoreType datastoreType) {
		this.datastoreType = datastoreType;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public DashboardDataType type() {
		return DashboardDataType.DATASOURCE;
	}

}
