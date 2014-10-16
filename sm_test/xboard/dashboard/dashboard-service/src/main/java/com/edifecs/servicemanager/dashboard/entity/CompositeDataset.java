package com.edifecs.servicemanager.dashboard.entity;

import java.util.ArrayList;
import java.util.List;

public class CompositeDataset {

	private List<Dataset> datasets = new ArrayList<Dataset>();

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}

}
