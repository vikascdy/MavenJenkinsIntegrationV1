// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

package com.edifecs.core.configuration.helper;

import java.io.File;

public final class SystemVariables {

	/*
	 * Directory Paths
	 */
	public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

	public static String SERVICE_MANAGER_ROOT_PATH =
            new File(WORKING_DIRECTORY).getParent() + File.separator;

	public static final String ROOT_PATH =
            new File(SERVICE_MANAGER_ROOT_PATH).getParent() + File.separator;

	public static final String CONFIGURATION_PATH = SERVICE_MANAGER_ROOT_PATH
			+ "conf" + File.separator;

	public static final String CONFIGURATION_NAVIGATION_PATH = CONFIGURATION_PATH
			+ "navigation" + File.separator;

	public static final String LOG_PATH = SERVICE_MANAGER_ROOT_PATH + "log"
			+ File.separator;

	public static final String TEMP_PATH = SERVICE_MANAGER_ROOT_PATH + "temp"
			+ File.separator;

	public static final String APPLICATION_SERVER_TEMP_PATH = TEMP_PATH
			+ "_application_server_";

	public static final String PLATFORM_PATH = SERVICE_MANAGER_ROOT_PATH
			+ "platform" + File.separator;

	public static final String CORE_PATH = PLATFORM_PATH + "core"
			+ File.separator;

	public static final String RESOURCES_PATH = CORE_PATH + "resources"
			+ File.separator;

	public static final String WEB_APPLICATIONS_COMMON_PATH = CORE_PATH + "war"
			+ File.separator;

	public static final String JAR_LIBRARY = CORE_PATH + "lib" + File.separator;

	public static final String LIB_FOLDER_NAME = "lib";

	public static final String NATIVE_LIB_PATH = CORE_PATH + "lib"
			+ File.separator + "native";

	/*
	 * Configuration file locations
	 */
	public static final String LOGGER_LEVEL_PROPERTIES = CONFIGURATION_PATH
			+ "logging.levels";

	public static final String CONFIGURATION_ARCHIVE_PATH = CONFIGURATION_PATH
			+ "saved" + File.separator;

	public static final String CONFIGURATION_TEMPLATE_PATH = CONFIGURATION_PATH
			+ "templates" + File.separator;

	public static final String CONFIGURATION_DELETED_PATH = CONFIGURATION_PATH
			+ "deleted" + File.separator;

	/*
	 * ENV Variables
	 */
	public static final String ENV_VAR_SM = "EDIFECS_SM_DIST";

	public static final String ENV_CONFIGURATION_PATH = File.separator + "conf"
			+ File.separator + "config.properties";

	/*
	 * Other Variables
	 */

	// default accounts
    public final static String SYSTEM_USERNAME = "system";

	public final static String DEFAULT_ORG_NAME = "edfx";

	public final static String DEFAULT_SITE_NAME = "Default Site";

	public final static String DEFAULT_TENANT_NAME = "_System";

	public final static String DEFAULT_SYSTEM_USER = "system";

	public final static String DEFAULT_EDIFECS_LOGO = "../resources/images/site-logo.png";

	// Configuration and Metadata Properties

	public static final String CORE_NODE_NAME = "Core";

	public static final String MANIFEST_FILE_EXTENSION = "manifest.yaml";

	public static final String APPS_FOLDER_NAME = "apps";

    public static final String SERVICES_FOLDER_NAME = "services";

	public static final String PLATFORM_FOLDER_NAME = "platform";

	public static final String ARTIFACTS_FOLDER_NAME = "artifacts";

	public static final String WAR_FOLDER_NAME = "wars";

    public static final String HTML_FOLDER_NAME = "html";

	public static final String APP_CONFIG_FOLDER_NAME = "conf";

	public static final String APPS_PATH = SERVICE_MANAGER_ROOT_PATH
			+ APPS_FOLDER_NAME + File.separator;

	public static final String METADATA_FILE_COMMON_RELATIVE_PATH = PLATFORM_FOLDER_NAME
			+ File.separator + "core" + File.separator + "metadata";

	public static final String CONFIGURATION_FILE_EXTENSION = ".xml";

    public static final String CONFIGURATION_JSON_FILE_EXTENSION = ".json";

