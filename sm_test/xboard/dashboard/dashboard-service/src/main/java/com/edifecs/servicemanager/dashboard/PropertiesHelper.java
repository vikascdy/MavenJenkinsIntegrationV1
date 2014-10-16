package com.edifecs.servicemanager.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.edifecs.servicemanager.dashboard.datastore.DashboardDatastore;
import com.edifecs.servicemanager.dashboard.entity.Dataset;
import com.edifecs.servicemanager.dashboard.entity.Datasource;
import com.edifecs.servicemanager.dashboard.entity.Parameter;
import com.edifecs.servicemanager.dashboard.entity.ParameterDef;
import com.edifecs.servicemanager.dashboard.util.DashboardVariables;

public class PropertiesHelper {

	public static List<Parameter> fetchParametersTest(
			Map<String, Object> prMap, DashboardDatastore db)
			throws XBoardException {

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (Entry<String, Object> pEntry : prMap.entrySet()) {

			Parameter parameter = new Parameter();
			parameter.setParameterDef(db.getParameterDefFromName(pEntry
					.getKey()));

			if (pEntry.getKey().equals(DashboardVariables.IMAGE_DATA)) {
				InputStream is = (InputStream) pEntry.getValue();
				System.out.println("processing image stream, is : " + is);
				byte[] bytes;
				try {
					bytes = IOUtils.toByteArray(is);
				} catch (IOException e) {
					e.printStackTrace();
					throw new XBoardException(
							"Failed to parse Input Stream for : "
									+ pEntry.getKey());
				}

				parameter.setValue(bytes);
			} else
				parameter.setValue(pEntry.getValue().toString());

			parameters.add(parameter);
		}
		return parameters;
	}

	public static void checkRequiredPropertiesTest(
			Map<String, Object> inputpMap, List<ParameterDef> reqList)
			throws XBoardException {
		for (ParameterDef param : reqList) {
			if (param.isRequired() && !inputpMap.containsKey(param.getName()))
				throw new XBoardException("Missing required property "
						+ param.getName());
		}
	}

	public static List<Parameter> fetchParameters(Map<String, String> prMap,
			DashboardDatastore db) throws XBoardException {

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (Entry<String, String> pEntry : prMap.entrySet()) {

			Parameter parameter = new Parameter();
			parameter.setParameterDef(db.getParameterDefFromName(pEntry
					.getKey()));
			parameter.setValue(pEntry.getValue());

			parameters.add(parameter);
		}
		return parameters;
	}

	public static void checkRequiredProperties(Map<String, String> inputpMap,
			List<ParameterDef> reqList) throws XBoardException {
		for (ParameterDef param : reqList) {
			if (param.isRequired() && !inputpMap.containsKey(param.getName()))
				throw new XBoardException("Missing required property "
						+ param.getName());
		}
	}

	public static String fetchPropertyFromDatasource(Datasource datasource,
			String name) throws XBoardException {

		for (Parameter p : datasource.getParameters()) {
			if (p.getParameterDef().getName().equalsIgnoreCase(name))
				return p.getValue().toString();
		}
		return null;
	}

	public static String fetchPropertyFromDataset(Dataset dataset, String name)
			throws XBoardException {

		for (Parameter p : dataset.getParameters()) {
			if (p.getParameterDef().getName().equalsIgnoreCase(name))
				return p.getValue().toString();
		}
		return null;
	}
}
