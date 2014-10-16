package com.edifecs.epp.flexfields.api;

import com.edifecs.epp.flexfields.model.Context;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexGroup;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


import java.util.*;

import static org.junit.Assert.*;

/**
 * FlexFieldRegistry Tester.
 */
public class FlexFieldRegistryTest extends FlexFieldRegistryTestBase {
    private IFlexFieldRegistry flexFieldRegistry;

    @Before
    public void before() throws Exception {
        flexFieldRegistry = new FlexFieldRegistry(null);
    }


    @After
    public void after() throws Exception {
    }

    /**
     * Method: getFlexFieldDefinitions(FlexGroup flexGroup)
     */
    @Test
    public void testGetFlexFieldDefinitions()  {
        try {
            FlexGroup flexGroup = createFlexGroup();
            FlexGroup flexGroupPersisted = flexFieldRegistry.setFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());

            FlexFieldDefinition field1 = createFlexFieldDefinition(10);
            FlexFieldDefinition flexFieldDefinition = flexFieldRegistry.setFlexField(field1);
            assertNotNull(flexFieldDefinition.getId());

            FlexFieldDefinition field2 = createFlexFieldDefinition(20);
            FlexFieldDefinition flexFieldDefinition2 = flexFieldRegistry.setFlexField(field2);
            assertNotNull(flexFieldDefinition2.getId());

            List<FlexFieldDefinition> fields = new ArrayList<>();
            fields.add(flexFieldDefinition);
            fields.add(flexFieldDefinition2);

            FlexGroup flexGroup1 = flexFieldRegistry.addFieldsToGroup(fields, flexGroupPersisted);
            assertNotNull(flexGroup1.getId());

            Collection<FlexFieldDefinition> flexFieldDefinitionCollection = flexFieldRegistry.getFlexFieldDefinitions(flexGroup1);
            assertNotNull(flexFieldDefinitionCollection);
            assertEquals(2, flexFieldDefinitionCollection.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Method: getFields(Map<String, String> context)
     */
    @Test
    public void testGetFields()  {
        try {
            Map<Context, String> contextMap = new EnumMap<Context, String>(Context.class);
            contextMap.put(Context.TENANT, "*");
            contextMap.put(Context.APPLICATION, "CM");
            contextMap.put(Context.ENTITY, "CMSData");

            FlexGroup flexGroup = createFlexGroup();
            FlexGroup flexGroupPersisted = flexFieldRegistry.setFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());

            FlexFieldDefinition field1 = createFlexFieldDefinition();
            FlexFieldDefinition flexFieldDefinition = flexFieldRegistry.setFlexField(field1);
            assertNotNull(flexFieldDefinition.getId());

            FlexFieldDefinition field2 = createFlexFieldDefinition();
            FlexFieldDefinition flexFieldDefinition2 = flexFieldRegistry.setFlexField(field2);
            assertNotNull(flexFieldDefinition2.getId());

            List<FlexFieldDefinition> fields = new ArrayList<>();
            fields.add(flexFieldDefinition);
            fields.add(flexFieldDefinition2);

            FlexGroup flexGroup1 = flexFieldRegistry.addFieldsToGroup(fields, flexGroupPersisted);
            assertNotNull(flexGroup1.getId());

            List<FlexGroup> flexGroupTree = flexFieldRegistry.getFields(contextMap);
            assertNotNull(flexGroupTree);

            for (FlexGroup flexGroupNested : flexGroupTree) {
                assertNotNull(flexGroupNested);
                Collection<FlexFieldDefinition> flexFieldDefinitions = flexFieldRegistry.getFlexFieldDefinitions(flexGroupNested);
                assertNotNull(flexFieldDefinitions);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }




    /**
     * Method: setFlexGroup(Collection<FlexGroup> flexGroups)
     */
    @Test
    public void testSetFlexGroup()  {
        try {
            List<FlexGroup> flexGroupList = new ArrayList<FlexGroup>(10);
            for (int i = 10; i < 10; i++) {
                FlexGroup flexGroup = createFlexGroup();
                flexGroupList.add(flexGroup);
            }
            flexFieldRegistry.setFlexGroup(flexGroupList);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: addFieldsToGroup(Collection<FlexFieldDefinition> flexFields, FlexGroup flexGroup)
     */
    @Test
    public void testAddFieldsToGroup() {
        try {
            FlexGroup flexGroup = createFlexGroup();
            FlexGroup flexGroupPersisted = flexFieldRegistry.setFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());

            FlexFieldDefinition field1 = createFlexFieldDefinition();
            FlexFieldDefinition flexFieldDefinition = flexFieldRegistry.setFlexField(field1);
            assertNotNull(flexFieldDefinition.getId());

            FlexFieldDefinition field2 = createFlexFieldDefinition();
            FlexFieldDefinition flexFieldDefinition2 = flexFieldRegistry.setFlexField(field2);
            assertNotNull(flexFieldDefinition2.getId());

            List<FlexFieldDefinition> fields = new ArrayList<>();
            fields.add(flexFieldDefinition);
            fields.add(flexFieldDefinition2);

            FlexGroup flexGroup1 = flexFieldRegistry.addFieldsToGroup(fields, flexGroupPersisted);
            assertNotNull(flexGroup1.getId());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: setFlexField(FlexFieldDefinition flexFieldDefinition)
     */
    @Test
    public void testSetFlexField() {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexFieldDefinition();
            flexFieldDefinition = flexFieldRegistry.setFlexField(flexFieldDefinition);
            assertNotNull(flexFieldDefinition.getId());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: deleteFlexGroup(FlexGroup flexGroup)
     */
    @Test
    public void testDeleteFlexGroup()  {
        try {
            FlexFieldDefinition flexFieldDefinition = createFlexFieldDefinition();
            flexFieldDefinition = flexFieldRegistry.setFlexField(flexFieldDefinition);
            assertNotNull(flexFieldDefinition.getId());
            flexFieldRegistry.deleteFlexFieldDefinition(flexFieldDefinition);
            assertNull(flexFieldRegistry.getFlexFieldDefinitionById(flexFieldDefinition.getId()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: deleteFlexFieldDefinition(FlexFieldDefinition flexFieldDefinition)
     */
    @Test
    public void testDeleteFlexFieldDefinition()  {
        try {
            FlexFieldDefinition flexFieldDefinition = flexFieldRegistry.setFlexField(createFlexFieldDefinition());
            assertNotNull(flexFieldDefinition.getId());
            flexFieldRegistry.deleteFlexFieldDefinition(flexFieldDefinition);
            assertNull(flexFieldRegistry.getFlexFieldDefinitionById(flexFieldDefinition.getId()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: setParent(FlexGroup parent, FlexGroup child)
     */
    @Test
    public void testSetParent() {
        try {
            FlexGroup parent = createFlexGroup();
            FlexGroup child = createFlexGroup();
            FlexGroup parentPersisted = flexFieldRegistry.setParent(parent, child);
            assertNotNull(parentPersisted.getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
