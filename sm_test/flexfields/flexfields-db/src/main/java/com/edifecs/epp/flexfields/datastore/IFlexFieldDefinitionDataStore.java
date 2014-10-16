package com.edifecs.epp.flexfields.datastore;

import com.edifecs.epp.flexfields.exception.ItemNotFoundException;
import com.edifecs.epp.flexfields.exception.FlexFieldRegistryException;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;

import java.util.Collection;

/**
 * Created by sandeep.kath on 5/7/2014.
 */
public interface IFlexFieldDefinitionDataStore {
    public FlexFieldDefinition createFlexField(FlexFieldDefinition flexFieldDefinition) throws FlexFieldRegistryException;

    public FlexFieldDefinition updateFlexField(FlexFieldDefinition flexFieldDefinition) throws ItemNotFoundException;

    public void deleteFlexField(FlexFieldDefinition flexFieldDefinition) throws ItemNotFoundException;

    public Collection<FlexFieldDefinition> getRange(long startRecord, long recordCount) throws FlexFieldRegistryException;

    public FlexFieldDefinition getById(long id) throws ItemNotFoundException;
}
