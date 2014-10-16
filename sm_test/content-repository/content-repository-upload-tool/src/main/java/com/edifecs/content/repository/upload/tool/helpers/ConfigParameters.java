package com.edifecs.content.repository.upload.tool.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ConfigParameters {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String CONFIGURATION_PROPERTIES_CLUSTER_NAME = "cluster.name";

	private static final String CONFIGURATION_PROPERTIES_CR_SERVICE_NAME = "cr.service.name";
	
	private static final String CONFIGURATION_PROPERTIES_CR_UPLOAD_PATH = "cr.upload.path";
	
	private static final String CONFIGURATION_PROPERTIES_CR_META_PATH = "cr.meta.path";
	private static final String CONFIGURATION_PROPERTIES_CR_BUNDLE_PATH = "cr.bundle.path";
	private static final String CONFIGURATION_PROPERTIES_CR_PRODUCT_ARTIFACTS_PATH = "cr.product.artifacts.path";
	private static final String CONFIGURATION_PROPERTIES_CR_ARTIFACTS_PATH = "cr.artifacts.path";
	
	private static final String CONFIGURATION_PROPERTIES_SERVER_NAME = "server.name";
	private static final String CONFIGURATION_PROPERTIES_NODE_NAME = "node.name";

	private static final String CONFIGURATION_PROPERTIES_MONITOR_DIRECTORY = "directories.to.monitor";
	private static final String CONFIGURATION_PROPERTIES_UPLOAD_DIRECTORY = "directories.to.upload";
	private static final String CONFIGURATION_PROPERTIES_PRODUCT_UPLOAD_DIRECTORY = "product.directories.to.upload";
	
	private static final String CONFIGURATION_PROPERTIES_WATCH_TIME = "directory.scan.frequency.in.secs";

	private static final String CONFIGURATION_PROPERTIES_CR_UPLOADED_FILES_LIST = "cr.uploaded.files.list";

	private static final String CONFIG_FILE = "config.properties";
	private static final String SETTINGS_FILE = "settings.properties";
	
	private Properties properties;
	
	public ConfigParameters() throws Exception {
		initializeProperties();
	}

	public static Properties loadConfigProperties(
			InputStream configPropertiesStream) throws IOException {

		Properties configProperties = new Properties();

		configProperties.load(configPropertiesStream);

		configPropertiesStream.close();

		return configProperties;
	}

	/**
	 * Retrieves the value cluster.name from the settings.properties file.
	 * 
	 * @return Name of the running cluster to get connected to
	 */
	public static String getClusterName(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CLUSTER_NAME);
	}

	/**
	 * Retrieves the value cr.service.name from the settings.properties file.
	 * 
	 * @return Service Name of Content Repository
	 */
	public static String getContentRepoServiceName(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CR_SERVICE_NAME);
	}

	/**
	 * Retrieves the value cr.upload.path from the settings.properties file.
	 * 
	 * @return Upload Path for Content Repository
	 */
	public static String getContentRepoUploadPath(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CR_UPLOAD_PATH);
	}

	/**
	 * Retrieves the value cr.meta.path from the settings.properties file.
	 * 
	 * @return Bundle Upload Path for Content Repository
	 */
	public static String getContentRepoMetaPath(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CR_META_PATH);
	}
	
	/**
	 * Retrieves the value cr.bundle.path from the settings.properties file.
	 * 
	 * @return Meta Upload Path for Content Repository
	 */
	public static String getContentRepoBundlePath(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CR_BUNDLE_PATH);
	}
	
	/**
	 * Retrieves the value cr.product.artifacts.path from the settings.properties file.
	 * 
	 * @return Artifacts Root Upload Path for Content Repository
	 */
	public static String getContentRepoProductArtifactsPath(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CR_PRODUCT_ARTIFACTS_PATH);
	}
	
	/**
	 * Retrieves the value cr.artifacts.path from the settings.properties file.
	 * 
	 * @return Artifacts Product Root Upload Path for Content Repository
	 */
	public static String getContentRepoArtifactsPath(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CR_ARTIFACTS_PATH);
	}
	
	/**
	 * Retrieves the value server.name from the settings.properties file.
	 * 
	 * @return Server Name for the Upload Tool
	 */
	public static String getServerName(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_SERVER_NAME);
	}

	/**
	 * Retrieves the value node.name from the settings.properties file.
	 * 
	 * @return Node Name for the Upload Tool
	 */
	public static String getNodeName(Properties configProperties) {
		return configProperties.getProperty(CONFIGURATION_PROPERTIES_NODE_NAME);
	}

	/**
	 * Retrieves the value directories.to.monitor from the settings.properties
	 * file.
	 * 
	 * @return Directories to be monitored for file upload to CR
	 */
	public static String[] getMonitoringDir(Properties configProperties) {
		String str = configProperties
				.getProperty(CONFIGURATION_PROPERTIES_MONITOR_DIRECTORY);
		return str.split(",");
	}

	/**
	 * Retrieves the value directories.to.upload from the settings.properties
	 * file.
	 * 
	 * @return Directories to be uploaded to CR
	 */
	public static String[] getDirToBeUploaded(Properties configProperties) {
		String str = configProperties
				.getProperty(CONFIGURATION_PROPERTIES_UPLOAD_DIRECTORY);
		return str.split(",");
	}
	
	/**
	 * Retrieves the value product.directories.to.upload from the settings.properties
	 * file.
	 * 
	 * @return Product Package Directories to be uploaded to CR
	 */
	public static String[] getProductDirToBeUploaded(Properties configProperties) {
		String str = configProperties
				.getProperty(CONFIGURATION_PROPERTIES_PRODUCT_UPLOAD_DIRECTORY);
		return str.split(",");
	}
	
	/**
	 * Retrieves the value directory.scan.frequency.in.secs from the
	 * settings.properties file.
	 * 
	 * @return Scan Frequency of Directories
	 */
	public static String getWatchTime(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_WATCH_TIME);
	}

	/**
	 * Retrieves the value cr.uploaded.files.list from the settings.properties
	 * file.
	 * 
	 * @return Uploaded Files List file for reference
	 */
	public static String getUploadedFilesList(Properties configProperties) {
		return configProperties
				.getProperty(CONFIGURATION_PROPERTIES_CR_UPLOADED_FILES_LIST);
	}

	public Properties getProperties() {
		return properties;
	}
	
	private void initializeProperties() throws Exception {
		properties = new Properties();
		
		try {
			InputStream clusterConfig = getClass().getClassLoader()
					.getResourceAsStream(CONFIG_FILE);
			properties.putAll(ConfigParameters.loadConfigProperties(clusterConfig));
			
			logger.info("Properties loaded from " + CONFIG_FILE);
		} catch (Exception e) {
			logger.error("No " + CONFIG_FILE + " file exists.");
		}
		
		String customConfig = CONFIG_FILE + "." + InetAddress.getLocalHost().getHostName().toLowerCase();
		try {
			InputStream customClusterConfig = getClass().getClassLoader().getResourceAsStream(customConfig);
			properties.putAll(ConfigParameters.loadConfigProperties(customClusterConfig));
			
			logger.info("Properties loaded from " + customConfig);
		} catch (Exception e) {
			logger.error("No " + customConfig + " file exists.");
		}
		
		try {
			InputStream settings = getClass().getClassLoader()
					.getResourceAsStream(SETTINGS_FILE);
			properties.putAll(ConfigParameters.loadConfigProperties(settings));
		} catch (Exception e) {
			throw new Exception("Error in loading Configuration File.", e);
		}
	}
}
