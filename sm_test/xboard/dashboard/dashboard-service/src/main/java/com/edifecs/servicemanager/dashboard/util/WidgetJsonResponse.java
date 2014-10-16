package com.edifecs.servicemanager.dashboard.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.edifecs.servicemanager.dashboard.entity.Parameter;
import com.edifecs.servicemanager.dashboard.entity.Widget;
import com.edifecs.servicemanager.dashboard.entity.WidgetType;

public class WidgetJsonResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private WidgetType widgetType;

	private long datasetId;

	// private List<XBoard> xboards = new ArrayList<XBoard>();

	private List<Parameter> parameters = new ArrayList<Parameter>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public WidgetType getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(WidgetType widgetType) {
		this.widgetType = widgetType;
	}

	public long getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(long datasetId) {
		this.datasetId = datasetId;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public static List<WidgetJsonResponse> widgetsToJsonResp(
			List<Widget> widgets) {

		List<WidgetJsonResponse> resp = new ArrayList<WidgetJsonResponse>();
		for (Widget w : widgets) {
			resp.add(widgetToJsonResp(w));
		}

		return resp;
	}

	public static WidgetJsonResponse widgetToJsonResp(Widget w) {

		WidgetJsonResponse respObj = new WidgetJsonResponse();
		respObj.setId(w.getId());
		respObj.setParameters(w.getParameters());
		respObj.setWidgetType(w.getWidgetType());
		respObj.setDatasetId(w.getDataset().getId());

		return respObj;
	}
}
