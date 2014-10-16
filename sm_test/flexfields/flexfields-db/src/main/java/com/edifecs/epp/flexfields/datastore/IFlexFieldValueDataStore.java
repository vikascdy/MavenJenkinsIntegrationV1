package com.edifecs.epp.flexfields.datastore;

import com.edifecs.epp.flexfields.exception.FieldValueException;
import com.edifecs.epp.flexfields.exception.ItemAlreadyExistsException;
import com.edifecs.epp.flexfields.exception.ItemNotFoundException;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;

import java.util.Map;

/**
 * Created by sandeep.kath on 5/2/2014.
 */
public interface IFlexFieldValueDataStore {
    public FlexFieldValue createFlexFieldValue(FlexFieldValue FlexFieldValue) throws ItemAlreadyExistsException, FieldValueException;

    public FlexFieldValue updateFlexFieldValue(FlexFieldValue FlexFieldValue) throws ItemNotFoundException, FieldValueException;

    public void deleteFlexFieldValue(FlexFieldValue FlexFieldValue) throws ItemNotFoundException;

    public FlexFieldValue getById(long id) throws ItemNotFoundException;

    public Boolean validateFieldValue(FlexFieldValue flexFieldValue) throws FieldValueException;

    public Map<FlexFieldDefinition, FlexFieldValue> getFlexFieldValues(FlexGroup flexGroup) throws FieldValueException;
}
