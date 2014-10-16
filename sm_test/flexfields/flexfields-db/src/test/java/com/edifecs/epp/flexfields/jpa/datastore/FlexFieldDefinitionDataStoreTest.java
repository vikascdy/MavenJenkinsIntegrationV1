package com.edifecs.epp.flexfields.jpa.datastore;

import com.edifecs.epp.flexfields.datastore.IFlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.exception.ItemAlreadyExistsException;
import com.edifecs.epp.flexfields.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.model.FieldType;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by sandeep.kath on 5/5/2014.
 */
public class FlexFieldDefinitionDataStoreTest {
    private DatabaseDataStore databaseDataStore;
    private IFlexFieldDefinitionDataStore flexFieldDefinitionDataStore;


    @Before
    public void setUp() throws Exception {
        databaseDataStore = new DatabaseDataStore();
        flexFieldDefinitionDataStore = new FlexFieldDefinitionDataStore();
    }

    @Test
    public void testCreateFlexField() {
        try {
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("Provider+&90");
            flexFieldDefinition.setDescription("Provider flex field required to be captured at forms");
            flexFieldDefinition.setDefaultValue("Overlake Hospital");
            flexFieldDefinition.setDisplayName("Provider Name");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the proper name");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);

            FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted.getId());
            assertNotNull(flexFieldDefinitionDataStore.getById(flexFieldPersisted.getId()));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }

    @Test
    public void testUpdate() {
        try {
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("Secondary Provider ++Qwe");
            flexFieldDefinition.setDescription("Provider flex field required to be captured at forms");
            flexFieldDefinition.setDefaultValue("Overlake Hospital");
            flexFieldDefinition.setDisplayName("Secondary Provider Name");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the proper name");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);

            FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted.getId());

            flexFieldPersisted.setName("Provider IInd");
            flexFieldDefinitionDataStore.updateFlexField(flexFieldPersisted);

            assertNotNull(flexFieldDefinitionDataStore.getById(flexFieldPersisted.getId()));
            assertEquals("Provider IInd", flexFieldDefinitionDataStore.getById(flexFieldPersisted.getId())
                    .getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDelete() {
        try {
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("Secondary Email PPPo1");
            flexFieldDefinition.setDescription("Secondary Email");
            flexFieldDefinition.setDefaultValue("abc@xyz.com");
            flexFieldDefinition.setDisplayName("Secondary Email Address");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the valid email");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);

            FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted.getId());
            flexFieldDefinitionDataStore.deleteFlexField(flexFieldPersisted);
            assertNull(flexFieldDefinitionDataStore.getById(flexFieldPersisted.getId()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetRange() {
        try {
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("FTP Address URI");
            flexFieldDefinition.setDescription("FTP Address");
            flexFieldDefinition.setDefaultValue("ftp://edifecs.com");
            flexFieldDefinition.setDisplayName("FTP Address");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the valid URL");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);

            FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted.getId());

            Collection<FlexFieldDefinition> fields = flexFieldDefinitionDataStore.getRange(0, 1);
            assertNotEquals(0, fields.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetById() {
        try {
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("Phone Extension ##");
            flexFieldDefinition.setDescription("Phone Extension1");
            flexFieldDefinition.setDefaultValue("1");
            flexFieldDefinition.setDisplayName("Phone Extension1");
            flexFieldDefinition.setRegEx("[0-9]+");
            flexFieldDefinition.setValidationMessage("Please enter the valid extension#");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.LONG);

            FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted.getId());

            FlexFieldDefinition flexFieldDefinition1 = flexFieldDefinitionDataStore.getById(flexFieldPersisted.getId());

            assertEquals(flexFieldDefinition.getName(), flexFieldDefinition1.getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test(expected = ItemAlreadyExistsException.class)
    public void testFieldDuplicate() throws Exception{
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("Phone Extension ###");
            flexFieldDefinition.setDescription("Phone Extension");
            flexFieldDefinition.setDefaultValue("1");
            flexFieldDefinition.setDisplayName("Phone Extension");
            flexFieldDefinition.setRegEx("[0-9]+");
            flexFieldDefinition.setValidationMessage("Please enter the valid extension#");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.LONG);

            FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted.getId());

            FlexFieldDefinition flexFieldDefinition2 = new FlexFieldDefinition();
            flexFieldDefinition2.setName("Phone Extension ###");
            flexFieldDefinition2.setDescription("Phone Extension");
            flexFieldDefinition2.setDefaultValue("1");
            flexFieldDefinition2.setDisplayName("Phone Extension");
            flexFieldDefinition2.setRegEx("[0-9]+");
            flexFieldDefinition2.setValidationMessage("Please enter the valid extension#");
            flexFieldDefinition2.setActiveFlag(true);
            flexFieldDefinition2.setDataType(FieldType.LONG);

            FlexFieldDefinition flexFieldPersisted2 = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition2);
            assertNotNull(flexFieldPersisted2.getId());

    }
}