	public static final String CONFIGURATION_FILE = "Configuration"
			+ CONFIGURATION_FILE_EXTENSION;

    public static final String CONFIGURATION_JSON_FILE = "Configuration"
            + CONFIGURATION_JSON_FILE_EXTENSION;

	public static final String CONFIGURATION_FILE_PATH = CONFIGURATION_PATH
			+ "Configuration" + CONFIGURATION_FILE_EXTENSION;

	public static final String CORE_CONFIGURATION_FILE = CORE_NODE_NAME + "-"
			+ CONFIGURATION_FILE;

	public static final String CORE_CONFIGURATION_FILE_PATH = CONFIGURATION_PATH
			+ CORE_CONFIGURATION_FILE;

	public static final String CONFIGURATION_XSD_FILE_NAME = "Configuration.xsd";

	// Config.Properties file definitions
	public static final String CONFIGURATION_PROPERTIES = CONFIGURATION_PATH
			+ "application.conf";

	// SSH Console*

	public static final String SECURITY_PATH = CONFIGURATION_PATH + "security"
			+ File.separator;

	public static final String PASSWORD = "password";

	public static final String SSH_SERVER_KEY_ALIAS = "xes-sshserver";

	public static final String SECURITY_CERTIFICATE_FILE_NAME = "keystore.jks";

	public static String SECURITY_CERTIFICATE_FILE = SECURITY_PATH
			+ SECURITY_CERTIFICATE_FILE_NAME;

	// Web Application Server

    public static final String WEBSERVER_BASE_DIR = SERVICE_MANAGER_ROOT_PATH
            + "temp" + File.separator;

	public static final String WEBSERVER_WORKING_DIR = "tomcat" + File.separator;

	// System Variable Keys

	public static final String NODE_NAME_KEY = "NODE_NAME_KEY";

    public static final String NODE_CONFIG_JSON_KEY = "NODE_CONFIG_JSON_KEY";

    public static final String CONFIG_JSON_KEY = "CONFIG_JSON_KEY";

	public static final String COLOR_DISABLED_KEY = "COLOR_DISABLED_KEY";

	// JUnit Test Variables

	public static final String TEST_ROOT_PATH = new File(WORKING_DIRECTORY)
			.getParentFile().getParentFile()
			+ File.separator
			+ "dist"
			+ File.separator;

	public static final String TEST_CONFIGURATION_PATH = TEST_ROOT_PATH
			+ "conf" + File.separator;

    /*
     * Content Repository folder locations
     */
	public static final String TEST_SECURITY_PATH = TEST_CONFIGURATION_PATH
			+ "security" + File.separator;

	public static final String TEST_SECURITY_CERTIFICATE_FILE = TEST_SECURITY_PATH
			+ SECURITY_CERTIFICATE_FILE_NAME;

	// Content Repository Variables

	public static final String CONTENT_REPOSITORY_SERVICE_NAME = "content-repository-service";

	public static final String CONTENT_REPOSITORY_DEFAULT_DATA_DIRECTORY = SERVICE_MANAGER_ROOT_PATH
			+ "repository" + File.separator;

	public static final String CONTENT_REPOSITORY_DEFAULT_CONFIG_XML_FILENAME = "repository.xml";

	/*
	 * Content Repository folder locations
	 */

	public static final String CONTENT_REPOSITORY_ROOT_PATH = "/";

	public static final String CONTENT_REPOSITORY_SERVICE_MANAGER = "/platform/";

	public static final String CONTENT_REPOSITORY_ARTIFACTS = "/artifacts/";

	public static final String CONTENT_REPOSITORY_CONFIGURATION_DIRECTORY = CONTENT_REPOSITORY_ROOT_PATH
			+ "conf/";

	public static final String CONTENT_REPOSITORY_ACTIVE_CONFIGURATION_FILE = CONTENT_REPOSITORY_CONFIGURATION_DIRECTORY
			+ "Configuration.xml";

	public static final String CONTENT_REPOSITORY_DELETED_CONFIGURATION = CONTENT_REPOSITORY_CONFIGURATION_DIRECTORY
			+ "deleted/";

	public static final String CONTENT_REPOSITORY_SAVED_CONFIGURATION = CONTENT_REPOSITORY_CONFIGURATION_DIRECTORY
			+ "saved/";

