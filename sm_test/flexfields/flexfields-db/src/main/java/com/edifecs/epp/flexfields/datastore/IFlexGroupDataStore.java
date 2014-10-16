package com.edifecs.epp.flexfields.datastore;

import com.edifecs.epp.flexfields.exception.FieldValueException;
import com.edifecs.epp.flexfields.exception.ItemAlreadyExistsException;
import com.edifecs.epp.flexfields.exception.ItemNotFoundException;
import com.edifecs.epp.flexfields.exception.FlexFieldRegistryException;
import com.edifecs.epp.flexfields.model.Context;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexGroup;

import java.util.Collection;
import java.util.Map;

/**
 * Created by sandeep.kath on 5/8/2014.
 */
public interface IFlexGroupDataStore {
    public FlexGroup createFlexGroup(FlexGroup flexGroup) throws ItemAlreadyExistsException;

    public FlexGroup updateFlexGroup(FlexGroup flexGroup) throws ItemNotFoundException;

    public void deleteFlexGroup(FlexGroup flexGroup) throws ItemNotFoundException;

    public Collection<FlexGroup> getRange(long startRecord, long recordCount) throws FlexFieldRegistryException;

    public FlexGroup getById(long id) throws ItemNotFoundException;

    public void addFlexFieldToGroup(FlexGroup flexGroup, FlexFieldDefinition flexFieldDefinition, int sequence) throws ItemAlreadyExistsException, ItemNotFoundException;

    public void removeFlexFieldFromGroup(FlexGroup flexGroup, FlexFieldDefinition flexFieldDefinition) throws ItemNotFoundException;

    public Collection<FlexFieldDefinition> getFields(FlexGroup group) throws ItemNotFoundException;

    public String[] getRequiredPermission(FlexGroup group) throws ItemNotFoundException;

    public FlexGroup getFlexGroupHierarchy(FlexGroup group, long entityId ) throws ItemNotFoundException, FieldValueException;

    public Collection<FlexGroup> getFlexGroupsByContext(String contextName, String contextValue) throws ItemNotFoundException;

    public Collection<FlexGroup> getFlexGroupsByContext(Map<Context, String> contextMap) throws ItemNotFoundException;

    public FlexGroup setParent(FlexGroup parent, FlexGroup child) throws ItemNotFoundException, ItemAlreadyExistsException;


}
