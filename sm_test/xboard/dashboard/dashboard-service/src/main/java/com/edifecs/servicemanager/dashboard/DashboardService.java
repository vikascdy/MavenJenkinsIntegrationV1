package com.edifecs.servicemanager.dashboard;

import com.edifecs.servicemanager.annotations.Property;
import com.edifecs.servicemanager.annotations.Property.PropertyType;
import com.edifecs.servicemanager.annotations.Resource;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;
import com.edifecs.servicemanager.dashboard.datastore.DashboardDatastore;
import com.edifecs.servicemanager.dashboard.entity.DatasetType;
import com.edifecs.servicemanager.dashboard.entity.DatastoreType;
import com.edifecs.servicemanager.dashboard.entity.ParameterDef;
import com.edifecs.servicemanager.dashboard.entity.WidgetType;
import com.edifecs.servicemanager.dashboard.entity.XBoardType;
import com.edifecs.servicemanager.dashboard.service.ESClient;
import com.edifecs.servicemanager.dashboard.service.MysqlService;
import com.edifecs.servicemanager.dashboard.util.DashboardVariables;

@Service(name = "Dashboard Service",
	version = "1.0",
	description = "Dashboard Service",
	properties = {
		@Property(name = "environment", propertyType = PropertyType.STRING, description = "define environment production|development ", defaultValue = "development", required = true) 
	},
	resources = {
		@Resource(type = "JDBC Database", name = "Database Server", unique = false)
})
public class DashboardService extends AbstractService {

	private DashboardDatastore db;
	private DatasetCommandHandler datasetCommandHandler;

	@Override
	public void start() throws Exception {
		getLogger().debug("Successfully started service : {}", getId());

		if (getProperties().getProperty("environment") != null) {
			Environment.setMode(getProperties().getProperty("environment"));
			getLogger().debug("setting envirenment to {}",
					getProperties().getProperty("environment"));
		} else {
			getLogger().debug("setting default envirenment to development");
			Environment.setMode("development");
		}

		try {

			db = new DashboardDatastore(getLogger());
			db.initialize(getResources().get("DashBoard Database"));

		} catch (Exception e) {
			e.printStackTrace();
			getLogger().equals(e);
		}
		datasetCommandHandler = new DatasetCommandHandler(db);
		addHandler(new DatastoreCommandHandler(db));
		addHandler(datasetCommandHandler);
		addHandler(new WidgetCommandHandler(db));
		addHandler(new XBoardCommandHandler(db));

		populateData();

	}

