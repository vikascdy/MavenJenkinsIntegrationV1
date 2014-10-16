package com.edifecs.servicemanager.dashboard;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.elasticsearch.client.Client;
import org.hibernate.exception.ConstraintViolationException;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.message.annotations.Arg;
import com.edifecs.message.annotations.Command;
import com.edifecs.message.annotations.CommandHandler;
import com.edifecs.message.annotations.JGroups;
import com.edifecs.servicemanager.dashboard.caching.ESCachingService;
import com.edifecs.servicemanager.dashboard.caching.ICachingService;
import com.edifecs.servicemanager.dashboard.datastore.DashboardDatastore;
import com.edifecs.servicemanager.dashboard.entity.Dataset;
import com.edifecs.servicemanager.dashboard.entity.DatasetType;
import com.edifecs.servicemanager.dashboard.entity.Datasource;
import com.edifecs.servicemanager.dashboard.entity.Widget;
import com.edifecs.servicemanager.dashboard.entity.XBoard;
import com.edifecs.servicemanager.dashboard.service.ESClient;
import com.edifecs.servicemanager.dashboard.service.MysqlService;
import com.edifecs.servicemanager.dashboard.util.DashboardVariables;
import com.edifecs.servicemanager.dashboard.util.DatasetJsonResponse;

/*
 * 
 * Every ES dataset mapped to Persisted Dataset by id, 
 * e.g: dataset id - 1, then ES index name : dataset1
 * 
 */

@Akka(enabled = true)
@CommandHandler
public class DatasetCommandHandler extends AbstractCommandHandler {

	private DashboardDatastore db;

	// TODO : use interface
	private MysqlService mysqlService;

	private ICachingService cachingService;

	public DatasetCommandHandler(DashboardDatastore db) {
		super();
		this.db = db;
		this.cachingService = new ESCachingService();
	}

	public void ESShutdown() throws Exception {
		cachingService.shutdown();
	}

	@Command
	public List<DatasetType> getDatasetTypes() throws Exception {
		return db.getAll(DatasetType.class);
	}

	@Command
	public List<DatasetType> getDatasetTypesForDatasourceType(
			@Arg(name = "datasourceTypeId", description = "datasourceTypeId", required = true) String datasourceTypeId)
			throws Exception {
		return db.getDatasetTypesForDatasourceType(Long
				.valueOf(datasourceTypeId));
	}

	@Command
	public DatasetType getDatasetTypeById(
			@Arg(name = "datasetTypeId", description = "datasetTypeId", required = true) String datasetTypeId)
			throws Exception {
		return db.getById(DatasetType.class, Long.valueOf(datasetTypeId));
	}

	@Command
	public Object getDatasets() throws Exception {
		return DatasetJsonResponse.datasetsToJsonResp(db.getAll(Dataset.class));
	}

	@Command
	public Object getDatasetById(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId)
			throws Exception {
		return DatasetJsonResponse.datasetToJsonResp(db.getById(Dataset.class,
				Long.valueOf(datasetId)));
	}

	@Command
	public Object getDatasetMeta(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId)
			throws Exception {
		return cachingService.getIndexMeta("dataset" + datasetId, datasetId);
	}

	@Command
	public boolean removeDataset(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId)
			throws Exception {
		Dataset dataset = db.getById(Dataset.class, Long.valueOf(datasetId));

		// remove all widgets using this dataset
		// remove widget from xboard
		for (Widget w : dataset.getWidgets())
			for (XBoard xb : w.getXboards()) {
				System.out.println("removing widget from xbaords");
				xb.getWidgets().remove(w);
				db.update(xb);
			}

		return db.delete(dataset);
	}

	@Command
	public Object getDatasetsInRange(
			@Arg(name = "startRecord", description = "startRecord", required = true) int startRecord,
			@Arg(name = "recordCount", description = "recordCount", required = true) int recordCount)
			throws Exception {
		return DatasetJsonResponse.datasetsToJsonResp(db.getRange(
				Dataset.class, startRecord, recordCount));
	}

	@Command
	public Object createDataset(
			@Arg(name = "datasourceId", description = "datasourceId", required = true) String datasourceId,
			@Arg(name = "datasetTypeId", description = "datasetTypeId", required = true) String datasetTypeId,
			@Arg(name = "datasetProperties", description = "datasetProperties", required = true) HashMap<String, String> datasetProperties)
			throws Exception {

		// TODO : verify if datasourceId should be a property of dataSetType and
		// thus extracted from datasetProperties #1.0
		Datasource datasource = db.getById(Datasource.class,
				Long.valueOf(datasourceId));

		DatasetType datasetType = db.getById(DatasetType.class,
				Long.valueOf(datasetTypeId));

		PropertiesHelper.checkRequiredProperties(datasetProperties,
				datasetType.getProperties());

		String query = datasetProperties.get(DashboardVariables.DATASET_QUERY);

		// persist dataset
		Dataset dataset = new Dataset();
		dataset.setDatasetType(datasetType);
		dataset.setDatasource(datasource);
		dataset.setParameters(PropertiesHelper.fetchParameters(
				datasetProperties, db));
		dataset.setComposite(false);
		dataset.setName(datasetProperties
				.get(DashboardVariables.PARATMETER_NAME));

		Long datasetId = null;
		datasetId = db.create(dataset);
		String index = "dataset" + datasetId;
		Object resp = null;

		try {
			switch (datasource.getDatastoreType().getName()) {
			case SupportedDataSources.MYSQL:

				mysqlService = new MysqlService();
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

				Object result = mysqlService.executeQuery(query);

				resp = cachingService.createIndexFromResultSetAndReturnMeta(
						index, String.valueOf(dataset.getId()), result);
				break;

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

				String indicesStr = datasetProperties
						.get(DashboardVariables.ES_INDICES);
				String[] indices;
				if (indicesStr.contains(","))
					indices = indicesStr.split(",");
				else {
					indices = new String[1];
					indices[0] = indicesStr;
				}

				Client sourceClient = esClient.getInstance();

				query = decodeESQuery(query);
				datasetProperties.put(DashboardVariables.DATASET_QUERY, query);

				resp = cachingService.executeESQueryAndCacheData(index,
						String.valueOf(dataset.getId()), query, indices,
						sourceClient);

				break;

			default:
				throw new XBoardException("Data Source Type Not Supported "
						+ datasource.getDatastoreType().getName());
			}
		} catch (Exception e) {
			dataset.setId(datasetId);
			db.delete(dataset);
			throw new XBoardException(
					"Exception occured while indexing dataset id : "
							+ datasetId + " cause : " + e.getMessage());
		}
		return resp;

	}

