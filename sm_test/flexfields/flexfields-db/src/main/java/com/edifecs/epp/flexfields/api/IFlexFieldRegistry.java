package com.edifecs.epp.flexfields.api;

import com.edifecs.epp.flexfields.exception.ItemNotFoundException;
import com.edifecs.epp.flexfields.model.Context;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexGroup;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by sandeep.kath on 5/2/2014.
 */
public interface IFlexFieldRegistry {

    /**
     * Get collection of FlexField Definitions belong to the Flex Group
     *
     * @param flexGroup instance of  FlexGroup
     * @return collection of FlexFieldDefinition
     * @throws Exception
     */
    Collection<FlexFieldDefinition> getFlexFieldDefinitions(FlexGroup flexGroup) throws Exception;

    /**
     * Get collection of nested Flex Groups for a given context.
     *
     * @param contextMap is Map of Context Key and its value as an Entry.
     * @return collection of FlexGroup
     * @throws Exception
     */
    List<FlexGroup> getFields(Map<Context, String> contextMap) throws Exception;

    /**
     * Add or Update the flex Groups associated with any context.
     *
     * @param flexGroups is a collection.
     * @throws Exception
     */
    void setFlexGroup(Collection<FlexGroup> flexGroups) throws Exception;

    /**
     * Add fields to the group.
     *
     * @param flexFields is a collection of FlexFieldDefinition.
     * @param flexGroup  is a instance of FlexGroup
     * @return flexGroup to which fields are added
     * @throws Exception
     */
    FlexGroup addFieldsToGroup(Collection<FlexFieldDefinition> flexFields, FlexGroup flexGroup) throws Exception;

    /**
     * Add field to the group.
     *
     * @param flexFieldDefinition is a FlexFieldDefinition.
     * @param flexGroup  is a instance of FlexGroup
     * @return flexGroup to which fields are added
     * @throws Exception
     */
    FlexGroup addFieldToGroup(FlexFieldDefinition flexFieldDefinition, FlexGroup flexGroup) throws Exception;

    /**
     * Add or update the flexField Definition
     *
     * @param flexFieldDefinition is a FlexFieldDefinition.
     * @return flexFieldDefinition
     * @throws Exception
     */
    FlexFieldDefinition setFlexField(FlexFieldDefinition flexFieldDefinition) throws Exception;

    /**
     * Add or update the flexField Definition
     *
     * @param flexGroup is a FlexGroup
     * @return FlexGroup Instance
     * @throws Exception
     */
    FlexGroup setFlexGroup(FlexGroup flexGroup) throws Exception;


    /**
     * Delete the flexGroup with associated flex field values.
     *
     * @param flexGroup to be removed.
     * @throws Exception
     */
    void deleteFlexGroup(FlexGroup flexGroup) throws Exception;

    /**
     * Delete the flexGroup with associated flex field values.
     *
     * @param flexFieldDefinition to be removed.
     * @throws Exception
     */
    void deleteFlexFieldDefinition(FlexFieldDefinition flexFieldDefinition) throws Exception;

    /**
     * Set Parent of flexGroup
     *
     * @param parent flexGroup.
     * @param child  flexGroup.
     * @return FlexGroup as Parent
     * @throws Exception
     */
    FlexGroup setParent(FlexGroup parent, FlexGroup child) throws Exception;

    /**
     * Get FlexFieldDefinition Object
     *
     * @param id flexFieldId
     * @return FlexFieldDefinition
     * @throws Exception
     */
    public FlexFieldDefinition getFlexFieldDefinitionById(long id) throws Exception;

    /**
     * Parse and create flex fields and flexGroups from profile attributes of Physical Properties of Manifest
     * @param manifestStream
     * @return boolean
     * @throws Exception
     */
    public boolean parseAppManifest(InputStream manifestStream) throws Exception;

    /**
     * Parse and create flex fields and flexGroups from profile attributes of Physical Properties of Manifest String
     * @param manifestString
     * @return boolean
     * @throws Exception
     */
    public boolean parseAppManifest(String manifestString) throws Exception;

    /**
     * Get the FlexGroup object by Group ID
     * @param id FlexGroup ID
     * @throws ItemNotFoundException
     */
    FlexGroup getFlexGroupById(long id) throws ItemNotFoundException;
}
