package com.edifecs.servicemanager.dashboard.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.servicemanager.dashboard.DatasetCommandHandler;
import com.edifecs.servicemanager.dashboard.DatastoreCommandHandler;
import com.edifecs.servicemanager.dashboard.Environment;
import com.edifecs.servicemanager.dashboard.Filter;
import com.edifecs.servicemanager.dashboard.WidgetCommandHandler;
import com.edifecs.servicemanager.dashboard.caching.ElasticSearchServer;
import com.edifecs.servicemanager.dashboard.datastore.DashboardDatastore;
import com.edifecs.servicemanager.dashboard.entity.Dataset;
import com.edifecs.servicemanager.dashboard.entity.DatasetType;
import com.edifecs.servicemanager.dashboard.entity.Datasource;
import com.edifecs.servicemanager.dashboard.entity.DatastoreType;
import com.edifecs.servicemanager.dashboard.entity.Parameter;
import com.edifecs.servicemanager.dashboard.entity.ParameterDef;
import com.edifecs.servicemanager.dashboard.entity.WidgetType;
import com.edifecs.servicemanager.dashboard.service.ESClient;
import com.edifecs.servicemanager.dashboard.service.MysqlService;
import com.edifecs.servicemanager.dashboard.util.DashboardVariables;
import com.edifecs.servicemanager.dashboard.util.Schema;

public class DashboardDatastoreTest {

	static DashboardDatastore db;

