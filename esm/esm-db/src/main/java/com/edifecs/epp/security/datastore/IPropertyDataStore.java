package com.edifecs.epp.security.datastore;

import java.util.Collection;

import com.edifecs.core.configuration.configuration.Scope;
import com.edifecs.epp.security.data.Property;

import com.edifecs.epp.security.data.User;

public interface IPropertyDataStore extends IBaseOwnerDataStore<Property> {

	/**
	 * Get collection of properties belong to the current user, also properties belonging to all modules for this user
	 * @param user the User object
	 * @return collection of properties
	 * @throws Exception
	 */
	Collection<Property> getProperties(User user) throws Exception;
	
	/**
	 * Get collection of properties belong to the specific module for the current user
	 * @param user the User object
	 * @param  moduleName the name of module for the user
	 * @return collection of properties
	 * @throws Exception
	 */
	Collection<Property> getProperties(User user, String moduleName) throws Exception;
	
	/**
	 * Get collection of properties belong to the specific module for the current user
	 * @param ownerId the id of the owner object
	 * @param  scope the scope for the properties
	 * @return collection of properties
	 * @throws Exception
	 */
	Collection<Property> getProperties(Long ownerId, Scope scope) throws Exception;
	
	
	/**
	 * Set all properties for the user
	 * @param user the User object
	 * @param properties collection of properties to be set
	 * @throws Exception
	 */
	void setProperties(User user, Collection<Property> properties) throws Exception;


	/**
	 * Set all properties for the specific owner object
	 * @param ownerId the id of the owner object
	 * @param scope the scope for the properties
	 * @param properties collection of properties to be set
	 * @throws Exception
	 */
	void setProperties(Long ownerId, Scope scope, Collection<Property> properties) throws Exception;
	
	/**
	 * Set all properties for the specific module for the user
	 * @param user the User object
	 * @param moduleName the name of module
	 * @param properties collection of properties to be set
	 * @throws Exception
	 */
	void setProperties(User user, String moduleName, Collection<Property> properties) throws Exception;
	
}
