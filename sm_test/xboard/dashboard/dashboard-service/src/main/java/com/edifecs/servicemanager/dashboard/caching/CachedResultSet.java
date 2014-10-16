package com.edifecs.servicemanager.dashboard.caching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedResultSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String index;

	private String type;

	private String datasetId;

	private Map<String, Object> meta = new HashMap<String, Object>();

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	@Override
	public String toString() {
		return "CachedResultSet [index=" + index + ", type=" + type
				+ ", datasetId=" + datasetId + ", meta=" + meta + ", data="
				+ data + "]";
	}

}
