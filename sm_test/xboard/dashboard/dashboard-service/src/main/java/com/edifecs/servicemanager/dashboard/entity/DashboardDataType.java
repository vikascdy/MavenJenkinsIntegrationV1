package com.edifecs.servicemanager.dashboard.entity;

import java.util.HashMap;
import java.util.Map;

public enum DashboardDataType {

	DATASOURCE_TYPE(DatastoreType.class), PARAMETER_DEF(ParameterDef.class), DATASOURCE(
			Datasource.class), PARAMETER(Parameter.class), DATASET_TYPE(
			DatasetType.class), DATASET(Dataset.class), WIDGET_TYPE(
			WidgetType.class), XBOARD_TYPE(XBoardType.class), XBOARD(
			XBoard.class), WIDGET(Widget.class);

	private static final Map<Class<? extends DashboardDataObject>, DashboardDataType> byClass = new HashMap<Class<? extends DashboardDataObject>, DashboardDataType>();
	static {
		for (DashboardDataType t : DashboardDataType.values()) {
			byClass.put(t.dataClass, t);
		}
	}

	public static DashboardDataType byClass(
			Class<? extends DashboardDataObject> cls) {
		return byClass.get(cls);
	}

	public final Class<? extends DashboardDataObject> dataClass;

	DashboardDataType(Class<? extends DashboardDataObject> dataClass) {
		this.dataClass = dataClass;
	}
}