	@BeforeClass
	public static void setup() {
		Environment.setMode("development");
		db = new DashboardDatastore();
		try {
			db.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void close() {
		db.close();
	}

	public void test() throws Exception {

		DatastoreType datastoreType = new DatastoreType();
		datastoreType.setName("Check");
		datastoreType.setDescription("Hello");
		datastoreType.setImpClass(this.getClass().toString());
		datastoreType.setCategory("DB");

		long id = db.create(datastoreType);

		System.out.println("###"
				+ db.getById(DatastoreType.class, id).getName());

		System.out.println("###" + db.getAll(DatastoreType.class));

		datastoreType.setName("Updated");

		System.out.println("###" + db.update(datastoreType));

		System.out.println("###"
				+ db.getById(DatastoreType.class, id).getName());

		System.out.println("###" + db.delete(datastoreType));

		// db.close();
		

	}

	public void test2() throws Exception {

		DatastoreType datastoreType = new DatastoreType();
		datastoreType.setName("Check");
		datastoreType.setDescription("Hello");
		datastoreType.setImpClass(this.getClass().toString());
		datastoreType.setCategory("DB");

		ParameterDef def1 = new ParameterDef();
		def1.setName("param1");

		ParameterDef def2 = new ParameterDef();
		def2.setName("param1");

		datastoreType.getProperties().add(def1);
		datastoreType.getProperties().add(def2);

		long id = db.create(datastoreType);

		System.out.println("### Datsource type created" + id);

		Datasource datasource = new Datasource();

		datasource.setDatastoreType(datastoreType);

		for (ParameterDef def : datastoreType.getProperties()) {

			System.out.println("Setting val for prop " + def.getName());
			Parameter p = new Parameter();
			p.setParameterDef(def);
			p.setValue("checking value insetion");

			datasource.getParameters().add(p);
		}

		long id1 = db.create(datasource);

		System.out.println("Datasource created : " + id1);

		System.out.println("verify"
				+ db.getById(Datasource.class, id1).getParameters().get(0)
						.getValue());

		DatasetType datasetType = new DatasetType();
		datasetType.setName("My first Dataset");
		datasetType.setDescription("check");
		datasetType.getProperties().add(def1);
		datasetType.getProperties().add(def2);
		datasetType.setDatastoreType(datastoreType);

		Dataset dataset = new Dataset();
		dataset.setDatasetType(datasetType);

		System.out.println("$$$$" + db.create(dataset));
	}

	@Test
	public void test3() throws Exception {

		DatastoreType mysqlDatastoreType = new DatastoreType();
		mysqlDatastoreType.setName("MySQL");
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

		mysqlDatastoreType.getProperties().add(server);
		mysqlDatastoreType.getProperties().add(username);
		mysqlDatastoreType.getProperties().add(passwd);
		mysqlDatastoreType.getProperties().add(dbName);
		mysqlDatastoreType.getProperties().add(port);

		DatasetType datasetType = new DatasetType();
		datasetType.setName("MySQL:Dataset");
		datasetType.setCategory("DATABASE");
		datasetType.setDescription("Dataset Meta for Mysql DataSource");

		ParameterDef name = new ParameterDef();
		name.setName(DashboardVariables.DATASET_NAME);
		name.setDataType("String");
		name.setDescription("Name of Dataset");
		name.setRequired(true);

		ParameterDef desc = new ParameterDef();
		desc.setName(DashboardVariables.DATASET_DESCRIPTION);
		desc.setDataType("String");
		desc.setDescription("Description of Dataset");
		desc.setRequired(true);

		ParameterDef query = new ParameterDef();
		query.setName(DashboardVariables.DATASET_QUERY);
		query.setDataType("String");
		query.setDescription("Query of Dataset");
		query.setRequired(true);

		datasetType.getProperties().add(name);
		datasetType.getProperties().add(query);
		datasetType.getProperties().add(desc);

		datasetType.setDatastoreType(mysqlDatastoreType);

		ParameterDef confDef = new ParameterDef();
		confDef.setName(DashboardVariables.CONFIGURATION);
		confDef.setRequired(true);
		confDef.setCategory("Widget");
		confDef.setDataType("String");
		confDef.setDescription("Stores configuration for charts/widgets");

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

		long id = db.create(mysqlDatastoreType);
		long datasetTypeId = db.create(datasetType);
		long wid = db.create(imageWidgetType);

		DatastoreCommandHandler dch = new DatastoreCommandHandler(db);
		Map<String, String> map = new HashMap<String, String>();
		map.put("Host", "localhost");
		map.put("User Name", "root");
		map.put("PassWord", "root");
		map.put("Database Name", "classicmodels");
		map.put("Port", "3306");
		long did = dch.createDatasource(String.valueOf(id),
				(HashMap<String, String>) map);

		DatasetCommandHandler dsh = new DatasetCommandHandler(db);
		System.out.println("verify "
				+ dsh.getDatasetTypesForDatasourceType(String.valueOf(id))
						.size());

		List<String> ids = new ArrayList<String>();

		// dataset 1
		map.clear();
		map.put("Name", "My Test Dataset 1");
		map.put("Description", "OR Dtatmart");
		map.put("Query",
				"select p.productName, monthname(o.orderDate) Month, od.quantityOrdered QtyED from (( orders as o inner join orderdetails as od on od.orderNumber = o.orderNumber) inner join products as p on p.productCode = od.productCode) order by p.productName");

		Schema schema = (Schema) dsh.createDataset(String.valueOf(did),
				String.valueOf(datasetTypeId), (HashMap<String, String>) map);
		ids.add(schema.getDatasetId());
		System.out.println(" Id " + schema.getDatasetId());

		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("name", "productLine");
		// map1.put("data", "buyPrice");
		System.out.println(" dataset P result "
				+ dsh.getParameterNames(schema.getDatasetId(),
						(HashMap<String, Object>) map1));

		// widget 1
		WidgetCommandHandler wch = new WidgetCommandHandler(db);
		map1.clear();
		map1.put("Name", "My Test Widget");
		map1.put("Description", "OR Dtatmart");
		map1.put("Configuration", "SELECT * FROM subject");
		map1.put("Image", new FileInputStream(new File(
				"C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg")));
		wch.createWidget(null, "1", (HashMap<String, Object>) map1);

		// System.out.println("widgets : " + wch.getWidgets().size());

		// dataset 2
		map.clear();
		map.put("Name", "My Test Dataset 2");
		map.put("Description", "Testing");
		map.put("Query", "SELECT * FROM subject");

		// schema = (Schema) dsh.createDataset(String.valueOf(did),
		// String.valueOf(datasetTypeId), (HashMap<String, String>) map);
		// ids.add(schema.getDatasetId());

		// System.out.println("check del on dataset 2 "
		// + dsh.deleteDataset(schema.getDatasetId()));

		String q2 = "%7B%22size%22%3A0%2C%22query%22%3A%7B%22match_all%22%3A%7B%7D%7D%2C%22facets%22%3A%7B%22facet_result%22%3A%7B%22terms%22%3A%7B%22fields%22%3A%5B%22MonthName%22%5D%7D%7D%7D%7D";

		map.clear();
		map.put("Name", "My Comp Dataset");
		map.put("Description", "Testing");
		map.put("Query", q2);
		// schema = (Schema) dsh.createCompositeDataset((ArrayList<String>) ids,
		// (HashMap<String, String>) map);
		// System.out.println("success " + schema);
		//
		// System.out.println("check del on c dataset "
		// + dsh.deleteDataset(schema.getDatasetId()));

		// map.clear();
		// map.put("Name", "My Comp Dataset");
		// map.put("Description", "Testing");
		// map.put("Query", q2);
		// System.out.println("success "
		// + dsh.createCompositeDataset((ArrayList<String>) ids,
		// (HashMap<String, String>) map));

		// testESdatasource();

		// testFilters();
	}

	public void testElasticSearchServer(Object obj) {

		ElasticSearchServer esServer = new ElasticSearchServer();
		Client client = esServer.getClient();
		System.out.println("###"
				+ (client.admin().cluster().prepareHealth()
						.setWaitForGreenStatus().execute().actionGet())
						.getStatus());

		IndexResponse response = client.prepareIndex("dashboard", "dataset")
				.setSource(obj.toString()).execute().actionGet();

		System.out.println("action Get " + response.getId());

		// GetResponse getResponse = esServer.getClient()
		// .prepareGet("dashboard", "dataset", response.getId()).execute()
		// .actionGet();
		// System.out.println("###" + getResponse.getSourceAsString());

		SearchResponse searchResponse = client.prepareSearch("dashboard")
				.setTypes("dataset")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.termQuery("OWNER_", "2")) // Query
				.setFrom(0).setSize(60).setExplain(true).execute().actionGet();

		System.out.println("!! " + searchResponse.getHits().getTotalHits());

		for (SearchHit hit : searchResponse.getHits()) {

			System.out.println("$$ " + hit.getExplanation());
			System.out.println("$$ " + hit.getSource());
		}
		esServer.shutdown();
	}

	public void testFilters() throws Exception {

		DatasetCommandHandler dsh = new DatasetCommandHandler(db);

		Filter f1 = new Filter();
		f1.setFieldName("productLine");
		f1.setType("single");
		f1.getValues().add("Motorcycles");
		f1.getValues().add("Classic Cars");

		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("name", "productLine");
		map1.put("data", "buyPrice");

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("fieldName", "productLine");
		map2.put("type", "single");
		map2.put("values", f1.getValues());

		List<HashMap<String, Object>> fs = new ArrayList<HashMap<String, Object>>();
		fs.add((HashMap<String, Object>) map2);

		System.out.println("##"
				+ dsh.filterDatasetWithAggregation("1", fs,
						(HashMap<String, Object>) map1));
	}

	public void testElasticSearchServer() {
		ElasticSearchServer server = new ElasticSearchServer();

		System.out.println("cehck" + server.getClient());
	}

	public void testESdatasource() throws Exception {
		ParameterDef server = new ParameterDef();
		server.setName(DashboardVariables.HOSTNAME);
		server.setDataType("String");
		server.setDescription("Server Address");
		server.setRequired(true);

		ParameterDef name = new ParameterDef();
		name.setName(DashboardVariables.DATASET_NAME);
		name.setDataType("String");
		name.setDescription("Name of Dataset");
		name.setRequired(true);

		ParameterDef port = new ParameterDef();
		port.setName(DashboardVariables.PORT);
		port.setDescription("Port");
		port.setDataType("Integer");
		port.setRequired(true);

		ParameterDef desc = new ParameterDef();
		desc.setName(DashboardVariables.DATASET_DESCRIPTION);
		desc.setDataType("String");
		desc.setDescription("Description of Dataset");
		desc.setRequired(true);

		ParameterDef query = new ParameterDef();
		query.setName(DashboardVariables.DATASET_QUERY);
		query.setDataType("String");
		query.setDescription("Query of Dataset");
		query.setRequired(true);

		ParameterDef cluster = new ParameterDef();
		cluster.setName(DashboardVariables.ES_CLUSTER_NAME);
		cluster.setDataType("String");
		cluster.setDescription("Cluster Name For ES");
		cluster.setRequired(true);

		DatastoreType esDatasourceType = new DatastoreType();
		esDatasourceType.setCategory("NoSQL");
		esDatasourceType.setDescription("Elastic Search Datatsource");
		esDatasourceType.setImpClass(ESClient.class.getName());
		esDatasourceType.setName("Elastic Search");
		esDatasourceType.getProperties().add(server);
		esDatasourceType.getProperties().add(port);
		esDatasourceType.getProperties().add(name);
		esDatasourceType.getProperties().add(desc);
		esDatasourceType.getProperties().add(cluster);

		ParameterDef indices = new ParameterDef();
		indices.setName(DashboardVariables.ES_INDICES);
		indices.setDataType("String");
		indices.setDescription("Comma separated indices");
		indices.setRequired(true);

		DatasetType esDatasetType = new DatasetType();
		esDatasetType.setName("ES:Dataset");
		esDatasetType.setCategory("NoSQL");
		esDatasetType
				.setDescription("Dataset Meta for Elastic Search DataSource");

		esDatasetType.getProperties().add(name);
		esDatasetType.getProperties().add(query);
		esDatasetType.getProperties().add(desc);
		esDatasetType.getProperties().add(indices);

		esDatasetType.setDatastoreType(esDatasourceType);

		long esDatasourceTypeId = db.create(esDatasourceType);
		System.out.println("created esDatasourceTypeId " + esDatasourceTypeId);
		long esDatasetTypeId = db.create(esDatasetType);
		System.out.println("created esDatasetTypeId " + esDatasetTypeId);

		DatastoreCommandHandler dch = new DatastoreCommandHandler(db);
		Map<String, String> map = new HashMap<String, String>();
		map.put("Host", "localhost");
		map.put("Name", "Test ES Datasource");
		map.put("Cluster Name", "elasticsearch");
		map.put("Description", "testing es");
		map.put("Port", "9300");
		long did = dch.createDatasource(String.valueOf(esDatasourceTypeId),
				(HashMap<String, String>) map);
		System.out.println("created datasource " + did);

		DatasetCommandHandler dsh = new DatasetCommandHandler(db);

		// dataset 1
		String q2 = "%7B%22size%22%3A0%2C%22query%22%3A%7B%22match_all%22%3A%7B%7D%7D%2C%22facets%22%3A%7B%22facet_result%22%3A%7B%22terms%22%3A%7B%22fields%22%3A%5B%22MonthName%22%5D%7D%7D%7D%7D";
		String encoded = "%22%7B%5C%22size%5C%22%3A0%2C%5C%22query%5C%22%3A%7B%5C%22match_all%5C%22%3A%7B%7D%7D%2C%5C%22facets%5C%22%3A%7B%5C%22facet_result%5C%22%3A%7B%5C%22terms%5C%22%3A%7B%5C%22fields%5C%22%3A%5B%5C%22MonthName%5C%22%5D%7D%7D%7D%7D%22";

		map.clear();
		map.put("Name", "My Test Dataset 1");
		map.put("Description", "OR Dtatmart");
		map.put("Query", encoded);
		map.put("Indices", "dataset1,dataset2");

		Schema schema = (Schema) dsh.createDataset(String.valueOf(did),
				String.valueOf(esDatasetTypeId), (HashMap<String, String>) map);

		System.out.println("Dataset created schema : " + schema);
	}
}