	public static final String CONTENT_REPOSITORY_TEMPLATE_CONFIGURATION = CONTENT_REPOSITORY_CONFIGURATION_DIRECTORY
			+ "templates/";

	public static final String CONTENT_REPOSITORY_COMMON_METADATA_CONFIGURATION = CONTENT_REPOSITORY_SERVICE_MANAGER
			+ "core/metadata/";

	public static final String CONTENT_REPOSITORY_CORE_ARTIFACTS = CONTENT_REPOSITORY_SERVICE_MANAGER
			+ "core/artifacts/";

	public static final String CONTENT_REPOSITORY_COMPONENTS_DIRECTORY = CONTENT_REPOSITORY_SERVICE_MANAGER
			+ "components/";

	public static final String CONTENT_REPOSITORY_APPS_DIRECTORY = CONTENT_REPOSITORY_ROOT_PATH
			+ "apps/";

	// Service names

	public static final String SECURITY_SERVICE_TYPE_NAME = "esm-service";

	public static final String NAVIGATION_SERVICE_TYPE_NAME = "xboard-portal-service";

	public static final String NAVIGATION_REGISTER_MENU_COMMAND = "registerMenu";

	public static final String NAVIGATION_REGISTER_ENTRY_COMMAND = "registerMenuEntry";

	// WAR file names

	public static final String PORTAL_WAR_FILENAME = "xboard-ui.war";

	public static final String SECURITY_WAR_FILENAME = "esm-ui.war";

	public static final String REST_WAR_FILENAME = "spray-service-ui.war";

    // HTML Dir Names

    public static final String PORTAL_HTML_DIRNAME = "xboard-service";

    public static final String SECURITY_HTML_DIRNAME = "esm-service";

    public static final String REST_HTML_DIRNAME = "spray-service";

	/**
	 * Path to the core JDBC folder where all bundles are located.
	 */
	public static final String LIB_JDBC_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "jdbc" + File.separator;

	/**
	 * Path to the core Hibernate folder where all bundles are located.
	 */
	public static final String LIB_HIBERNATE_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "hibernate"
			+ File.separator;

	/**
	 * Path to the core Repository folder where all bundles are located.
	 */
	public static final String LIB_REPOSITORY_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "repository"
			+ File.separator;

	/**
	 * Path to the core Agent folder where all bundles are located.
	 */
	public static final String LIB_AGENT_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "agent"
			+ File.separator;

	/**
	 * Path to the core Node folder where all bundles are located.
	 */
	public static final String LIB_NODE_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "sm-container"
			+ File.separator;

	/**
	 * Path to the core Common folder where all bundles are located.
	 */
	public static final String LIB_COMMON_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "common"
			+ File.separator;

	/**
	 * Path to the core Native folder where all bundles are located.
	 */
	public static final String LIB_NATIVE_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "native"
			+ File.separator;

	/**
	 * Path to the core Tomcat folder where all bundles are located.
	 */
	public static final String LIB_TOMCAT_DIR = PLATFORM_PATH + "core"
			+ File.separator + "lib" + File.separator + "sm-launcher"
			+ File.separator;

	/**
	 * Apps Directory
	 */
	public static final String APPS_DIRECTORY = SERVICE_MANAGER_ROOT_PATH
			+ "apps" + File.separator;

	/**
	 * Apps Directory
	 */
	public static final String COMPONENTS_DIRECTORY = PLATFORM_PATH
			+ "components" + File.separator;

	public static final String LAUNCH_NODES_KEY = "LAUNCH_NODES_KEY";

    /*
     * Akka Paths
     *
     * WARNING: If you change these, *make sure* to update akka.remote.trusted-selection-paths
     * in isc/isc-core/src/main/resources/default.conf! Failing to do this will result in messages
     * between nodes being blocked.
     */

    public static final String AKKA_CLUSTER_ROOT = "cluster";

    public static final String AKKA_CLUSTER_JOINER_NAME = "joiner";

    public static final String AKKA_COMMAND_ROOT = "service";

    public static final String AKKA_STREAM_ROOT = "stream";

    private SystemVariables() {
        // Intentionally Left Blank
    }
}
