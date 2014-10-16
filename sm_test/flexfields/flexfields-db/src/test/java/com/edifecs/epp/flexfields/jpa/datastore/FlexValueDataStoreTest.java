package com.edifecs.epp.flexfields.jpa.datastore;

import com.edifecs.epp.flexfields.datastore.IFlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.datastore.IFlexFieldValueDataStore;
import com.edifecs.epp.flexfields.datastore.IFlexGroupDataStore;
import com.edifecs.epp.flexfields.exception.FieldValueException;
import com.edifecs.epp.flexfields.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexGroupDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexValueDataStore;
import com.edifecs.epp.flexfields.model.FieldType;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FlexValueDataStoreTest {
    private IFlexGroupDataStore flexGroupDataStore;
    private IFlexFieldDefinitionDataStore flexFieldDefinitionDataStore;
    private IFlexFieldValueDataStore flexFieldValueDataStore;

    @Before
    public void before() throws Exception {
        DatabaseDataStore databaseDataStore = new DatabaseDataStore();
        flexGroupDataStore = new FlexGroupDataStore();
        flexFieldDefinitionDataStore = new FlexFieldDefinitionDataStore();
        flexFieldValueDataStore = new FlexValueDataStore();
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: createFlexFieldValue(FlexFieldValue flexFieldValue)
     */
    @Test
    public void testCreateFlexFieldValue() {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexField();
            FlexGroup flexGroup = createFlexGroup();
            flexGroupDataStore.addFlexFieldToGroup(flexGroup, flexFieldDefinition, 1);
            FlexFieldValue flexFieldValue = new FlexFieldValue();
            flexFieldValue.setEntityName("User");
            flexFieldValue.setEntityID(100L);
            flexFieldValue.setFlexGroupId(flexGroup.getId());
            flexFieldValue.setFlexFieldDefinitionId(flexFieldDefinition.getId());
            flexFieldValue.setValue(null);
            FlexFieldValue flexFieldValuePersisted = flexFieldValueDataStore.createFlexFieldValue(flexFieldValue);
            assertNotNull(flexFieldValuePersisted);
            assertNotNull(flexFieldValueDataStore.getById(flexFieldValuePersisted.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Method: updateFlexFieldValue(FlexFieldValue flexFieldValue)
     */
    @Test
    public void testUpdateFlexFieldValue() throws Exception {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexField();
            FlexGroup flexGroup = createFlexGroup();
            flexGroupDataStore.addFlexFieldToGroup(flexGroup, flexFieldDefinition, 1);
            FlexFieldValue flexFieldValue = new FlexFieldValue();
            flexFieldValue.setEntityName("User");
            flexFieldValue.setEntityID(100L);
            flexFieldValue.setFlexGroupId(flexGroup.getId());
            flexFieldValue.setFlexFieldDefinitionId(flexFieldDefinition.getId());
            FlexFieldValue flexFieldValuePersisted = flexFieldValueDataStore.createFlexFieldValue(flexFieldValue);
            assertNotNull(flexFieldValuePersisted);
            assertNotNull(flexFieldValueDataStore.getById(flexFieldValuePersisted.getId()));

            flexFieldValuePersisted.setFlexFieldDefinitionId(flexFieldValue.getFlexFieldDefinitionId());
            flexFieldValuePersisted.setFlexGroupId(flexFieldValue.getFlexGroupId());
            flexFieldValuePersisted.setEntityID(200L);

            FlexFieldValue _flexFieldValuePersisted = flexFieldValueDataStore.updateFlexFieldValue(flexFieldValuePersisted);
            assertNotNull(_flexFieldValuePersisted);
            assertEquals(200L, _flexFieldValuePersisted.getEntityID());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: deleteFlexFieldValue(FlexFieldValue flexFieldValue)
     */
    @Test
    public void testDeleteFlexFieldValue() throws Exception {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexField();
            FlexGroup flexGroup = createFlexGroup();
            flexGroupDataStore.addFlexFieldToGroup(flexGroup, flexFieldDefinition, 1);
            FlexFieldValue flexFieldValue = new FlexFieldValue();
            flexFieldValue.setEntityName("User");
            flexFieldValue.setEntityID(100L);
            flexFieldValue.setFlexGroupId(flexGroup.getId());
            flexFieldValue.setFlexFieldDefinitionId(flexFieldDefinition.getId());
            FlexFieldValue flexFieldValuePersisted = flexFieldValueDataStore.createFlexFieldValue(flexFieldValue);
            assertNotNull(flexFieldValuePersisted);
            assertNotNull(flexFieldValueDataStore.getById(flexFieldValuePersisted.getId()));

            flexFieldValueDataStore.deleteFlexFieldValue(flexFieldValuePersisted);

            assertNull(flexFieldValueDataStore.getById(flexFieldValuePersisted.getId()));


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    /**
     * Method: validateFieldValue(FlexFieldValue flexFieldValue)
     */
    @Test(expected = FieldValueException.class)
    public void testInvalidDataValue() throws Exception {

        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
        flexFieldDefinition.setName("Age");
        flexFieldDefinition.setDescription("Patient Age");
        flexFieldDefinition.setDefaultValue("0");
        flexFieldDefinition.setDisplayName("Patient Age");
        flexFieldDefinition.setRegEx("[0-9]+");
        flexFieldDefinition.setValidationMessage("Please enter the valid integer number");
        flexFieldDefinition.setActiveFlag(true);
        flexFieldDefinition.setDataType(FieldType.LONG);
        FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);

        FlexGroup flexGroup = createFlexGroup();
        flexGroupDataStore.addFlexFieldToGroup(flexGroup, flexFieldPersisted, 1);
        FlexFieldValue flexFieldValue = new FlexFieldValue();
        flexFieldValue.setEntityName("User");
        flexFieldValue.setEntityID(123);
        flexFieldValue.setFlexGroupId(flexGroup.getId());
        flexFieldValue.setValue("ABC124");
        flexFieldValue.setFlexFieldDefinitionId(flexFieldPersisted.getId());
        FlexFieldValue flexFieldValuePersisted = flexFieldValueDataStore.createFlexFieldValue(flexFieldValue);

    }

    @Test(expected = FieldValueException.class)
    public void testInvalidRegExDataValue() throws Exception {

        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
        flexFieldDefinition.setName("Secondary Email");
        flexFieldDefinition.setDescription("Secondary Email");
        flexFieldDefinition.setDefaultValue("");
        flexFieldDefinition.setDisplayName("Secondary Email");
        flexFieldDefinition.setRegEx("^([0-9a-zA-Z]([-\\.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$");
        flexFieldDefinition.setValidationMessage("Please enter the valid email address");
        flexFieldDefinition.setActiveFlag(true);
        flexFieldDefinition.setDataType(FieldType.STRING);
        FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);

        FlexGroup flexGroup = createFlexGroup();
        flexGroupDataStore.addFlexFieldToGroup(flexGroup, flexFieldPersisted, 1);
        FlexFieldValue flexFieldValue = new FlexFieldValue();
        flexFieldValue.setEntityName("User");
        flexFieldValue.setEntityID(123);
        flexFieldValue.setFlexGroupId(flexGroup.getId());
        flexFieldValue.setValue("e@edifecs"); //invalid email
        flexFieldValue.setFlexFieldDefinitionId(flexFieldPersisted.getId());
        FlexFieldValue flexFieldValuePersisted = flexFieldValueDataStore.createFlexFieldValue(flexFieldValue);

    }

    private FlexGroup createFlexGroup() throws Exception {
        //Create FlexGroup
        FlexGroup flexGroup = new FlexGroup();
        flexGroup.setAppName("TDM");
        flexGroup.setComponentName("VDD");
        flexGroup.setDescription("Files Count");
        flexGroup.setEntityName("EDIFile");
        flexGroup.setName("EDIFile");
        flexGroup.setDisplayName("Upload EDI File");
        flexGroup.setTenantName("BCBS_WA");
        FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
        return flexGroupPersisted;

    }

    private FlexFieldDefinition createFlexField() throws Exception {
        //Create FlexField
        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
        flexFieldDefinition.setName("FTP Address" + Math.random());
        flexFieldDefinition.setDescription("FTP Address");
        flexFieldDefinition.setDefaultValue("ftp://edifecs.com");
        flexFieldDefinition.setDisplayName("FTP Address");
        flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
        flexFieldDefinition.setValidationMessage("Please enter the valid URL");
        flexFieldDefinition.setActiveFlag(true);
        flexFieldDefinition.setDataType(FieldType.STRING);
        FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
        return flexFieldPersisted;
    }
}
