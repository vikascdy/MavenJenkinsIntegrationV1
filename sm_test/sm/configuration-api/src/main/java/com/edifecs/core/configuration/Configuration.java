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
package com.edifecs.core.configuration;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.edifecs.core.configuration.configuration.*;
import com.edifecs.core.configuration.exception.InvalidManifestException;
import com.edifecs.core.configuration.helper.JAXBUtility;
import com.edifecs.core.configuration.helper.PropertiesException;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.packaging.manifest.Manifest;
import com.edifecs.epp.packaging.manifest.PhysicalComponent;
import com.edifecs.epp.util.serde.SerDeUtil;
import com.edifecs.epp.util.yaml.YamlParser;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;
import scala.collection.immutable.Stack;

import javax.xml.bind.*;

/**
 * Holds information specific to the currently running configuration. This
 * includes all configuration and manifest information.
 * 
 * @author willclem
 */
public class Configuration implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static final long serialVersionUID = 1L;

    private Server server;

    private List<Node> nodes = new ArrayList<>();

    private List<Resource> resources = new ArrayList<>();

    private Map<ManifestId, Cartridge> cartridgeMap = new HashMap<>();

    /**
     * Given a path to an application manifest file, register the application with SM.
     *
     * @param appFile
     * @throws PropertiesException
     */
    public void addAppDirectory(File appFile) throws PropertiesException {
        // ServiceManager\apps\<Application Name>
        if (appFile.isDirectory()) {
            for (File file : appFile.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(SystemVariables.MANIFEST_FILE_EXTENSION.toLowerCase())) {
                    addManifest(file);
                }
            }
        }
    }

    public Configuration addManifest(File manifestFile) throws PropertiesException {
        if (manifestFile.exists()
                && manifestFile.isFile()
                && manifestFile.getName().toLowerCase().endsWith(SystemVariables.MANIFEST_FILE_EXTENSION.toLowerCase())) {
            try {
                return addManifest(manifestFile.getParentFile().getAbsolutePath(), new FileInputStream(manifestFile));
            } catch (FileNotFoundException e) {
                throw new PropertiesException("Unable to load metadata configuration file.", e);
            }
        } else {
            throw new PropertiesException("No METADATA configurations found for "+ manifestFile.getName());
        }
    }

    public Configuration addManifest(String manifestLocation, InputStream manifestStream) throws PropertiesException, InvalidManifestException {
        try {
            scala.collection.immutable.Map<String, Object> map = YamlParser.yamlAsMap(manifestStream).get();
            Manifest manifest = Manifest.apply(map, new SerDeUtil.Context(new Stack())).get();
            cartridgeMap.put(new ManifestId(manifest.name(), manifest.version()), new Cartridge(manifestLocation, manifest));
        } catch (IllegalArgumentException e) {
            throw new InvalidManifestException(manifestLocation, e);
        }

        return this;
    }

    private void logXMLParseError(UnmarshalException ex, String filePath) {
        if (ex.getCause() instanceof SAXParseException) {
            final SAXParseException saxErr = (SAXParseException)ex.getCause();
            logger.error("Failed to parse XML file '" + filePath + "': " +
                    saxErr.getMessage() + " (at line " + saxErr.getLineNumber() +
                    ", column " + saxErr.getColumnNumber() + ")", saxErr);
        } else {
            logger.error("Invalid Configuration.xml file located at: " + filePath, ex);
        }
    }

    public Configuration loadConfiguration(String rootPath) throws PropertiesException {
        // Load all files ending in either Configuration.json or Configuration.xml
        File confDirectory = new File(SystemVariables.CONFIGURATION_PATH);
        if (confDirectory.listFiles() != null) {
            for (File conf : confDirectory.listFiles()) {
                if (conf.getName().endsWith(SystemVariables.CONFIGURATION_FILE)) {
                    try {
                        FileInputStream configStream = new FileInputStream(conf);
                        DeploymentConfiguration xmlConf = unmarshalDeploymentConfigurationFromXml(configStream);
                        configStream.close();
                        mergeServerAndResources(xmlConf);
                    } catch (UnmarshalException ex) {
                        logXMLParseError(ex, conf.getAbsolutePath());
                    } catch (Exception e) {
                        logger.error("Invalid Configuration.xml file located at: " + conf.getAbsolutePath(), e);
                    }
                } else if (conf.getName().endsWith(SystemVariables.CONFIGURATION_JSON_FILE)) {
                    try {
                        FileInputStream configStream = new FileInputStream(conf);
                        loadConfigurationFromJSON(configStream);
                    } catch (IOException e) {
                        logger.error("Invalid Configuration.json file located at: " + conf.getAbsolutePath(), e);
                    }
                }
            }
        }

        // Load possible resource file in conf/Configuration.xml or conf/Configuration.json
        String resourceConfigurationPath = "/conf/" + SystemVariables.CONFIGURATION_FILE;
        try {
            URL u = getClass().getClassLoader().getResource(resourceConfigurationPath);
            if (u != null) {
                InputStream resourceConfigXml = getClass().getResourceAsStream(resourceConfigurationPath);
                DeploymentConfiguration xmlConf = unmarshalDeploymentConfigurationFromXml(resourceConfigXml);
                resourceConfigXml.close();
                mergeServerAndResources(xmlConf);
            }
        } catch (UnmarshalException ex) {
            logXMLParseError(ex, resourceConfigurationPath);
        } catch (Exception e) {
            logger.error("Invalid Configuration.xml file located at: " + resourceConfigurationPath, e);
        }

        // Load possible resource file in conf/Configuration.xml or conf/Configuration.json
        String resourceConfigurationJsonPath = "/conf/" + SystemVariables.CONFIGURATION_JSON_FILE;
        try {
            URL u = getClass().getClassLoader().getResource(resourceConfigurationJsonPath);
            if (u != null) {
                InputStream resourceConfigJson = getClass().getResourceAsStream(resourceConfigurationPath);
                loadConfigurationFromJSON(resourceConfigJson);
            }
        } catch (IOException e) {
            logger.error("Invalid Configuration.json file located at: " + resourceConfigurationPath, e);
        }

        loadMetadataAndAppConfigurations(rootPath);

        return this;
    }

    public void loadConfigurationFromJSON(InputStream configStream) throws PropertiesException, IOException {
        final Gson gson = new Gson();
        DeploymentConfiguration jsonConf = gson.fromJson(new InputStreamReader(configStream),
                DeploymentConfiguration.class);
        configStream.close();
        mergeServerAndResources(jsonConf);
    }

    /**
     * Gets the local Server information if found. Will return NULL if no server
     * is found.
     *
     * @return The local Server definition
     */
    public void mergeServerAndResources(DeploymentConfiguration configuration) throws PropertiesException {

        loadServer(configuration);

        loadNodes(configuration);

        loadResources(configuration);
    }

    private void loadServer(DeploymentConfiguration configuration) throws PropertiesException {
        // Load Server Information
        Server defaultServer = null;
        for(Cluster cluster : configuration.getClusters()) {
            for (Server serverInstance : cluster.getServers()) {
                try {
                    // For Simple installs, using 127.0.0.1 auto loads this node on any machine.
                    if ("127.0.0.1".equals(serverInstance.getIpAddress())
                            || "localhost".equals(serverInstance.getHostName())) {
                        defaultServer = serverInstance;
                    } else {
                        if (isValidAddress(serverInstance.getHostName(), serverInstance.getIpAddress())) {
                            server = serverInstance;
                            break;
                        }
                    }
                } catch (UnknownHostException e) {
                    throw new PropertiesException(e);
                }
            }
        }

        if (server == null) {
            server = defaultServer;
        }
    }

    private void loadNodes(DeploymentConfiguration configuration) throws PropertiesException {
        // Load Node Information from all servers in the configuration file
        for(Cluster cluster : configuration.getClusters()) {
            for (Server serverInstance : cluster.getServers()) {
                nodes.addAll(serverInstance.getNodes());
            }
        }
    }

    private void loadResources(DeploymentConfiguration configuration) throws PropertiesException {
        // Load Resource information from all servers in the configuration file
        for(Cluster cluster : configuration.getClusters()) {
            resources.addAll(cluster.getResources());
        }
    }

    public void loadMetadataAndAppConfigurations(String rootPath) throws PropertiesException {
        //TODO: Simplify this logic and package structure

        // Load all Metadata files in all known locations
        // ServiceManager\platform\core\metadata
        File directory = new File(rootPath + SystemVariables.METADATA_FILE_COMMON_RELATIVE_PATH);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(SystemVariables.MANIFEST_FILE_EXTENSION)) {
                    addManifest(file);
                }
            }
        }

        // ServiceManager\apps\<Application Name>
        directory = new File(rootPath + SystemVariables.APPS_FOLDER_NAME);
        files = directory.listFiles();
        if (files != null) {
            for (File componentsDir : files) {
                addAppDirectory(componentsDir);
            }
        }
    }

    //TODO: Move these into its own class
    //////////////////////////////////////////////////////

    public static Properties getServiceProperties(final Service service) {
        Properties properties = new Properties();

        if (service.getProperties() != null) {
            for (Property property : service.getProperties()) {
                setProperty(property, service, properties);
            }
        }
        return properties;
    }

    private static void setProperty(Property property, Service service, Properties properties) {
        if(property.getName() == null) {
            logger.error(String.format(
                    "Service with name '%s' has an invalid property with name '%s' and value '%s'",
                    service.getName(),
                    property.getName(),
                    property.getValue()));
        } else {
        	if (property.getName() != null || property.getValue() != null){
        		properties.put(property.getName(), property.getValue());
        	}
        }
    }

    public static List<File> getWARFiles(String rootPath) {
        List<File> warFiles = new ArrayList<>();

        // Load all Metadata files in all known locations

        // ServiceManager\apps\<Application Name>
        File directory = new File(rootPath + SystemVariables.APPS_FOLDER_NAME);
        if (directory.listFiles() != null) {
            for (File componentsDir : directory.listFiles()) {
                componentsDir = new File(componentsDir, SystemVariables.WAR_FOLDER_NAME);
                if (componentsDir.isDirectory()) {
                    for (File service : componentsDir.listFiles()) {
                        if (service.isDirectory()) {
                            for (File file : service.listFiles()) {
                                if (file.isFile() && file.getName().endsWith(".war")) {
                                    warFiles.add(file);
                                }
                            }
                        }
                    }
                }
            }
        }
        return warFiles;
    }


    public static List<File> getHtmlDirectories(String rootPath) {
        List<File> htmlDir = new ArrayList<>();

        // Load all Metadata files in all known locations

        // ServiceManager\apps\<Application Name>
        File directory = new File(rootPath + SystemVariables.APPS_FOLDER_NAME);
        if (directory.listFiles() != null) {
            for (File componentsDir : directory.listFiles()) {
                componentsDir = new File(componentsDir, SystemVariables.HTML_FOLDER_NAME);
                if (componentsDir.isDirectory()) {
                    htmlDir.add(componentsDir);
                }
            }
        }

        //TODO: Put logic in here to load the html directories dynamically from configuration as well

        return htmlDir;
    }

    public static List<File> getAppConfigFiles(String rootPath) {
        List<File> confFiles = new ArrayList<>();

        // Load all Conf files from apps

        // ServiceManager\apps\
        File directory = new File(rootPath + SystemVariables.APPS_FOLDER_NAME);
        if (directory.listFiles() != null) {
            // ServiceManager\apps\<Application Name>\
            for (File componentsDir : directory.listFiles()) {
                // ServiceManager\apps\<Application Name>\services\
                componentsDir = new File(componentsDir, SystemVariables.SERVICES_FOLDER_NAME);
                if (componentsDir.isDirectory()) {
                    // ServiceManager\apps\<Application Name>\services\<service name>\
                    for (File service : componentsDir.listFiles()) {
                        // ServiceManager\apps\<Application Name>\services\<service name>\conf
                        service = new File(service, SystemVariables.APP_CONFIG_FOLDER_NAME);
                        if (service.isDirectory()) {
                            // ServiceManager\apps\<Application Name>\services\<service name>\conf\<file>
                            for (File file : service.listFiles()) {
                                if (file.isFile()) {
                                    confFiles.add(file);
                                }
                            }
                        }
                    }
                }
            }
        }
        return confFiles;
    }

    public static List<File> getSecurityRolesAndPermissionsFiles(String rootPath) {
        List<File> confFiles = new ArrayList<File>();

        // Load all Conf files from apps
        confFiles = getAppConfigFiles(rootPath);

        // node and agent defaults
        File directory = new File(SystemVariables.SECURITY_PATH);
        if (directory.listFiles() != null) {
            for (File componentFile : directory.listFiles()) {
                if (componentFile.isFile()) {
                    confFiles.add(componentFile);
                }
            }
        }
        return confFiles;
    }

    public static List<File> getNavigationFiles(String rootPath) {
        List<File> confFiles = new ArrayList<>();

        // Load all Conf files from apps
        for (File f : getAppConfigFiles(rootPath)) {
            if (f.isFile() && f.getName().endsWith("nav.json"))
                confFiles.add(f);
        }

        return confFiles;
    }

    public static List<File> getFeatureItemFiles(String rootPath) {
        List<File> confFiles = new ArrayList<>();

        // Load all Conf files from apps
        for (File f : getAppConfigFiles(rootPath)) {
            if (f.isFile() && f.getName().endsWith("featured-items.json"))
                confFiles.add(f);
        }

        return confFiles;
    }

    public static List<File> getLibDirectories(String rootPath) {
        List<File> libDirs = new ArrayList<>();

        // Load all bundle folders in all known locations

        // ServiceManager\apps\<Application Name>
        File appsDirectory = new File(rootPath + SystemVariables.APPS_FOLDER_NAME);
        if (appsDirectory.listFiles() != null) {
            for (File bundleDir : appsDirectory.listFiles()) {
                bundleDir = new File(bundleDir, SystemVariables.LIB_FOLDER_NAME);
                if (bundleDir.isDirectory()) {
                    libDirs.add(bundleDir);
                }
            }
        }

        return libDirs;
    }

    ////////////////////////////////////////////
    // Configuration.XML File Methods

    /**
     * Converts a services Resources to a map of Properties.
     *
     * @param service
     * @return
     */
    public Map<String, Properties> getServiceResources(Service service) throws PropertiesException {
        Map<String, Properties> resources = new HashMap<>();
        for (ResourceReference resourceReference : service.getResources()) {
            Properties properties = new Properties();

            // If the properties are not defined as part of the service, and references a resource from the global
            // space, lookup those values
            Resource resource = getResourceByName(resourceReference.getName());
            if (resource != null) {
                for (Property property : resource.getProperties()) {
                    setProperty(property, resource, properties);
                }
            }

            if (!resourceReference.getProperties().isEmpty()) {
                // If the properties are defined directly as part of the service, override the global properties
                for (Property property : resourceReference.getProperties()) {
                    setProperty(property, resource, properties);
                }
            }
            resources.put(resourceReference.getTypeName(), properties);
        }
        return resources;
    }

    private void setProperty(Property property, Resource resource, Properties properties) {
        if(property.getName() == null) {
            logger.error(String.format(
                    "Resource with name '%s' has an invalid property with name '%s' and value '%s'",
                    resource.getName(),
                    property.getName(),
                    property.getValue()));
        } else if(property.getValue() == null) {
            logger.error(String.format(
                    "Resource with name '%s' has an invalid property value with name '%s' and value '%s'",
                    resource.getName(),
                    property.getName(),
                    property.getValue()));
        } else {
            if (property.getName() != null || property.getValue() != null){
            	properties.put(property.getName(), property.getValue());
            }
        }
    }

    public Node getNode(final String nodeName) throws PropertiesException {
        // Iterate through the whole configuration file for the node name
        for (Node node : nodes) {
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        throw new PropertiesException("No configuration found for node: " + nodeName);
    }

    /**
     * Gets the node representing the running Node. If this is not a node, then
     * it will return null. It gets the local node information by getting it
     * from System.getProperty(SystemVariables.NODE_NAME_KEY).
     *
     * @return Local Node
     * @throws PropertiesException
     */
    public Node getNode() throws PropertiesException {
        return getNode(System.getProperty(SystemVariables.NODE_NAME_KEY));
    }

    /**
     * Gets the local Server information if found. Will return NULL if no server
     * is found.
     *
     * @return The local Server definition
     */
    public Server getServer() throws PropertiesException {
        return server;
    }

    ////////////////////////////////////////////////////////////
    public static DeploymentConfiguration unmarshalDeploymentConfigurationFromXml(
            final InputStream file) throws JAXBException {
        JAXBContext contextObj = JAXBUtility.CONFIGURATION.getContext();
        Unmarshaller unmarshaller = contextObj.createUnmarshaller();

        return (DeploymentConfiguration) unmarshaller.unmarshal(file);
    }

    //TODO: Review this and move to another project?
    public static String getHostname() throws UnknownHostException {
        // TODO: Check on this as it fails in linux when the /etc/hosts file is
        // incorrect
        return InetAddress.getLocalHost().getHostName();
    }

    //TODO: Review this and move to another project?
    /**
     * Helper Method used to check to see if the supplied IP address belongs to
     * this machine.
     *
     * @param serverName
     *            Hostname for the server
     * @param ipAddress
     *            IP Address to check
     * @return Returns True if the IP belongs to the machine, False if not
     * @throws UnknownHostException
     *             Thrown if its unable to get local host machine information
     */
    private static boolean isValidAddress(final String serverName, final String ipAddress) throws UnknownHostException {
        // Host Name takes priority and is checked first
        if (serverName != null && getHostname().toUpperCase().equals(serverName.toUpperCase())) {
            return true;
        }

        // Check IP Addresses for a valid address
        InetAddress[] inetAddresses = InetAddress.getAllByName(getHostname());
        for (InetAddress inetAddress : inetAddresses) {
            if (ipAddress != null && ipAddress.equals(inetAddress.getHostAddress())) {
                return true;
            }
        }

        return false;
    }

    public Cartridge getCartridgeByServiceName(String name) {
        for (Map.Entry<ManifestId, Cartridge> cartridge : cartridgeMap.entrySet()) {
            for(PhysicalComponent physicalComponent : cartridge.getValue().getManifest().getPhysicalComponents()) {
                if(physicalComponent.name().equals(name)) {
                    return cartridge.getValue();
                }
            }
        }
        //TODO: Throw friendly exception
        return null;
    }

    public PhysicalComponent getServiceManifestByName(String name) {
        for (Map.Entry<ManifestId, Cartridge> cartridge : cartridgeMap.entrySet()) {
            for(PhysicalComponent physicalComponent : cartridge.getValue().getManifest().getPhysicalComponents()) {
                if(physicalComponent.name().equals(name)) {
                    return physicalComponent;
                }
            }
        }
        //TODO: Throw friendly exception
        return null;
    }

    /**
     * Gets the Resource with the given resourceName.
     *
     * @param resourceName Name of the Resource to find
     * @return Resource
     */
    public final Resource getResourceByName(final String resourceName) {
        for (Resource resource : resources) {
            if (resource.getName().equals(resourceName)) {
                return resource;
            }
        }
        return null;
    }

    public String getServerName() throws PropertiesException {
        if (server == null || server.getName() == null) {
            try {
                return getHostname();
            } catch (UnknownHostException e) {
                throw new PropertiesException(e);
            }
        }
        return server.getName();
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
