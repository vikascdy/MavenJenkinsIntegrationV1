package com.edifecs.servicemanager.dashboard;

import java.util.HashMap;
import java.util.List;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.message.annotations.Arg;
import com.edifecs.message.annotations.Command;
import com.edifecs.message.annotations.CommandHandler;
import com.edifecs.message.annotations.JGroups;
import com.edifecs.servicemanager.dashboard.datastore.DashboardDatastore;
import com.edifecs.servicemanager.dashboard.entity.Dataset;
import com.edifecs.servicemanager.dashboard.entity.Widget;
import com.edifecs.servicemanager.dashboard.entity.WidgetType;
import com.edifecs.servicemanager.dashboard.entity.XBoard;
import com.edifecs.servicemanager.dashboard.util.DashboardVariables;
import com.edifecs.servicemanager.dashboard.util.WidgetJsonResponse;

@Akka(enabled = true)
@CommandHandler
public class WidgetCommandHandler extends AbstractCommandHandler {

	private DashboardDatastore db;

	public WidgetCommandHandler(DashboardDatastore db) {
		super();
		this.db = db;
	}

	@Command
	public List<WidgetType> getWidgetTypes() throws Exception {
		return db.getAll(WidgetType.class);
	}

	@Command
	public WidgetType getWidgetTypeById(
			@Arg(name = "widgetTypeId", description = "widgetTypeId", required = true) String widgetTypeId)
			throws Exception {
		return db.getById(WidgetType.class, Long.valueOf(widgetTypeId));
	}

	@Command
	public List<WidgetType> getWidgetTypesInRange(
			@Arg(name = "startRecord", description = "startRecord", required = true) int startRecord,
			@Arg(name = "recordCount", description = "recordCount", required = true) int recordCount)
			throws Exception {
		return db.getRange(WidgetType.class, startRecord, recordCount);
	}

	@Command
	public Object getWidgets() throws Exception {
		return WidgetJsonResponse.widgetsToJsonResp(db.getAll(Widget.class));
	}

	@Command
	public Object getWidgetsForWidgetType(
			@Arg(name = "widgetTypeId", description = "widgetTypeId", required = true) String widgetTypeId)
			throws Exception {
		return WidgetJsonResponse.widgetsToJsonResp(db
				.getWidgetsForWidgetType(Long.valueOf(widgetTypeId)));
	}

	@Command
	public Object getWidget(
			@Arg(name = "widgetId", description = "widgetId", required = true) String widgetId)
			throws Exception {
		return WidgetJsonResponse.widgetToJsonResp(db.getById(Widget.class,
				Long.valueOf(widgetId)));
	}

	@Command
	public boolean removeWidget(
			@Arg(name = "widgetId", description = "widgetId", required = true) String widgetId)
			throws Exception {

		Widget widget = db.getById(Widget.class, Long.valueOf(widgetId));

		// remove widget from xboard
		for (XBoard xb : widget.getXboards()) {
			System.out.println("removing widget from xbaords");
			xb.getWidgets().remove(widget);
			db.update(xb);
		}

		// remove dataset from this wigdet
		Dataset d = widget.getDataset();
		d.getWidgets().remove(widget);
		db.update(d);

		return db.delete(widget);
	}

	@Command
	public long createWidget(
			@Arg(name = "datasetId", description = "datasetId", required = false) String datasetId,
			@Arg(name = "widgetTypeId", description = "widgetTypeId", required = true) String widgetTypeId,
			@Arg(name = "widgetProperties", description = "widgetProperties", required = true) HashMap<String, Object> widgetProperties)
			throws Exception {

		// TODO : fix map

		WidgetType widgetType = db.getById(WidgetType.class,
				Long.valueOf(widgetTypeId));

		PropertiesHelper.checkRequiredPropertiesTest(widgetProperties,
				widgetType.getProperties());

		Widget widget = new Widget();
		widget.setWidgetType(widgetType);
		widget.setParameters(PropertiesHelper.fetchParametersTest(
				widgetProperties, db));
		widget.setName(widgetProperties.get(DashboardVariables.PARATMETER_NAME)
				.toString());
		// TODO : VERFIY 1.0

		Long id = null;
		if (null != datasetId) {
			Dataset d = db.getById(Dataset.class, Long.valueOf(datasetId));
			d.getWidgets().add(widget);
			widget.setDataset(d);

			id = db.create(widget);
			db.update(d);
		} else {
			id = db.create(widget);
		}

		return id;
	}

	@Command
	public Widget updateWidget(
			@Arg(name = "widgetId", description = "widgetId", required = true) String widgetId,
			@Arg(name = "widgetProperties", description = "widgetProperties", required = true) HashMap<String, String> widgetProperties)
			throws Exception {

		Widget widget = db.getById(Widget.class, Long.valueOf(widgetId));

		WidgetType widgetType = widget.getWidgetType();

		PropertiesHelper.checkRequiredProperties(widgetProperties,
				widgetType.getProperties());

		widget.setParameters(PropertiesHelper.fetchParameters(widgetProperties,
				db));

		db.update(widget);
		return widget;
	}

}
