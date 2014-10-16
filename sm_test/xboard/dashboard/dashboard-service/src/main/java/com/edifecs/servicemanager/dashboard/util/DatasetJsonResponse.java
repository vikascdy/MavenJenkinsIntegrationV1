package com.edifecs.servicemanager.dashboard.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.edifecs.servicemanager.dashboard.entity.Dataset;
import com.edifecs.servicemanager.dashboard.entity.Parameter;

public class DatasetJsonResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private long datasetTypeId;

	private long datasourceId;

	private List<Parameter> parameters = new ArrayList<Parameter>();

	private boolean composite;

	private List<Long> datasets = new ArrayList<Long>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDatasetTypeId() {
		return datasetTypeId;
	}

	public void setDatasetTypeId(long datasetTypeId) {
		this.datasetTypeId = datasetTypeId;
	}

	public long getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(long datasourceId) {
		this.datasourceId = datasourceId;
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

	public List<Long> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Long> datasets) {
		this.datasets = datasets;
	}

	public static List<DatasetJsonResponse> datasetsToJsonResp(
			List<Dataset> datasets) {

		List<DatasetJsonResponse> resp = new ArrayList<DatasetJsonResponse>();
		for (Dataset d : datasets) {
			resp.add(datasetToJsonResp(d));
		}

		return resp;
	}

	public static DatasetJsonResponse datasetToJsonResp(Dataset d) {

		DatasetJsonResponse respObj = new DatasetJsonResponse();

		try {

			respObj.setId(d.getId());
			respObj.setParameters(d.getParameters());
			respObj.setComposite(d.isComposite());
			if (null != d.getDatasetType())
				respObj.setDatasetTypeId(d.getDatasetType().getId());
			if (null != d.getDatasource())
				respObj.setDatasourceId(d.getDatasource().getId());

			for (Dataset cd : d.getDatasets())
				respObj.getDatasets().add(cd.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respObj;
	}

}
