package com.edifecs.servicemanager.dashboard;

import java.util.HashMap;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.message.annotations.Arg;
import com.edifecs.message.annotations.Command;
import com.edifecs.message.annotations.JGroups;
import com.edifecs.servicemanager.dashboard.datastore.DashboardDatastore;
import com.edifecs.servicemanager.dashboard.entity.Datasource;
import com.edifecs.servicemanager.dashboard.entity.DatastoreType;
import com.edifecs.servicemanager.dashboard.service.ESClient;
import com.edifecs.servicemanager.dashboard.service.MysqlService;
import com.edifecs.servicemanager.dashboard.util.DashboardVariables;

@Akka(enabled = true)
public class DatastoreCommandHandler extends AbstractCommandHandler {

	private DashboardDatastore db;

	public DatastoreCommandHandler(DashboardDatastore db) {
		super();
		this.db = db;
	}

	@Command
	public List<DatastoreType> getDatasourceTypes() throws Exception {
		return db.getAll(DatastoreType.class);
	}

	@Command
	public DatastoreType getDatasourceTypeById(
			@Arg(name = "datasourceTypeId", description = "datasourceTypeId", required = true) String datasourceTypeId)
			throws Exception {
		return db.getById(DatastoreType.class, Long.valueOf(datasourceTypeId));
	}

	@Command
	public Datasource getDatasourceById(
			@Arg(name = "datasourceId", description = "datasourceId", required = true) String datasourceId)
			throws Exception {
		return db.getById(Datasource.class, Long.valueOf(datasourceId));
	}

	@Command
	public boolean removeDatasource(
			@Arg(name = "datasourceId", description = "datasourceId", required = true) String datasourceId)
			throws Exception {
		return db.delete(db.getById(Datasource.class,
				Long.valueOf(datasourceId)));
	}

	@Command
	public List<DatastoreType> getDatasourceTypesInRange(
			@Arg(name = "startRecord", description = "startRecord", required = true) int startRecord,
			@Arg(name = "recordCount", description = "recordCount", required = true) int recordCount)
			throws Exception {
		return db.getRange(DatastoreType.class, startRecord, recordCount);
	}

	@Command
	public List<Datasource> getDatasources() throws Exception {
		return db.getAll(Datasource.class);
	}

	@Command
	public long createDatasource(
			@Arg(name = "datasourceTypeId", description = "datasourceTypeId", required = true) String datasourceTypeId,
			@Arg(name = "datasourceProperties", description = "datasourceProperties", required = true) HashMap<String, String> datasourceProperties)
			throws Exception {

		DatastoreType datasourceType = db.getById(DatastoreType.class,
				Long.valueOf(datasourceTypeId));

		PropertiesHelper.checkRequiredProperties(datasourceProperties,
				datasourceType.getProperties());

		Datasource datasource = new Datasource();
		datasource.setDatastoreType(datasourceType);
		datasource.setName(datasourceProperties
				.get(DashboardVariables.PARATMETER_NAME));
		datasource.setParameters(PropertiesHelper.fetchParameters(
				datasourceProperties, db));

		if (validateDatasource(datasource)) {
			Long id = null;
			try {
				id = db.create(datasource);
			} catch (ConstraintViolationException e) {
				e.printStackTrace();
				throw new XBoardException(
						"Datasource with the name {"
								+ datasourceProperties
										.get(DashboardVariables.PARATMETER_NAME)
								+ "} already exists");
			}

			return id;
		} else
			throw new XBoardException("Inavlid DataSource");
	}

	private boolean validateDatasource(Datasource datasource) throws Exception {

		switch (datasource.getDatastoreType().getName()) {
		case SupportedDataSources.MYSQL:

			MysqlService mysqlService = new MysqlService();
			mysqlService.setHostname(PropertiesHelper
					.fetchPropertyFromDatasource(datasource,
							DashboardVariables.HOSTNAME));
			mysqlService.setDbName(PropertiesHelper
					.fetchPropertyFromDatasource(datasource,
							DashboardVariables.DBNAME));
			mysqlService.setUsername(PropertiesHelper
					.fetchPropertyFromDatasource(datasource,
							DashboardVariables.USERNAME));
			mysqlService.setPasswd(PropertiesHelper
					.fetchPropertyFromDatasource(datasource,
							DashboardVariables.PASSWD));
			mysqlService.setPort(Integer.valueOf(PropertiesHelper
					.fetchPropertyFromDatasource(datasource,
							DashboardVariables.PORT)));

			return mysqlService.testConnection();

		case SupportedDataSources.ELASTIC_SEARCH:
			ESClient esClient = new ESClient();
			esClient.setClusterName(PropertiesHelper
					.fetchPropertyFromDatasource(datasource,
							DashboardVariables.ES_CLUSTER_NAME));
			esClient.setHost(PropertiesHelper.fetchPropertyFromDatasource(
					datasource, DashboardVariables.HOSTNAME));
			esClient.setPort(Integer.valueOf(PropertiesHelper
					.fetchPropertyFromDatasource(datasource,
							DashboardVariables.PORT)));

			return esClient.testConnection();
		default:
			throw new XBoardException("Data Source Type Not Supported "
					+ datasource.getDatastoreType().getName());
		}
	}

	@Command
	public Object getSchema(
			@Arg(name = "datasourceId", description = "datasourceId", required = true) String datasourceId)
			throws Exception {

		// TODO : add support for other datasources

		Datasource datasource = db.getById(Datasource.class,
				Long.valueOf(datasourceId));

		MysqlService mysqlService = new MysqlService();
		mysqlService.setHostname(PropertiesHelper.fetchPropertyFromDatasource(
				datasource, DashboardVariables.HOSTNAME));
		mysqlService.setDbName(PropertiesHelper.fetchPropertyFromDatasource(
				datasource, DashboardVariables.DBNAME));
		mysqlService.setUsername(PropertiesHelper.fetchPropertyFromDatasource(
				datasource, DashboardVariables.USERNAME));
		mysqlService.setPasswd(PropertiesHelper.fetchPropertyFromDatasource(
				datasource, DashboardVariables.PASSWD));
		mysqlService.setPort(Integer.valueOf(PropertiesHelper
				.fetchPropertyFromDatasource(datasource,
						DashboardVariables.PORT)));

		return mysqlService.getSchema();
	}
}
