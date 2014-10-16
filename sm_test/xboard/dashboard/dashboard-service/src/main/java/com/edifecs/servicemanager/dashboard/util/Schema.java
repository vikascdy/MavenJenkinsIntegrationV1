package com.edifecs.servicemanager.dashboard.util;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Schema implements Serializable {

	private String datasetId;

	private Map<String, Object> meta = new HashMap<String, Object>();

	// private Map<String, String> data = new HashMap<String, String>();

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public Map<String, Object> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}

	public static final Schema getSchemaFromResultSetMeta(ResultSetMetaData rsmd) {

		String columnName = null;
		String columnType = null;
		Schema jsonResultSet = new Schema();

		try {
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				columnName = rsmd.getColumnName(i + 1);
				columnType = rsmd.getColumnTypeName(i + 1);
				jsonResultSet.getMeta().put(columnName, columnType);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonResultSet;
	}
}
