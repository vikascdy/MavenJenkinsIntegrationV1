package com.edifecs.servicemanager.dashboard.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "Dataset")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Dataset extends DashboardDataObject {

	@Id
	@Column(name = "Dataset_Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "Dataset_Name", unique = true)
	private String name;

	@OneToOne
	@JoinColumn(name = "Dataset_Type_Id")
	private DatasetType datasetType;

	@OneToOne
	@JoinColumn(name = "Datasource_Id")
	private Datasource datasource;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Parameter> parameters = new ArrayList<Parameter>();

	@Column(name = "Composite")
	private boolean composite;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Composite_Dataset_Id")
	private Dataset compositeDataset;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "compositeDataset")
	private List<Dataset> datasets = new ArrayList<Dataset>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.REMOVE)
	private List<Widget> widgets = new ArrayList<Widget>();

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

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

	public DatasetType getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(DatasetType datasetType) {
		this.datasetType = datasetType;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public boolean isComposite() {
		return composite;
	}

	public void setComposite(boolean composite) {
		this.composite = composite;
	}

	public Dataset getCompositeDataset() {
		return compositeDataset;
	}

	public void setCompositeDataset(Dataset compositeDataset) {
		this.compositeDataset = compositeDataset;
	}

	public List<Widget> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<Widget> widgets) {
		this.widgets = widgets;
	}

	@Override
	public DashboardDataType type() {
		return DashboardDataType.DATASET;
	}

	@Override
	public String toString() {
		return "Dataset [id=" + id + ", datasetType=" + datasetType
				+ ", datasource=" + datasource + ", parameters=" + parameters
				+ ", composite=" + composite + ", compositeDataset="
				+ compositeDataset + ", datasets=" + datasets + "]";
	}

}
