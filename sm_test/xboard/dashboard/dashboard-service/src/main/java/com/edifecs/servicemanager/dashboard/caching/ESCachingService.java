package com.edifecs.servicemanager.dashboard.caching;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.histogram.HistogramFacet;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.statistical.StatisticalFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.servicemanager.dashboard.Environment;
import com.edifecs.servicemanager.dashboard.Filter;
import com.edifecs.servicemanager.dashboard.XBoardException;
import com.edifecs.servicemanager.dashboard.caching.DataDictionary.Dictionary;
import com.edifecs.servicemanager.dashboard.service.ESClient;
import com.edifecs.servicemanager.dashboard.util.Schema;

public class ESCachingService implements ICachingService {

	private Client client;

	private Logger logger;

	public ESCachingService() {
		super();

		logger = LoggerFactory.getLogger(getClass());
		if (Environment.getMode().equalsIgnoreCase("development")) {
			logger.debug("using embedded elastic search in developemnt mode.");

			ElasticSearchServer esServer = new ElasticSearchServer();
			this.client = esServer.getClient();
		} else {
			logger.debug("using transport client for elastic search in production mode. \n "
					+ "connecting to cluster elasticsearch running on 192.241.211.21");

			ESClient es = new ESClient();
			es.setClusterName("elasticsearch");
			es.setHost("192.241.211.21");
			es.setPort(9300);

			logger.debug("successfully connected To Elastic Search Server.");
			try {
				this.client = es.getInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean createIndex(String index, String type, String id,
			String jsonSource) {

		IndexResponse response = client.prepareIndex(index, type, id)
				.setSource(jsonSource).execute().actionGet();

		if (null != response)
			return true;
		else
			return false;
	}

	@Override
	public CachedResultSet queryIndex(String index, String type,
			List<Filter> parameters) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<FilterBuilder> filters;

		SearchRequestBuilder searchReq = new SearchRequestBuilder(client);
		searchReq.setIndices(index);
		searchReq.setTypes(type);
		searchReq.setFrom(0);
		searchReq.setSize(60);

		filters = parseParameters(parameters);
		FilterBuilder[] fbs = new FilterBuilder[filters.size()];
		searchReq.setFilter(FilterBuilders.andFilter(filters.toArray(fbs)));

		logger.debug("Filter Query +++ " + searchReq.toString());

		SearchResponse resp = searchReq.execute().actionGet();
		System.out.println("Filter Results Hits +++"
				+ resp.getHits().getTotalHits());

		for (SearchHit hit : resp.getHits()) {
			result.add(hit.getSource());
		}

		logger.debug("Filter Rows Found " + result.size());

		CachedResultSet esResult = new CachedResultSet();
		esResult.setData(result);
		esResult.setMeta(getTypeMeta(index, type));
		esResult.setIndex(index);
		esResult.setType(type);
		esResult.setDatasetId(type);

		return esResult;
	}

	@Override
	public CachedResultSet queryIndex(String index, String type,
			List<Filter> parameters, Map<String, Object> fieldMeta)
			throws XBoardException {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<FilterBuilder> filters;

		SearchRequestBuilder searchReq = new SearchRequestBuilder(client);
		searchReq.setIndices(index);
		searchReq.setTypes(type);
		searchReq.setFrom(0);
		searchReq.setSize(1000);

		filters = parseParameters(parameters);
		FilterBuilder[] fbs = new FilterBuilder[filters.size()];
		// searchReq.setFilter(FilterBuilders.andFilter(filters.toArray(fbs)));

		logger.debug("Filter Query +++ " + searchReq.toString());

		String termField;
		String dataField;

		if (fieldMeta.containsKey("name") && fieldMeta.containsKey("data")) {
			termField = fieldMeta.get("name").toString();
			dataField = fieldMeta.get("data").toString();

			TermsStatsFacetBuilder termsStatsFacet = FacetBuilders
					.termsStatsFacet("aggregation").keyField(termField)
					.valueField(dataField).size(0);

			termsStatsFacet.facetFilter(FilterBuilders.andFilter(filters
					.toArray(new FilterBuilder[filters.size()])));

			searchReq.addFacet(termsStatsFacet);
		} else
			throw new XBoardException(
					"missing NAME or DATA values for fieldMeta");

		SearchResponse resp = searchReq.execute().actionGet();
		System.out.println("Filter Results Hits +++"
				+ resp.getHits().getTotalHits());

		// for (SearchHit hit : resp.getHits()) {
		// result.add(hit.getSource());
		// }

		if (null != resp.getFacets()) {

			for (Facet f : resp.getFacets().facets()) {

				logger.debug(" Facet Name : " + f.getName());
				logger.debug("Facet Type : " + f.getType());

				// Terms
				if (f.getType().equalsIgnoreCase("terms_stats")) {
					TermsStatsFacet termsFacet = (TermsStatsFacet) resp
							.getFacets().facetsAsMap().get(f.getName());

					for (TermsStatsFacet.Entry terms : termsFacet) {
						Map<String, Object> json = new HashMap<String, Object>();
						// json.put(f.getName(), terms.getTerm());
						json.put(termField, terms.getTerm().string());
						json.put(dataField, terms.getTotal());
						result.add(json);
					}
				}
			}
		}

		logger.debug("Filter Rows Found " + result.size());

		CachedResultSet esResult = new CachedResultSet();
		esResult.setData(result);
		esResult.setMeta(getTypeMeta(index, type));
		esResult.setIndex(index);
		esResult.setType(type);
		esResult.setDatasetId(type);

		return esResult;
	}

	private List<FilterBuilder> parseParameters(List<Filter> parameters) {
		List<FilterBuilder> filters = new ArrayList<FilterBuilder>();
		for (Filter f : parameters) {
			// only single and range filters supported
			if (f.getType().equalsIgnoreCase("single")) {
				if (f.getValues().size() > 1)
					filters.add(FilterBuilders.inFilter(f.getFieldName(), f
							.getValues().toArray()));
				else
					filters.add(FilterBuilders.inFilter(f.getFieldName(), f
							.getValues().get(0)));
			} else {
				filters.add(FilterBuilders.rangeFilter(f.getFieldName())
						.from(f.getValues().get(0)).to(f.getValues().get(1)));
			}
		}
		return filters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CachedResultSet getAll(String index, String type,
			Map<String, Object> fieldMeta) throws XBoardException {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(
				client);
		searchRequestBuilder.setIndices(index);
		searchRequestBuilder.setSearchType(SearchType.SCAN);
		searchRequestBuilder.setScroll(new TimeValue(120000));
		searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
		searchRequestBuilder.setSize(1000);

		String termField;
		String dataField;

		if (fieldMeta.containsKey("name") && fieldMeta.containsKey("data")) {
			termField = fieldMeta.get("name").toString();
			dataField = fieldMeta.get("data").toString();

			searchRequestBuilder.addFacet(FacetBuilders
					.termsStatsFacet("aggregation").keyField(termField)
					.valueField(dataField).size(0));
		} else
			throw new XBoardException(
					"missing NAME or DATA values for fieldMeta");

		SearchResponse scrollResp = searchRequestBuilder.execute().actionGet();

		// Scroll until no hits are returned
		while (true) {
			scrollResp = searchRequestBuilder.execute().actionGet();

			// for (SearchHit hit : scrollResp.getHits()) {
			// result.add(hit.getSource());
			// }
			if (null != scrollResp.getFacets()) {

				for (Facet f : scrollResp.getFacets().facets()) {

					logger.debug(" Facet Name : " + f.getName());
					logger.debug("Facet Type : " + f.getType());

					// Terms
					if (f.getType().equalsIgnoreCase("terms_stats")) {
						TermsStatsFacet termsFacet = (TermsStatsFacet) scrollResp
								.getFacets().facetsAsMap().get(f.getName());

						for (TermsStatsFacet.Entry terms : termsFacet) {
							Map<String, Object> json = new HashMap<String, Object>();
							// json.put(f.getName(), terms.getTerm());
							json.put(termField, terms.getTerm().string());
							json.put(dataField, terms.getTotal());
							result.add(json);
						}
					}
				}
			}

			// Break condition: No hits are returned
			if (scrollResp.getHits().hits().length == 0) {
				break;
			}
		}

		CachedResultSet esResult = new CachedResultSet();
		esResult.setData(result);
		esResult.setMeta(getTypeMeta(index, type));
		esResult.setIndex(index);
		esResult.setType(type);
		esResult.setDatasetId(type);

		return esResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CachedResultSet getAllParameterNames(String index, String type,
			Map<String, Object> fieldMeta) throws XBoardException {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(
				client);
		searchRequestBuilder.setIndices(index);
		searchRequestBuilder.setSearchType(SearchType.SCAN);
		searchRequestBuilder.setScroll(new TimeValue(120000));
		searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
		searchRequestBuilder.setSize(1000);

		String termField;

		if (fieldMeta.containsKey("name")) {
			termField = fieldMeta.get("name").toString();

			searchRequestBuilder.addFacet(FacetBuilders.termsFacet(
					"aggregation").field(termField));
		} else
			throw new XBoardException(
					"missing NAME or DATA values for fieldMeta");

		SearchResponse scrollResp = searchRequestBuilder.execute().actionGet();

		// Scroll until no hits are returned
		while (true) {
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(1200000)).execute().actionGet();

			// for (SearchHit hit : scrollResp.getHits()) {
			// result.add(hit.getSource());
			// }
			if (null != scrollResp.getFacets()) {

				for (Facet f : scrollResp.getFacets().facets()) {

					logger.debug(" Facet Name : " + f.getName());
					logger.debug("Facet Type : " + f.getType());

					// Terms
					if (f.getType().equalsIgnoreCase("terms")) {
						TermsFacet termsFacet = (TermsFacet) scrollResp
								.getFacets().facetsAsMap().get(f.getName());

						for (TermsFacet.Entry terms : termsFacet) {
							Map<String, Object> json = new HashMap<String, Object>();
							// json.put(f.getName(), terms.getTerm());
							json.put(termField, terms.getTerm().string());
							result.add(json);
						}
					}
				}
			}

			// Break condition: No hits are returned
			if (scrollResp.getHits().hits().length == 0) {
				break;
			}
		}

		CachedResultSet esResult = new CachedResultSet();
		esResult.setData(result);
		// esResult.setMeta(getTypeMeta(index, type));
		esResult.setIndex(index);
		esResult.setType(type);
		esResult.setDatasetId(type);

		return esResult;
	}

	@Override
	public CachedResultSet getAll(String index, String type) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		SearchResponse scrollResp = client.prepareSearch(index).setTypes(type)
				.setSearchType(SearchType.SCAN)
				.setScroll(new TimeValue(120000))
				.setQuery(QueryBuilders.matchAllQuery()).setSize(1000)
				.execute().actionGet();

		// Scroll until no hits are returned
		while (true) {
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(1200000)).execute().actionGet();

			for (SearchHit hit : scrollResp.getHits()) {
				result.add(hit.getSource());
			}

			// Break condition: No hits are returned
			if (scrollResp.getHits().hits().length == 0) {
				break;
			}
		}

		CachedResultSet esResult = new CachedResultSet();
		esResult.setData(result);
		esResult.setMeta(getTypeMeta(index, type));
		esResult.setIndex(index);
		esResult.setType(type);
		esResult.setDatasetId(type);

		return esResult;
	}

	private Map<String, Object> getTypeMeta(String index, String type) {
		try {
			ClusterState cs = client.admin().cluster().prepareState()
					.setFilterIndices(index).execute().actionGet().getState();
			IndexMetaData imd = cs.getMetaData().index(index);

			return imd.mapping(type).getSourceAsMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CachedResultSet getIndexMeta(String index, String type) {

		CachedResultSet result = new CachedResultSet();
		result.setDatasetId(type);
		result.setIndex(index);
		result.setMeta(getTypeMeta(index, type));

		return result;
	}

	@Override
	public Schema createIndexFromResultSetAndReturnMeta(String index,
			String type, Object resultSet) throws XBoardException {

		logger.debug("Extracting data from Result Set");

		BulkRequestBuilder bulkRequest = client.prepareBulk();
		Schema schema = new Schema();
		schema.setDatasetId(type);
		try {
			ResultSet rs = (ResultSet) resultSet;
			ResultSetMetaData rsmd = rs.getMetaData();

			XContentBuilder mapping = XContentFactory.jsonBuilder()
					.startObject();
			mapping.startObject(String.valueOf(type));
			mapping.startObject("properties");

			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>();

				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String colName = rsmd.getColumnLabel(i + 1);
					String columnType = rsmd.getColumnTypeName(i + 1);
					Object colVal;

					Dictionary dataType = DataDictionary
							.getJavaType(columnType);

					if (rs.getRow() == 1) {

						logger.debug("Dataset Column Name : " + colName
								+ " ## Column Type : " + columnType + "\n");

						mapping.startObject(colName);
						mapping.field("type", dataType.toString().toLowerCase());
						if (dataType.equals(Dictionary.DATE))
							mapping.field("format",
									"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd");
						mapping.field("store", "yes");
						mapping.field("index", "analyzed");
						mapping.field("analyzer", "keyword");
						mapping.endObject();
						// .field("index", "analyzed")
						// .field("null_value", "na")

						schema.getMeta().put(colName, dataType.toString());
					}

					switch (dataType) {
					case STRING:
						colVal = rs.getString(colName);
						break;

					case DOUBLE:
						colVal = rs.getDouble(colName);
						break;

					case INTEGER:
						colVal = rs.getInt(colName);
						break;

					case DATE:
						// TODO : verify
						if (null != rs.getDate(colName)) {
							DateFormat df = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							colVal = df.format(rs.getDate(colName));
						} else {
							colVal = null;
						}

						break;
					default:
						colVal = rs.getString(colName);
						break;
					}

					row.put(colName, colVal);
				}

				bulkRequest
						.add(client.prepareIndex(index, type).setSource(row));
			}

			// prepare mapping
			mapping.endObject();
			mapping.endObject();
			mapping.endObject();

			logger.debug("Set mapping for index : {}", index);

			if (!client.admin().indices()
					.exists(new IndicesExistsRequest(index)).actionGet()
					.isExists())
				client.admin().indices().prepareCreate(index)
						.addMapping(type, mapping).execute().actionGet();

			logger.debug("Executing Bulk Request");

			BulkResponse bulkResponse = bulkRequest.execute().actionGet();

			if (bulkResponse.hasFailures()) {
				throw new Exception(bulkResponse.buildFailureMessage());
			}

			logger.debug("Index Created Successfully : " + index);
			// for (BulkItemResponse resp : bulkResponse.getItems()) {
			// System.out
			// .println("::: " + resp.getId() + " - "
			// + resp.getIndex() + " - " + resp.getType()
			// + "::: \r\n");
			// }

		} catch (SQLException e) {
			e.printStackTrace();
			throw new XBoardException("SQL Exception : " + " reason : "
					+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new XBoardException("failed indexing datatset : " + index
					+ " reason : " + e.getMessage());

		}
		return schema;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public Schema createCompositeIndex(String index, String type,
			String jsonQuery, List<String> searchIndices) {
		try {

			BulkRequestBuilder bulkRequest = client.prepareBulk();

			System.out.println("Json Query : " + jsonQuery);

			String[] indices = new String[searchIndices.size()];
			searchIndices.toArray(indices);

			SearchResponse searchResponse = client.search(
					Requests.searchRequest(indices).source(jsonQuery))
					.actionGet();

			System.out.println("Search Resp : \n" + searchResponse);
			if (null != searchResponse.getHits()) {
				for (SearchHit hit : searchResponse.getHits()) {
					bulkRequest.add(client.prepareIndex(index, type).setSource(
							hit.getSource()));
				}
			}
			if (null != searchResponse.getFacets()) {

				for (Facet f : searchResponse.getFacets().facets()) {

					System.out.println(" Facet Name : " + f.getName());
					System.out.println("Facet Type : " + f.getType());

					// Terms
					if (f.getType().equalsIgnoreCase("terms")) {
						TermsFacet termsFacet = (TermsFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						for (TermsFacet.Entry terms : termsFacet) {
							Map<String, Object> json = new HashMap<String, Object>();
							json.put(f.getName(), terms.getTerm());

							json.put("count(" + f.getName() + ")",
									terms.getCount());
							bulkRequest.add(client.prepareIndex(index, type)
									.setSource(json));
						}
					}

					// Ranges
					if (f.getType().equalsIgnoreCase("range")) {
						RangeFacet rangeFacet = (RangeFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						for (RangeFacet.Entry ranges : rangeFacet) {

							Map<String, Object> json = new HashMap<String, Object>();
							json.put("to", ranges.getTo());
							json.put("from", ranges.getFrom());
							json.put("count", ranges.getCount());
							json.put("min", ranges.getMin());
							json.put("max", ranges.getMax());
							json.put("sum", ranges.getTotal());
							json.put("mean", ranges.getMean());

							bulkRequest.add(client.prepareIndex(index, type)
									.setSource(json));
						}
					}

					// Histogram
					if (f.getType().equalsIgnoreCase("histogram")) {
						HistogramFacet histFacet = (HistogramFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						for (HistogramFacet.Entry hists : histFacet) {

							Map<String, Object> json = new HashMap<String, Object>();
							json.put("key", hists.getKey());
							json.put("count", hists.getCount());

							bulkRequest.add(client.prepareIndex(index, type)
									.setSource(json));
						}
					}

					// Statistical
					if (f.getType().equalsIgnoreCase("statistical")) {
						StatisticalFacet statFacet = (StatisticalFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						Map<String, Object> json = new HashMap<String, Object>();
						json.put("name", statFacet.getName());
						json.put("type", statFacet.getType());
						json.put("sum_of_squares", statFacet.getSumOfSquares());
						json.put("std_deviation", statFacet.getStdDeviation());
						json.put("count", statFacet.getCount());
						json.put("variance", statFacet.getVariance());
						json.put("min", statFacet.getMin());
						json.put("max", statFacet.getMax());
						json.put("sum", statFacet.getTotal());
						json.put("mean", statFacet.getMean());

						bulkRequest.add(client.prepareIndex(index, type)
								.setSource(json));
					}
				}
			}

			if (bulkRequest.numberOfActions() > 0) {

				BulkResponse bulkResponse = bulkRequest.execute().actionGet();

				if (bulkResponse.hasFailures()) {
					throw new Exception(bulkResponse.buildFailureMessage());
				}

				// for (BulkItemResponse resp : bulkResponse.getItems()) {
				// System.out.println("::: " + resp.getId() + " - "
				// + resp.getIndex() + " - " + resp.getType()
				// + "::: \r\n");
				// }

				System.out.println("Composite Dataset Created Successfully "
						+ index);
				// Thread.sleep(1000);

				Schema schema = new Schema();
				schema.setDatasetId(type);
				schema.setMeta(getTypeMeta(index, type));

				return schema;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CachedResultSet getDatasetPreview(String index, String type) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		SearchResponse searchResponse = client.prepareSearch(index)
				.setTypes(type).setQuery(QueryBuilders.matchAllQuery())
				.setFrom(0).setSize(100).execute().actionGet();

		System.out.println(" ### " + searchResponse.getHits().getTotalHits());

		for (SearchHit hit : searchResponse.getHits()) {
			result.add(hit.getSource());
		}

		CachedResultSet esResult = new CachedResultSet();
		esResult.setData(result);
		esResult.setMeta(getTypeMeta(index, type));
		esResult.setIndex(index);
		esResult.setType(type);
		esResult.setDatasetId(type);

		return esResult;

	}

	@Override
	public void deleteIndex(String index) {

		DeleteIndexResponse resp = client.admin().indices()
				.delete(new DeleteIndexRequest(index)).actionGet();
		if (!resp.isAcknowledged()) {
			System.out.println("Index wasn't deleted");
		}
	}

	@Override
	public Schema executeESQueryAndCacheData(String index, String type,
			String jsonQuery, String[] searchIndices, Client sourceClient) {
		try {

			BulkRequestBuilder bulkRequest = client.prepareBulk();

			System.out.println("Json Query : " + jsonQuery);

			SearchResponse searchResponse = sourceClient.search(
					Requests.searchRequest(searchIndices).source(jsonQuery))
					.actionGet();

			System.out.println("Search Resp : \n" + searchResponse);
			if (null != searchResponse.getHits()) {
				for (SearchHit hit : searchResponse.getHits()) {
					bulkRequest.add(client.prepareIndex(index, type).setSource(
							hit.getSource()));
				}
			}
			if (null != searchResponse.getFacets()) {

				for (Facet f : searchResponse.getFacets().facets()) {

					System.out.println(" Facet Name : " + f.getName());
					System.out.println("Facet Type : " + f.getType());

					// Terms
					if (f.getType().equalsIgnoreCase("terms")) {
						TermsFacet termsFacet = (TermsFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						for (TermsFacet.Entry terms : termsFacet) {
							Map<String, Object> json = new HashMap<String, Object>();
							json.put(f.getName(), terms.getTerm());

							json.put("count(" + f.getName() + ")",
									terms.getCount());
							bulkRequest.add(client.prepareIndex(index, type)
									.setSource(json));
						}
					}

					// Ranges
					if (f.getType().equalsIgnoreCase("range")) {
						RangeFacet rangeFacet = (RangeFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						for (RangeFacet.Entry ranges : rangeFacet) {

							Map<String, Object> json = new HashMap<String, Object>();
							json.put("to", ranges.getTo());
							json.put("from", ranges.getFrom());
							json.put("count", ranges.getCount());
							json.put("min", ranges.getMin());
							json.put("max", ranges.getMax());
							json.put("sum", ranges.getTotal());
							json.put("mean", ranges.getMean());

							bulkRequest.add(client.prepareIndex(index, type)
									.setSource(json));
						}
					}

					// Histogram
					if (f.getType().equalsIgnoreCase("histogram")) {
						HistogramFacet histFacet = (HistogramFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						for (HistogramFacet.Entry hists : histFacet) {

							Map<String, Object> json = new HashMap<String, Object>();
							json.put("key", hists.getKey());
							json.put("count", hists.getCount());

							bulkRequest.add(client.prepareIndex(index, type)
									.setSource(json));
						}
					}

					// Statistical
					if (f.getType().equalsIgnoreCase("statistical")) {
						StatisticalFacet statFacet = (StatisticalFacet) searchResponse
								.getFacets().facetsAsMap().get(f.getName());

						Map<String, Object> json = new HashMap<String, Object>();
						json.put("name", statFacet.getName());
						json.put("type", statFacet.getType());
						json.put("sum_of_squares", statFacet.getSumOfSquares());
						json.put("std_deviation", statFacet.getStdDeviation());
						json.put("count", statFacet.getCount());
						json.put("variance", statFacet.getVariance());
						json.put("min", statFacet.getMin());
						json.put("max", statFacet.getMax());
						json.put("sum", statFacet.getTotal());
						json.put("mean", statFacet.getMean());

						bulkRequest.add(client.prepareIndex(index, type)
								.setSource(json));
					}
				}
			}

			if (bulkRequest.numberOfActions() > 0) {

				BulkResponse bulkResponse = bulkRequest.execute().actionGet();

				if (bulkResponse.hasFailures()) {
					throw new Exception(bulkResponse.buildFailureMessage());
				}

				System.out.println("Dataset Created Successfully " + index);
				// Thread.sleep(1000);

				Schema schema = new Schema();
				schema.setDatasetId(type);
				schema.setMeta(getTypeMeta(index, type));

				return schema;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void shutdown() throws XBoardException {
		client.close();
	}
}
