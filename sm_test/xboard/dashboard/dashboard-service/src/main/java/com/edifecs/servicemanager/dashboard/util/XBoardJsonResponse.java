package com.edifecs.servicemanager.dashboard.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.edifecs.servicemanager.dashboard.entity.Parameter;
import com.edifecs.servicemanager.dashboard.entity.Widget;
import com.edifecs.servicemanager.dashboard.entity.XBoard;

public class XBoardJsonResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;

	private long xBoardTypeId;

	private List<Long> widgetIds = new ArrayList<Long>();

	private List<Parameter> parameters = new ArrayList<Parameter>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getxBoardTypeId() {
		return xBoardTypeId;
	}

	public void setxBoardTypeId(long xBoardTypeId) {
		this.xBoardTypeId = xBoardTypeId;
	}

	public List<Long> getWidgetIds() {
		return widgetIds;
	}

	public void setWidgetIds(List<Long> widgetIds) {
		this.widgetIds = widgetIds;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public static List<XBoardJsonResponse> xBoardListToJsonResp(
			List<XBoard> xBoards) {

		List<XBoardJsonResponse> jsonResp = new ArrayList<XBoardJsonResponse>();
		for (XBoard xb : xBoards) {
			jsonResp.add(xBoardToJsonResp(xb));
		}
		return jsonResp;
	}

	public static XBoardJsonResponse xBoardToJsonResp(XBoard xb) {

		XBoardJsonResponse resp = new XBoardJsonResponse();
		resp.setId(xb.getId());
		resp.setParameters(xb.getParameters());
		resp.setxBoardTypeId(xb.getxBoardType().getId());

		for (Widget w : xb.getWidgets())
			resp.getWidgetIds().add(w.getId());

		return resp;
	}
}