	@Command
	public Object getDatasetResult(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId)
			throws Exception {

		Object result;
		String index = "dataset" + datasetId;
		result = cachingService.getAll(index, datasetId);
		return result;
	}

	@Command
	public Object getAggregatedDatasetResult(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId,
			@Arg(name = "fieldMeta", description = "fieldMeta", required = true) HashMap<String, Object> fieldMeta)
			throws Exception {

		Object result;
		String index = "dataset" + datasetId;
		result = cachingService.getAll(index, datasetId, fieldMeta);
		return result;
	}

	@Command
	public Object getParameterNames(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId,
			@Arg(name = "fieldMeta", description = "fieldMeta", required = true) HashMap<String, Object> fieldMeta)
			throws Exception {

		Object result;
		String index = "dataset" + datasetId;
		result = cachingService.getAllParameterNames(index, datasetId,
				fieldMeta);
		return result;
	}

	@Command
	public Object getDatasetPreview(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId)
			throws Exception {

		Object result;
		String index = "dataset" + datasetId;
		result = cachingService.getDatasetPreview(index, datasetId);
		return result;
	}

	@Command
	public Object filterDataset(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId,
			@Arg(name = "parameters", description = "parameters", required = true) List<HashMap<String, Object>> parameters) {

		String index = "dataset" + datasetId;
		return cachingService.queryIndex(index, datasetId,
				fetchParameters(parameters));

	}

	@Command
	public Object filterDatasetWithAggregation(
			@Arg(name = "datasetId", description = "datasetId", required = true) String datasetId,
			@Arg(name = "parameters", description = "parameters", required = true) List<HashMap<String, Object>> parameters,
			@Arg(name = "fieldMeta", description = "fieldMeta", required = true) HashMap<String, Object> fieldMeta)
			throws Exception {

		String index = "dataset" + datasetId;
		return cachingService.queryIndex(index, datasetId,
				fetchParameters(parameters), fieldMeta);

	}

	@Command
	public Object createCompositeDataset(
			@Arg(name = "datasetIds", description = "datasetIds", required = true) ArrayList<String> datasetIds,
			@Arg(name = "datasetProperties", description = "datasetProperties", required = true) HashMap<String, String> datasetProperties)
			throws Exception {

		String query = datasetProperties.get(DashboardVariables.DATASET_QUERY);

		// remove the extra double quotes around query.
		System.out.println("Raw Query : " + query);
		query = decodeESQuery(query);
		datasetProperties.put(DashboardVariables.DATASET_QUERY, query);

		Dataset cDataset = new Dataset();
		cDataset.setParameters(PropertiesHelper.fetchParameters(
				datasetProperties, db));
		cDataset.setComposite(true);
		cDataset.setCompositeDataset(cDataset);
		cDataset.setName(datasetProperties
				.get(DashboardVariables.PARATMETER_NAME));

		List<String> searchIndices = new ArrayList<String>();
		for (String id : datasetIds) {
			Dataset d = db.getById(Dataset.class, Long.valueOf(id));
			cDataset.getDatasets().add(d);
			searchIndices.add("dataset" + id);
		}

		Long cDatasetId = null;
		try {
			cDatasetId = db.create(cDataset);
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			throw new XBoardException("Dataset with the name {"
					+ datasetProperties.get(DashboardVariables.PARATMETER_NAME)
					+ "} already exists");
		}
		String index = "dataset" + cDatasetId;

		return cachingService.createCompositeIndex(index,
				String.valueOf(cDataset.getId()), query, searchIndices);

	}

	@SuppressWarnings("unchecked")
	private List<Filter> fetchParameters(
			List<HashMap<String, Object>> parameters) {

		Iterator<HashMap<String, Object>> iterator = parameters.iterator();
		List<Filter> filters = new ArrayList<Filter>();

		while (iterator.hasNext()) {

			Map<String, Object> map = iterator.next();
			Filter f = new Filter();
			f.setFieldName(map.get("fieldName").toString());
			f.setType(map.get("type").toString());
			f.setValues((List<String>) map.get("values"));
			filters.add(f);
		}
		return filters;
	}

	private String decodeESQuery(String query)
			throws UnsupportedEncodingException {
		query = query.substring(3, query.lastIndexOf("%"));
		query = StringEscapeUtils.unescapeJava(URLDecoder
				.decode(query, "UTF-8"));
		return query;
	}
}
