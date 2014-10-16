package com.edifecs.servicemanager.dashboard.caching;

import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;

import com.edifecs.servicemanager.dashboard.Filter;
import com.edifecs.servicemanager.dashboard.XBoardException;
import com.edifecs.servicemanager.dashboard.util.Schema;

public interface ICachingService {

	boolean createIndex(String index, String type, String id, String jsonSource);

	Schema createCompositeIndex(String index, String type, String jsonQuery,
			List<String> searchIndices);

	Schema executeESQueryAndCacheData(String index, String type,
			String jsonQuery, String[] searchIndices, Client sourceClient);

	Schema createIndexFromResultSetAndReturnMeta(String index, String type,
			Object resultSet) throws XBoardException;

	Object queryIndex(String index, String type, List<Filter> parameters);

	Object queryIndex(String index, String type, List<Filter> parameters,
			Map<String, Object> fieldMeta) throws XBoardException;

	Object getAll(String index, String type, Map<String, Object> fieldMeta)
			throws XBoardException;

	Object getAll(String index, String type) throws XBoardException;

	CachedResultSet getAllParameterNames(String index, String type,
			Map<String, Object> fieldMeta) throws XBoardException;

	Object getDatasetPreview(String index, String type);

	Object getIndexMeta(String index, String type);

	void deleteIndex(String index);

	void shutdown() throws XBoardException;
}
