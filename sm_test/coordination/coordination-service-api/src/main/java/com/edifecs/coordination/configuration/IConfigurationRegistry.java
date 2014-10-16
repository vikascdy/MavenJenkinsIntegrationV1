package com.edifecs.coordination.configuration;

import com.edifecs.core.configuration.configuration.Property;
import com.edifecs.core.configuration.configuration.Scope;

import java.util.Collection;


/**
 * 
 * 
 * @author willclem
 */
public interface IConfigurationRegistry {

	public static final String BASE = "/edifecs/platform/";
	public static final String CONFIGURATION_REGISTRY = BASE + "configuration/";
	
	
	/**
	 * Get collection of properties belong to the current user, also properties belonging to all modules for this user
	 * @return collection of properties
	 * @throws Exception
	 */
	Collection<? extends Property> getProperties() throws Exception;
	
	/**
	 * Get collection of properties belong to the current user, also properties belonging to all modules for this user
	 * @return json string of properties
	 * @throws Exception
	 */
	String getPropertiesJsonString() throws Exception;
	
	/**
	 * Get collection of properties belong to the specific module for the current user
	 * @param  moduleName the name of module for the current user
	 * @return collection of properties
	 * @throws Exception
	 */
	Collection<? extends Property> getProperties(String moduleName) throws Exception;
	
	/**
	 * Get collection of properties belong to the specific module for the current user
	 * @param  scope the scope for the properties
	 * @return collection of properties
	 * @throws Exception
	 */
	Collection<? extends Property> getProperties(Scope scope) throws Exception;
	
	/**
	 * Get collection of properties belong to the specific module for the current user
	 * @param  moduleName the name of module for the current user
	 * @return json string of properties
	 * @throws Exception
	 */
	String getPropertiesJsonString(String moduleName) throws Exception;
	
	/**
	 * Get collection of properties belong to the specific module for the current user
	 * @param  scope the scope for the properties
	 * @return json string of properties
	 * @throws Exception
	 */
	String getPropertiesJsonString(Scope scope) throws Exception;
	
	/**
	 * Set all properties for the current user
	 * @param properties collection of properties to be set
	 * @throws Exception
	 */
	void setProperties(Collection<? extends Property> properties) throws Exception;

	/**
	 * Set all properties for the current user
	 * @param propertiesJsonString json string of properties
	 * @throws Exception
	 */
	void setProperties(String propertiesJsonString) throws Exception;

	/**
	 * Set all properties for the specific module for the current user
	 * @param scope the scope for the properties
	 * @param properties collection of properties to be set
	 * @throws Exception
	 */
	void setProperties(Scope scope, Collection<? extends Property> properties) throws Exception;
	
	/**
	 * Set all properties for the specific module for the current user
	 * @param moduleName the name of module
	 * @param properties collection of properties to be set
	 * @throws Exception
	 */
	void setProperties(String moduleName, Collection<? extends Property> properties) throws Exception;
	
	/**
	 * Set all properties for the specific module for the current user
	 * @param scope the scope for the properties
	 * @param propertiesJsonString json string of properties
	 * @throws Exception
	 */
	void setProperties(Scope scope, String propertiesJsonString) throws Exception;
	
	/**
	 * Set all properties for the specific module for the current user
	 * @param moduleName the name of module
	 * @param propertiesJsonString json string of properties
	 * @throws Exception
	 */
	void setProperties(String moduleName, String propertiesJsonString) throws Exception;
	
	/**
	 * Get collection of property definitions belong to the current user, also properties belonging to all modules for this user
	 * @param moduleName the name of module
	 * @param version the version of module
	 * @param scope the scope for the property definition
	 * @return collection of property definitions
	 * @throws Exception
	 */
	Collection<PropertyDefinition> getPropertyDefinitions(String moduleName, String version, Scope scope) throws Exception;
	
	/**
	 * set property definitions for specific module and scope
	 * @param moduleName the name of module
	 * @param version the version of module
	 * @param scope the scope for the property definition
	 * @param propertyDefs the property definitions to be set
	 * @throws Exception
	 */
	void setPropertyDefinitions(String moduleName, String version, Scope scope, Collection<PropertyDefinition> propertyDefs) throws Exception;

}
