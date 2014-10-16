package com.edifecs.epp.flexfields.jpa.datastore;

import com.edifecs.epp.flexfields.datastore.IFlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.datastore.IFlexGroupDataStore;
import com.edifecs.epp.flexfields.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexGroupDataStore;
import com.edifecs.epp.flexfields.model.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.Collection;
import java.util.EnumMap;

import java.util.Map;

import static org.junit.Assert.*;

public class FlexGroupDataStoreTest {


    private IFlexGroupDataStore flexGroupDataStore;
    private IFlexFieldDefinitionDataStore flexFieldDefinitionDataStore;

    @Before
    public void setUp() throws Exception {
        DatabaseDataStore databaseDataStore = new DatabaseDataStore();
        flexGroupDataStore = new FlexGroupDataStore();
        flexFieldDefinitionDataStore = new FlexFieldDefinitionDataStore();
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: createFlexGroup(FlexGroup flexGroup)
     */
    @Test
    public void testCreateFlexGroup() throws Exception {
        try {
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("ESM");
            flexGroup.setComponentName("UserProfile");
            flexGroup.setDescription("Address");
            flexGroup.setEntityName("User");
            flexGroup.setName("Secondary Address");
            flexGroup.setDisplayName("Secondary Address");
            flexGroup.setTenantName("BCBS_CA");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());
            assertNotNull(flexGroupDataStore.getById(flexGroupPersisted.getId()));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }

    /**
     * Method: updateFlexGroup(FlexGroup flexGroup)
     */
    @Test
    public void testUpdateFlexGroup() throws Exception {
        try {
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("ESM");
            flexGroup.setComponentName("UserProfile");
            flexGroup.setDescription("Address");
            flexGroup.setEntityName("User");
            flexGroup.setName("Secondary Address");
            flexGroup.setDisplayName("Secondary Address");
            flexGroup.setTenantName("BCBS_CA");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());
            assertNotNull(flexGroupDataStore.getById(flexGroupPersisted.getId()));
            flexGroupPersisted.setName("Home Address");
            FlexGroup flexGroupUpdated = flexGroupDataStore.updateFlexGroup(flexGroupPersisted);
            assertEquals("Home Address", flexGroupUpdated.getName());

        } catch (Exception exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
    }

    /**
     * Method: deleteFlexGroup(FlexGroup flexGroup)
     */
    @Test
    public void testDeleteFlexGroup() throws Exception {
        try {
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("BPM");
            flexGroup.setComponentName("WLM");
            flexGroup.setDescription("Claim Processed");
            flexGroup.setEntityName("Claim");
            flexGroup.setName("ClaimProcessed");
            flexGroup.setDisplayName("Claims Processed #");
            flexGroup.setTenantName("BCBS_LA");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());
            assertNotNull(flexGroupDataStore.getById(flexGroupPersisted.getId()));

            flexGroupDataStore.deleteFlexGroup(flexGroupPersisted);
            assertNull(flexGroupDataStore.getById(flexGroupPersisted.getId()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: getRange(long startRecord, long recordCount)
     */
    @Test
    public void testGetRange() throws Exception {
        try {
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("TDM");
            flexGroup.setComponentName("VDD");
            flexGroup.setDescription("Files Count");
            flexGroup.setEntityName("EDIFile");
            flexGroup.setName("EDIFile");
            flexGroup.setDisplayName("Upload EDI File");
            flexGroup.setTenantName("BCBS_WA");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());
            assertNotNull(flexGroupDataStore.getById(flexGroupPersisted.getId()));

            Collection<FlexGroup> flexGroups = flexGroupDataStore.getRange(0, 10);
            assertNotEquals(0, flexGroups.size());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: getById(long id)
     */
    @Test
    public void testGetById() throws Exception {
        try {
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("TM");
            flexGroup.setComponentName("TPM");
            flexGroup.setDescription("Claim Inquiry");
            flexGroup.setEntityName("ClaimInquiry");
            flexGroup.setName("Claim Inquiry");
            flexGroup.setDisplayName("Claims Inquiry #");
            flexGroup.setTenantName("BCBS_TX");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());
            assertNotNull(flexGroupDataStore.getById(flexGroupPersisted.getId()));
            FlexGroup flexGroup1 = flexGroupDataStore.getById(flexGroupPersisted.getId());
            assertEquals(flexGroup.getName(), flexGroup1.getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: addFlexFieldToGroup(FlexGroup flexGroup, FlexFieldDefinition flexFieldDefinition, int sequence)
     */
    @Test
    public void testAddFlexFieldToGroup() {
        try {
            //Create FlexField
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("FTP Address #3");
            flexFieldDefinition.setDescription("FTP Address");
            flexFieldDefinition.setDefaultValue("ftp://edifecs.com");
            flexFieldDefinition.setDisplayName("FTP Address");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the valid URL");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);
            FlexFieldDefinition flexFieldPersisted = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted.getId());
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
            assertNotNull(flexGroupPersisted.getId());
            //Add Field to Group
            flexGroupDataStore.addFlexFieldToGroup(flexGroupPersisted, flexFieldPersisted, 1);

            Collection<FlexFieldDefinition> flexFieldDefinitions = flexGroupDataStore.getFields(flexGroupPersisted);
            assertEquals(1, flexFieldDefinitions.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: removeFlexFieldFromGroup(FlexGroup flexGroup, FlexFieldDefinition flexFieldDefinition)
     */
    @Test
    public void testRemoveFlexFieldFromGroup() throws Exception {
        try {
            //Create FlexField
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("FTP Address1");
            flexFieldDefinition.setDescription("FTP Address");
            flexFieldDefinition.setDefaultValue("ftp://edifecs.com");
            flexFieldDefinition.setDisplayName("FTP Address");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the valid URL");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);
            FlexFieldDefinition flexFieldPersisted1 = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted1.getId());
            // Create 2nd Field Definition
            flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("Email Address");
            flexFieldDefinition.setDescription("Email Address");
            flexFieldDefinition.setDefaultValue("mail.edifecs.com");
            flexFieldDefinition.setDisplayName("Email Address");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the valid Email Address");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);
            FlexFieldDefinition flexFieldPersisted2 = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted2.getId());
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
            assertNotNull(flexGroupPersisted.getId());
            //Add Field to Group
            flexGroupDataStore.addFlexFieldToGroup(flexGroupPersisted, flexFieldPersisted1, 1);
            flexGroupDataStore.addFlexFieldToGroup(flexGroupPersisted, flexFieldPersisted2, 1);

            Collection<FlexFieldDefinition> flexFieldDefinitions = flexGroupDataStore.getFields(flexGroupPersisted);
            assertEquals(2, flexFieldDefinitions.size());

            flexGroupDataStore.removeFlexFieldFromGroup(flexGroupPersisted, flexFieldPersisted1);

            flexFieldDefinitions = flexGroupDataStore.getFields(flexGroupPersisted);
            assertEquals(1, flexFieldDefinitions.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    /**
     * Method: getRequiredPermission(FlexGroup group)
     */
    @Test
    public void testGetRequiredPermission() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: getFlexGroupHierarchy(FlexGroup flexGroup)
     */
    @Test
    public void testGetFlexGroupHierarchy() throws Exception {
        try {
            //Create FlexField
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName("FTP Address #2");
            flexFieldDefinition.setDescription("FTP Address");
            flexFieldDefinition.setDefaultValue("ftp://edifecs.com");
            flexFieldDefinition.setDisplayName("FTP Address");
            flexFieldDefinition.setRegEx("[a-zA-Z]+\\\\.?");
            flexFieldDefinition.setValidationMessage("Please enter the valid URL");
            flexFieldDefinition.setActiveFlag(true);
            flexFieldDefinition.setDataType(FieldType.STRING);
            FlexFieldDefinition flexFieldPersisted1 = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
            assertNotNull(flexFieldPersisted1.getId());
            //Create FlexGroup
            FlexGroup parentFlexGroup = new FlexGroup();
            parentFlexGroup.setAppName("TDM");
            parentFlexGroup.setComponentName("VDD");
            parentFlexGroup.setDescription("Files Count");
            parentFlexGroup.setEntityName("EDIFile");
            parentFlexGroup.setName("EDIFile");
            parentFlexGroup.setDisplayName("Upload EDI File");
            parentFlexGroup.setTenantName("BCBS_WA");
            FlexGroup parentFlexGroupPersisted = flexGroupDataStore.createFlexGroup(parentFlexGroup);
            assertNotNull(parentFlexGroupPersisted.getId());

            //Add Field to Group
            flexGroupDataStore.addFlexFieldToGroup(parentFlexGroupPersisted, flexFieldPersisted1, 1);
            // child flexGroup
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("TDM");
            flexGroup.setComponentName("FTPServer");
            flexGroup.setDescription("FTP Server");
            flexGroup.setEntityName("FTPServer");
            flexGroup.setName("FTPServer");
            flexGroup.setDisplayName("Set FTP server");
            flexGroup.setTenantName("BCBS_WA");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());

            //Add Field to Group
            flexGroupDataStore.addFlexFieldToGroup(flexGroupPersisted, flexFieldPersisted1, 1);
            //Add child group to parent group
            parentFlexGroup = flexGroupDataStore.setParent(parentFlexGroupPersisted, flexGroupPersisted);
            assertNotNull(parentFlexGroup.getId());
            FlexGroup tree = flexGroupDataStore.getFlexGroupHierarchy(parentFlexGroup,0);
            assertNotNull(tree);
            assertEquals(1, tree.getChildren().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Method: getFlexGroupsByContext(String contextName, String contextValue)
     */
    @Test
    public void testGetFlexGroupsByContext() throws Exception {
        try {
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("CM");
            flexGroup.setComponentName("ICD10");
            flexGroup.setDescription("CMS Link");
            flexGroup.setEntityName("CMSData");
            flexGroup.setName("CMSLink");
            flexGroup.setDisplayName("CMS Link");
            flexGroup.setTenantName("Humana");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());

            Collection<FlexGroup> flexGroupList = flexGroupDataStore.getFlexGroupsByContext("tenantName", "Humana");
            assertEquals(1, flexGroupList.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: getFlexGroupsByContext(Map<String, String> contextMap)
     */
    @Test
    public void testGetFlexGroupsByContextMap() throws Exception {
        try {
            FlexGroup flexGroup = new FlexGroup();
            flexGroup.setAppName("OR");
            flexGroup.setComponentName("OR ETL");
            flexGroup.setDescription("OR ETL File Name");
            flexGroup.setEntityName("ORMetaData");
            flexGroup.setName("OR ETL File Name");
            flexGroup.setDisplayName("OR ETL File Name");
            flexGroup.setTenantName("Cigna");
            FlexGroup flexGroupPersisted = flexGroupDataStore.createFlexGroup(flexGroup);
            assertNotNull(flexGroupPersisted.getId());
            Map<Context, String> contextMap = new EnumMap<Context,String>(Context.class);
            contextMap.put(Context.APPLICATION, "OR");
            contextMap.put(Context.ENTITY, "ORMetaData");
            Collection<FlexGroup> flexGroupList = flexGroupDataStore.getFlexGroupsByContext(contextMap);
            assertEquals(1, flexGroupList.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

} 
