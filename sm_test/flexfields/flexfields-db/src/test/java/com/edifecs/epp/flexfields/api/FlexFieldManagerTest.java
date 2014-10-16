package com.edifecs.epp.flexfields.api;

import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * FlexFieldManager Tester.
 */
public class FlexFieldManagerTest extends FlexFieldRegistryTestBase {

    private IFlexFieldManager flexFieldManager;
    private IFlexFieldRegistry flexFieldRegistry;
    @Before
    public void before() throws Exception {
        flexFieldManager = new FlexFieldManager(null);
        flexFieldRegistry = new FlexFieldRegistry(null);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: setFlexFieldValue(FlexFieldValue flexFieldValue)
     */
    @Test
    public void testSetFlexFieldValue() {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexFieldDefinition();
            FlexGroup flexGroup = createFlexGroup();

            Collection<FlexFieldDefinition> fields = new ArrayList<FlexFieldDefinition>();
            fields.add(flexFieldDefinition);
            FlexGroup flexGroup1 = flexFieldRegistry.addFieldsToGroup(fields,flexGroup);

            assertNotNull(flexGroup1);
            assertNotNull(flexGroup1.getId());
            List<FlexFieldDefinition> flexFieldDefinitions =(List)flexGroup1.getFlexFieldsCollection();
            assertNotNull(flexFieldDefinitions);
            assertNotEquals(flexFieldDefinitions.size(), 0);

            FlexFieldDefinition flexFieldDefinition1 = flexFieldDefinitions.get(0);

            FlexFieldValue flexFieldValue = createFlexFieldValue(flexGroup1, flexFieldDefinition1, "http://www.aspemporium.com");

            FlexFieldValue flexFieldValuePersisted = flexFieldManager.setFlexFieldValue(flexFieldValue);

            assertNotNull(flexFieldValuePersisted);
            assertNotNull(flexFieldValuePersisted.getId());
            assertEquals(flexFieldValuePersisted.getValue(), "http://www.aspemporium.com");

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Method: getFlexFieldValue(long id)
     */
    @Test
    public void testGetFlexFieldValue() {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexFieldDefinition();
            FlexGroup flexGroup = createFlexGroup();

            Collection<FlexFieldDefinition> fields = new ArrayList<FlexFieldDefinition>();
            fields.add(flexFieldDefinition);
            FlexGroup flexGroup1 = flexFieldRegistry.addFieldsToGroup(fields,flexGroup);

            assertNotNull(flexGroup1);
            assertNotNull(flexGroup1.getId());
            List<FlexFieldDefinition> flexFieldDefinitions =(List)flexGroup1.getFlexFieldsCollection();
            assertNotNull(flexFieldDefinitions);
            assertNotEquals(flexFieldDefinitions.size(), 0);

            FlexFieldDefinition flexFieldDefinition1 = flexFieldDefinitions.get(0);

            FlexFieldValue flexFieldValue = createFlexFieldValue(flexGroup1, flexFieldDefinition1, "http://www.aspemporium.com");

            FlexFieldValue flexFieldValuePersisted = flexFieldManager.setFlexFieldValue(flexFieldValue);

            assertNotNull(flexFieldValuePersisted);
            assertNotNull(flexFieldValuePersisted.getId());
            assertEquals(flexFieldValuePersisted.getValue(), "http://www.aspemporium.com");

            FlexFieldValue flexFieldValue1 = flexFieldManager.getFlexFieldValue(flexFieldValuePersisted.getId());
            assertNotNull(flexFieldValue1.getId());
            assertEquals(flexFieldValue1.getValue(), "http://www.aspemporium.com");

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Method: getFlexFieldValues(FlexGroup flexGroup)
     */
    @Test
    public void testGetFlexFieldValues()  {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexFieldDefinition();
            FlexGroup flexGroup = createFlexGroup();

            Collection<FlexFieldDefinition> fields = new ArrayList<FlexFieldDefinition>();
            fields.add(flexFieldDefinition);
            FlexGroup flexGroup1 = flexFieldRegistry.addFieldsToGroup(fields,flexGroup);

            assertNotNull(flexGroup1);
            assertNotNull(flexGroup1.getId());
            List<FlexFieldDefinition> flexFieldDefinitions =(List)flexGroup1.getFlexFieldsCollection();
            assertNotNull(flexFieldDefinitions);
            assertNotEquals(flexFieldDefinitions.size(), 0);

            FlexFieldDefinition flexFieldDefinition1 = flexFieldDefinitions.get(0);
            FlexFieldValue flexFieldValue = createFlexFieldValue(flexGroup1, flexFieldDefinition1, "http://www.aspemporium.com");
            FlexFieldValue flexFieldValuePersisted = flexFieldManager.setFlexFieldValue(flexFieldValue);

            Map<FlexFieldDefinition, FlexFieldValue> flexFieldValues = flexFieldManager.getFlexFieldValues(flexGroup1);
            assertNotEquals(flexFieldValues.size(), 0);
            for(Map.Entry entry: flexFieldValues.entrySet()) {
                FlexFieldDefinition _flexFieldDef = (FlexFieldDefinition)entry.getKey();
                FlexFieldValue _flexFieldValue =(FlexFieldValue) entry.getValue();
                assertEquals( "http://www.aspemporium.com",_flexFieldValue.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


}