	private void populateData() {

		// FIXME: break me, too long

		DatastoreType mysqlDatastoreType = new DatastoreType();
		mysqlDatastoreType.setName(SupportedDataSources.MYSQL);
		mysqlDatastoreType
				.setDescription("DataSource for connection with MySql");
		mysqlDatastoreType.setImpClass(MysqlService.class.getName());
		mysqlDatastoreType.setCategory("DATABASE");

		ParameterDef server = new ParameterDef();
		server.setName(DashboardVariables.HOSTNAME);
		server.setDataType("String");
		server.setDescription("Server Address");
		server.setRequired(true);

		ParameterDef username = new ParameterDef();
		username.setName(DashboardVariables.USERNAME);
		username.setDescription("Database User Name");
		username.setDataType("String");
		username.setRequired(true);

		ParameterDef passwd = new ParameterDef();
		passwd.setName(DashboardVariables.PASSWD);
		passwd.setDescription("Database PassWord");
		passwd.setDataType("String");
		passwd.setRequired(true);

		ParameterDef dbName = new ParameterDef();
		dbName.setName(DashboardVariables.DBNAME);
		dbName.setDescription("Database Name");
		dbName.setDataType("String");
		dbName.setRequired(true);

		ParameterDef port = new ParameterDef();
		port.setName(DashboardVariables.PORT);
		port.setDescription("Port");
		port.setDataType("Integer");
		port.setRequired(true);

		ParameterDef name = new ParameterDef();
		name.setName(DashboardVariables.NAME);
		name.setDataType("String");
		name.setDescription("Name");
		name.setRequired(true);

		ParameterDef desc = new ParameterDef();
		desc.setName(DashboardVariables.DESCRIPTION);
		desc.setDataType("String");
		desc.setDescription("Description");
		desc.setRequired(false);

		ParameterDef cat = new ParameterDef();
		cat.setName(DashboardVariables.CATEGORY);
		cat.setDataType("String");
		cat.setDescription("Category");
		cat.setRequired(true);

		ParameterDef cluster = new ParameterDef();
		cluster.setName(DashboardVariables.ES_CLUSTER_NAME);
		cluster.setDataType("String");
		cluster.setDescription("Cluster Name For ES");
		cluster.setRequired(true);

		mysqlDatastoreType.getProperties().add(server);
		mysqlDatastoreType.getProperties().add(username);
		mysqlDatastoreType.getProperties().add(passwd);
		mysqlDatastoreType.getProperties().add(dbName);
		mysqlDatastoreType.getProperties().add(port);
		mysqlDatastoreType.getProperties().add(name);
		mysqlDatastoreType.getProperties().add(desc);
		mysqlDatastoreType.getProperties().add(cat);

		DatastoreType esDatasourceType = new DatastoreType();
		esDatasourceType.setCategory("NoSQL");
		esDatasourceType.setDescription("Elastic Search Datatsource");
		esDatasourceType.setImpClass(ESClient.class.getName());
		esDatasourceType.setName(SupportedDataSources.ELASTIC_SEARCH);
		esDatasourceType.getProperties().add(server);
		esDatasourceType.getProperties().add(port);
		esDatasourceType.getProperties().add(name);
		esDatasourceType.getProperties().add(desc);
		esDatasourceType.getProperties().add(cluster);
		esDatasourceType.getProperties().add(cat);

		DatasetType datasetType = new DatasetType();
		datasetType.setName("MySQL:Dataset");
		datasetType.setCategory("DATABASE");
		datasetType.setDescription("Dataset Meta for Mysql DataSource");

		DatasetType cDatasetType = new DatasetType();
		cDatasetType.setName("MySQL:Composite:Dataset");
		cDatasetType.setCategory("DATABASE");
		cDatasetType.setDescription("Dataset Meta for Mysql DataSource");

		DatasetType esDatasetType = new DatasetType();
		esDatasetType.setName("ES:Dataset");
		esDatasetType.setCategory("NoSQL");
		esDatasetType
				.setDescription("Dataset Meta for Elastic Search DataSource");

		ParameterDef query = new ParameterDef();
		query.setName(DashboardVariables.DATASET_QUERY);
		query.setDataType("String");
		query.setDescription("Query of Dataset");
		query.setRequired(true);

		ParameterDef cquery = new ParameterDef();
		cquery.setName(DashboardVariables.CACHE_QUERY);
		cquery.setDataType("String");
		cquery.setDescription("ES Query of Dataset");
		cquery.setRequired(true);

		datasetType.getProperties().add(name);
		datasetType.getProperties().add(query);
		datasetType.getProperties().add(desc);

		cDatasetType.getProperties().add(name);
		cDatasetType.getProperties().add(cquery);
		cDatasetType.getProperties().add(desc);

		ParameterDef indices = new ParameterDef();
		indices.setName(DashboardVariables.ES_INDICES);
		indices.setDataType("String");
		indices.setDescription("Comma separated indices");
		indices.setRequired(true);

		esDatasetType.getProperties().add(name);
		esDatasetType.getProperties().add(query);
		esDatasetType.getProperties().add(desc);
		esDatasetType.getProperties().add(indices);

		datasetType.setDatastoreType(mysqlDatastoreType);
		cDatasetType.setDatastoreType(mysqlDatastoreType);
		esDatasetType.setDatastoreType(esDatasourceType);

		// chart widget
		ParameterDef confDef = new ParameterDef();
		confDef.setName(DashboardVariables.CONFIGURATION);
		confDef.setRequired(true);
		confDef.setCategory("Widget");
		confDef.setDataType("String");
		confDef.setDescription("Stores configuration for charts/widgets");

		ParameterDef datasetDef = new ParameterDef();
		datasetDef.setName(DashboardVariables.WIDGET_DATASET);
		datasetDef.setRequired(true);
		datasetDef.setCategory("Widget");
		datasetDef.setDataType("String");
		datasetDef.setDescription("Dataset for widget");

		WidgetType chartWidgetType = new WidgetType();
		chartWidgetType.setName(DashboardVariables.WIDGET_CHART);
		chartWidgetType.setDescription("Widget for charts");
		chartWidgetType.setCategory("Chart:Widget");
		chartWidgetType.getProperties().add(name);
		chartWidgetType.getProperties().add(desc);
		chartWidgetType.getProperties().add(confDef);

		WidgetType textWidgetType = new WidgetType();
		textWidgetType.setName(DashboardVariables.WIDGET_TEXT);
		textWidgetType.setDescription("Widget for text");
		textWidgetType.setCategory("Text:Widget");
		textWidgetType.getProperties().add(name);
		textWidgetType.getProperties().add(desc);
		textWidgetType.getProperties().add(confDef);

		ParameterDef imgBytes = new ParameterDef();
		imgBytes.setName(DashboardVariables.IMAGE_DATA);
		imgBytes.setRequired(true);
		imgBytes.setCategory("Widget");
		imgBytes.setDataType("Inputstream");
		imgBytes.setDescription("BLOB for Images");

		WidgetType imageWidgetType = new WidgetType();
		imageWidgetType.setName(DashboardVariables.WIDGET_IMAGE);
		imageWidgetType.setDescription("Widget for images");
		imageWidgetType.setCategory("Image:Widget");
		imageWidgetType.getProperties().add(name);
		imageWidgetType.getProperties().add(desc);
		imageWidgetType.getProperties().add(confDef);
		imageWidgetType.getProperties().add(imgBytes);

		WidgetType gridWidgetType = new WidgetType();
		gridWidgetType.setName(DashboardVariables.WIDGET_GRID);
		gridWidgetType.setDescription("Widget for grids");
		gridWidgetType.setCategory("Grid:Widget");
		gridWidgetType.getProperties().add(name);
		gridWidgetType.getProperties().add(desc);
		gridWidgetType.getProperties().add(confDef);

		WidgetType shapeWidgetType = new WidgetType();
		shapeWidgetType.setName(DashboardVariables.WIDGET_SHAPE);
		shapeWidgetType.setDescription("Widget for shapes");
		shapeWidgetType.setCategory("Shape:Widget");
		shapeWidgetType.getProperties().add(name);
		shapeWidgetType.getProperties().add(desc);
		shapeWidgetType.getProperties().add(confDef);

		WidgetType embdWidgetType = new WidgetType();
		embdWidgetType.setName(DashboardVariables.WIDGET_EMDB);
		embdWidgetType.setDescription("Widget for embeding widgets");
		embdWidgetType.setCategory("Embed:Widget");
		embdWidgetType.getProperties().add(name);
		embdWidgetType.getProperties().add(desc);
		embdWidgetType.getProperties().add(confDef);

		XBoardType xBoardType = new XBoardType();
		xBoardType.setName("XBoardType");
		xBoardType.setCategory("XBoardType");
		xBoardType.setDescription("Meta for xboard");
		xBoardType.getProperties().add(name);
		xBoardType.getProperties().add(desc);
		xBoardType.getProperties().add(cat);
		xBoardType.getProperties().add(confDef);

		try {
			// check if meta exists in case of persisted database.
			// TODO: not very efficient logic

			if (db.getAll(DatastoreType.class).size() < 1) {
				db.create(mysqlDatastoreType);
				db.create(datasetType);
				// reusing common fields, FIXME
				name.setId(0);
				desc.setId(0);
				confDef.setId(0);
				db.create(chartWidgetType);
				name.setId(0);
				desc.setId(0);
				confDef.setId(0);
				db.create(textWidgetType);
				name.setId(0);
				desc.setId(0);
				confDef.setId(0);
				db.create(imageWidgetType);
				name.setId(0);
				desc.setId(0);
				confDef.setId(0);
				db.create(gridWidgetType);
				name.setId(0);
				desc.setId(0);
				confDef.setId(0);
				db.create(shapeWidgetType);
				name.setId(0);
				desc.setId(0);
				confDef.setId(0);
				db.create(embdWidgetType);
				name.setId(0);
				desc.setId(0);
				confDef.setId(0);
				db.create(xBoardType);
				name.setId(0);
				desc.setId(0);
				cat.setId(0);
				server.setId(0);
				port.setId(0);
				db.create(esDatasourceType);
				name.setId(0);
				desc.setId(0);
				query.setId(0);
				db.create(esDatasetType);
			} else {
				System.out.println("Not persisting, items already exist");
			}
		} catch (XBoardException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		getLogger().debug("Successfully stopped service : {}", getId());
		if (null != datasetCommandHandler)
			datasetCommandHandler.ESShutdown();
		Thread.sleep(1000);

	}
}
