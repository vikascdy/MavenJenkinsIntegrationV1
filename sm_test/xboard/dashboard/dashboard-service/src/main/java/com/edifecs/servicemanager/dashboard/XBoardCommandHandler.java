package com.edifecs.servicemanager.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.message.annotations.Arg;
import com.edifecs.message.annotations.Command;
import com.edifecs.message.annotations.JGroups;
import com.edifecs.servicemanager.dashboard.datastore.DashboardDatastore;
import com.edifecs.servicemanager.dashboard.entity.Widget;
import com.edifecs.servicemanager.dashboard.entity.XBoard;
import com.edifecs.servicemanager.dashboard.entity.XBoardType;
import com.edifecs.servicemanager.dashboard.util.DashboardVariables;
import com.edifecs.servicemanager.dashboard.util.XBoardJsonResponse;

@Akka(enabled = true)
@com.edifecs.message.annotations.CommandHandler
public class XBoardCommandHandler extends AbstractCommandHandler {

	private DashboardDatastore db;

	public XBoardCommandHandler(DashboardDatastore db) {
		super();
		this.db = db;
	}

	@Command
	public List<XBoardType> getXBoardTypes() throws Exception {
		return db.getAll(XBoardType.class);
	}

	@Command
	public XBoardType getXBoardTypeById(
			@Arg(name = "xBoardTypeId", description = "xBoardTypeId", required = true) String xBoardTypeId)
			throws Exception {
		return db.getById(XBoardType.class, Long.valueOf(xBoardTypeId));
	}

	@Command
	public List<XBoardType> getXBoardTypesInRange(
			@Arg(name = "startRecord", description = "startRecord", required = true) int startRecord,
			@Arg(name = "recordCount", description = "recordCount", required = true) int recordCount)
			throws Exception {
		return db.getRange(XBoardType.class, startRecord, recordCount);
	}

	@Command
	public Object getXBoards() throws Exception {
		return XBoardJsonResponse.xBoardListToJsonResp(db.getAll(XBoard.class));
	}

	@Command
	public Object getXBoard(
			@Arg(name = "xBoardId", description = "xBoardId", required = true) String xBoardId)
			throws Exception {
		return XBoardJsonResponse.xBoardToJsonResp(db.getById(XBoard.class,
				Long.valueOf(xBoardId)));
	}

	@Command
	public boolean removeXBoard(
			@Arg(name = "xBoardId", description = "xBoardId", required = true) String xBoardId)
			throws Exception {
		return db.delete(db.getById(XBoard.class, Long.valueOf(xBoardId)));
	}

	@Command
	public long createXBoard(
			@Arg(name = "xBoardTypeId", description = "xBoardTypeId", required = true) String xBoardTypeId,
			@Arg(name = "widgetIds", description = "widgetIds", required = true) ArrayList<String> widgetIds,
			@Arg(name = "xBoardProperties", description = "xBoardProperties", required = true) HashMap<String, String> xBoardProperties)
			throws Exception {

		XBoard xBoard = null;
		if (null != widgetIds && !widgetIds.isEmpty()) {

			XBoardType xBoardType = db.getById(XBoardType.class,
					Long.valueOf(xBoardTypeId));

			PropertiesHelper.checkRequiredProperties(xBoardProperties,
					xBoardType.getProperties());
			xBoard = new XBoard();
			xBoard.setxBoardType(xBoardType);

			for (String widgetId : widgetIds) {
				Widget widget = db
						.getById(Widget.class, Long.valueOf(widgetId));
				widget.getXboards().add(xBoard);
				xBoard.getWidgets().add(widget);
				db.update(widget);
			}

			xBoard.setParameters(PropertiesHelper.fetchParameters(
					xBoardProperties, db));
			xBoard.setName(xBoardProperties
					.get(DashboardVariables.PARATMETER_NAME));

		}

		Long id = null;
		id = db.create(xBoard);
		return id;
	}

	@Command
	public Object updateXBoard(
			@Arg(name = "xBoardId", description = "xBoardId", required = true) String xBoardId,
			@Arg(name = "widgetIds", description = "widgetIds", required = true) ArrayList<String> widgetIds,
			@Arg(name = "xBoardProperties", description = "xBoardProperties", required = true) HashMap<String, String> xBoardProperties)
			throws Exception {

		XBoard xBoard = null;
		if (null != widgetIds && !widgetIds.isEmpty()) {

			xBoard = db.getById(XBoard.class, Long.valueOf(xBoardId));

			PropertiesHelper.checkRequiredProperties(xBoardProperties, xBoard
					.getxBoardType().getProperties());

			xBoard.getWidgets().clear();

			for (String widgetId : widgetIds) {
				Widget widget = db
						.getById(Widget.class, Long.valueOf(widgetId));
				if (!widget.getXboards().contains(xBoard)) {
					widget.getXboards().add(xBoard);
					db.update(widget);
				}
				// TODO : write comparator
				if (!xBoard.getWidgets().contains(widget))
					xBoard.getWidgets().add(widget);

			}

		}

		xBoard.setParameters(PropertiesHelper.fetchParameters(xBoardProperties,
				db));

		db.update(xBoard);

		return XBoardJsonResponse.xBoardToJsonResp(xBoard);
	}
}
