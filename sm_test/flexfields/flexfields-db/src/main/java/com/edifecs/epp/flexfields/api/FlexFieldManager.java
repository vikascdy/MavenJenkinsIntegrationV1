package com.edifecs.epp.flexfields.api;

import com.edifecs.epp.flexfields.datastore.IFlexFieldValueDataStore;
import com.edifecs.epp.flexfields.datastore.IFlexGroupDataStore;
import com.edifecs.epp.flexfields.exception.FieldValueException;
import com.edifecs.epp.flexfields.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexGroupDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexValueDataStore;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;

import java.util.Map;
import java.util.Properties;

/**
 * Created by sandeep.kath on 5/11/2014.
 */
public class FlexFieldManager implements IFlexFieldManager {
    IFlexFieldValueDataStore flexFieldValueDataStore;
    IFlexGroupDataStore flexGroupDataStore;
    DatabaseDataStore databaseDataStore;
    public FlexFieldManager(Properties properties) throws Exception {
        if(properties==null) {
            databaseDataStore = new DatabaseDataStore();
        } else {
            databaseDataStore = new DatabaseDataStore(properties);
        }
        flexFieldValueDataStore = new FlexValueDataStore();
        flexGroupDataStore = new FlexGroupDataStore();
    }



    @Override
    public FlexFieldValue setFlexFieldValue(FlexFieldValue flexFieldValue) throws Exception {
        if (flexFieldValue.getEntityName() == null) {
            throw new FieldValueException("Entity Name is required.");
        }
        if (flexFieldValue.getId() != null) {
            return flexFieldValueDataStore.updateFlexFieldValue(flexFieldValue);
        } else {
            return flexFieldValueDataStore.createFlexFieldValue(flexFieldValue);
        }
    }

    @Override
    public FlexFieldValue getFlexFieldValue(long id) throws Exception {
        return flexFieldValueDataStore.getById(id);
    }

    @Override
    public Map<FlexFieldDefinition, FlexFieldValue> getFlexFieldValues(FlexGroup flexGroup) throws Exception {
        return flexFieldValueDataStore.getFlexFieldValues(flexGroup);
    }


}
