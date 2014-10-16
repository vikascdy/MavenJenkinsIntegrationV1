package com.edifecs.epp.flexfields.handler;

import com.edifecs.epp.flexfields.api.FlexFieldManager;
import com.edifecs.epp.flexfields.api.FlexFieldRegistry;
import com.edifecs.epp.flexfields.api.IFlexFieldManager;
import com.edifecs.epp.flexfields.api.IFlexFieldRegistry;
import com.edifecs.epp.flexfields.model.*;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

import com.edifecs.epp.security.IAuthorizationManager;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by sandeep.kath on 5/28/2014.
 */
public class FlexFieldHandler extends AbstractCommandHandler implements IFlexFieldHandler {

    //TODO: Review and handler is just calling flexfield API, it may need to extended with validations/security and exceptions.


    protected IFlexFieldManager flexFieldManager;
    protected IFlexFieldRegistry flexFieldRegistry;

    public FlexFieldHandler(Properties properties) throws Exception {
        this.flexFieldManager = new FlexFieldManager(properties);
        this.flexFieldRegistry = new FlexFieldRegistry(properties);

    }

    @Override
    public Collection<FlexGroup> getFields(@Arg(name = "contextMap", required = false) Map<String, String> map) throws Exception {


        Map<Context, String> contextMap = new EnumMap<Context, String>(Context.class);
        for (String key : map.keySet()) {
            contextMap.put(Context.fromString(key), map.get(key));
        }

        List<FlexGroup> flexGroups = flexFieldRegistry.getFields(contextMap);
        IAuthorizationManager authorizationManager = getCommandCommunicator().getSecurityManager().getAuthorizationManager();
        filterFieldsByPermissions(flexGroups, authorizationManager);


        return flexGroups;

    }


    @Override
    public FlexFieldValue setFlexFieldValue(@Arg(name = "flexFieldValue", required = true) FlexFieldValue flexFieldValue) throws Exception {
        IAuthorizationManager authorizationManager = getCommandCommunicator().getSecurityManager().getAuthorizationManager();
        FlexGroup flexGroup = flexFieldRegistry.getFlexGroupById(flexFieldValue.getFlexGroupId());
        if (flexGroup != null) {
            if (checkPermission(flexGroup, authorizationManager)) {
                return flexFieldManager.setFlexFieldValue(flexFieldValue);
            } else {
                throw new Exception("Permission Denied"); //TODO: Need Better mechanism to send this error
            }
        } else throw new IllegalArgumentException("Invalid Flex Group Id");
    }

    @Override
    public FlexGroup addFieldToGroup(@Arg(name = "flexFieldDefinition", required = true) FlexFieldDefinition flexFieldDefinition, @Arg(name = "flexGroup", required = true) FlexGroup flexGroup) throws Exception {
        return flexFieldRegistry.addFieldToGroup(flexFieldDefinition, flexGroup);
    }

    @Override
    public FlexGroup addFieldsToGroup(@Arg(name = "flexFields", required = true) Collection<FlexFieldDefinition> flexFields, @Arg(name = "flexGroup", required = true) FlexGroup flexGroup) throws Exception {
        return flexFieldRegistry.addFieldsToGroup(flexFields, flexGroup);
    }

    @Override
    public void deleteFlexGroup(@Arg(name = "flexGroup", required = true) FlexGroup flexGroup) throws Exception {
        flexFieldRegistry.deleteFlexGroup(flexGroup);
    }

    @Override
    public void deleteFlexFieldDefinition(@Arg(name = "flexFieldDefinition", required = true) FlexFieldDefinition flexFieldDefinition) throws Exception {
        flexFieldRegistry.deleteFlexFieldDefinition(flexFieldDefinition);
    }

    @Override
    public FlexGroup setParent(@Arg(name = "parent", required = true) FlexGroup parent, @Arg(name = "child", required = true) FlexGroup child) throws Exception {
        return flexFieldRegistry.setParent(parent, child);
    }

    @Override
    public boolean parseAppManifest(@Arg(name = "manifestString", required = true) String manifestString) throws Exception {
        return flexFieldRegistry.parseAppManifest(manifestString);
    }

    private boolean checkPermission(FlexGroup flexGroup, IAuthorizationManager authorizationManager) {
        Collection<FlexGroup> flexGroups = new ArrayList();
        flexGroups.add(flexGroup);
        filterFieldsByPermissions(flexGroups, authorizationManager);
        if (flexGroups.size() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    private void filterFieldsByPermissions(Collection<FlexGroup> flexGroups, IAuthorizationManager authorizationManager) {
        Set groupPermissionsSet = new HashSet();
        Iterator<FlexGroup> iterator = flexGroups.iterator();
        while (iterator.hasNext()) {
            FlexGroup flexGroup = iterator.next();
            if (flexGroup.isRestricted() != null && flexGroup.getRestricted() == true) {
                String reqPermissions = flexGroup.getPermissionRequired();
                if (reqPermissions != null) {
                    if (!reqPermissions.trim().equals("")) {
                        if (authorizationManager.isPermitted(reqPermissions) == false) {
                            iterator.remove();
                            continue;
                        }
                        StringTokenizer st = new StringTokenizer(reqPermissions, ",");
                        while (st.hasMoreTokens()) {
                            groupPermissionsSet.add(st.nextToken());
                        }
                    }
                }
            }
            Collection<FlexFieldDefinition> flexFieldDefinitionCollection = flexGroup.getFlexFieldsCollection();
            if (flexFieldDefinitionCollection != null) {
                Iterator<FlexFieldDefinition> iteratorFlexField = flexFieldDefinitionCollection.iterator();
                while (iteratorFlexField.hasNext()) {
                    FlexFieldDefinition flexFieldDefinition = iteratorFlexField.next();

                    if (flexFieldDefinition.isRestricted() != null && flexFieldDefinition.isRestricted() == true) {
                        Set fieldPermissionsSet = new HashSet();
                        String reqPermissions = flexFieldDefinition.getRequiredPermission();
                        StringTokenizer st = new StringTokenizer(reqPermissions, ",");
                        while (st.hasMoreTokens()) {
                            fieldPermissionsSet.add(st.nextToken());
                        }
                        fieldPermissionsSet.addAll(groupPermissionsSet);
                        String requiredPermissions = StringUtils.join(fieldPermissionsSet, ',');
                        if (authorizationManager.isPermitted(requiredPermissions) == false) {
                            iteratorFlexField.remove();
                        }
                    }
                }
            }
            filterFieldsByPermissions(flexGroup.getChildren(), authorizationManager);
        }

    }

    /* Temporary Code to return the JSON for UI development */
    private Collection<FlexGroup> getFlexGroupTreeNodes() {
        FlexGroup flexGroup = new FlexGroup();
        flexGroup.setId(100L);
        flexGroup.setAppName("ESM");
        flexGroup.setComponentName("UserProfile");
        flexGroup.setDescription("Billing Address");
        flexGroup.setDisplayName("Billing Address");
        flexGroup.setEntityName("UserEntity");
        flexGroup.setName("Billing Address");
        flexGroup.setTenantName("*");

        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
        flexFieldDefinition.setId(999L);
        flexFieldDefinition.setName("Provider");
        flexFieldDefinition.setDescription("Provider flex field required to be captured at forms");
        flexFieldDefinition.setDefaultValue("Overlake Hospital");
        flexFieldDefinition.setDisplayName("Provider Name");
        flexFieldDefinition.setRegEx("((mailto\\:|(news|(ht|f)tp(s?))\\://){1}\\S+)");
        flexFieldDefinition.setValidationMessage("Please enter the proper name");
        flexFieldDefinition.setActiveFlag(true);
        flexFieldDefinition.setDataType(FieldType.STRING);


        FlexFieldValue flexFieldValue = new FlexFieldValue();
        flexFieldValue.setId(111L);
        flexFieldValue.setEntityName("UserEntity");
        flexFieldValue.setEntityID(124);
        flexFieldValue.setValue("Fortis Hospital");

        flexFieldDefinition.setFlexFieldValue(flexFieldValue);
        Collection<FlexFieldDefinition> flexFieldDefinitions = new ArrayList<>();
        flexFieldDefinitions.add(flexFieldDefinition);

        flexGroup.setFlexFieldsCollection(flexFieldDefinitions);

        List<FlexGroup> flexGroups = new ArrayList<FlexGroup>();
        FlexGroup flexGroup1 = createGroup();
        flexGroup.addChild(flexGroup1);

        flexGroups.add(flexGroup);
        return flexGroups;
    }

    /* Temporary code to generate JSON for UI*/

    private FlexGroup createGroup() {
        FlexGroup flexGroup = new FlexGroup();
        flexGroup.setId(200L);
        flexGroup.setAppName("ESM");
        flexGroup.setComponentName("UserProfile");
        flexGroup.setDescription("Patient Address");
        flexGroup.setDisplayName("Patient Address");
        flexGroup.setEntityName("UserEntity");
        flexGroup.setName("Patient Address");
        flexGroup.setTenantName("*");

        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
        flexFieldDefinition.setId(299L);
        flexFieldDefinition.setName("Street #");
        flexFieldDefinition.setDescription("Street #");
        flexFieldDefinition.setDefaultValue("");
        flexFieldDefinition.setDisplayName("Street Address");
        flexFieldDefinition.setRegEx(null);
        flexFieldDefinition.setValidationMessage("");
        flexFieldDefinition.setRequired(true);
        flexFieldDefinition.setActiveFlag(true);
        flexFieldDefinition.setDataType(FieldType.STRING);


        FlexFieldDefinition flexFieldDefinition2 = new FlexFieldDefinition();
        flexFieldDefinition2.setId(399L);
        flexFieldDefinition2.setName("City");
        flexFieldDefinition2.setDescription("City");
        flexFieldDefinition2.setDefaultValue("");
        flexFieldDefinition2.setDisplayName("City");
        flexFieldDefinition2.setRegEx(null);
        flexFieldDefinition2.setValidationMessage("");
        flexFieldDefinition2.setRequired(true);
        flexFieldDefinition2.setActiveFlag(true);
        flexFieldDefinition2.setDataType(FieldType.STRING);


        FlexFieldDefinition flexFieldDefinition3 = new FlexFieldDefinition();
        flexFieldDefinition3.setId(499L);
        flexFieldDefinition3.setName("State");
        flexFieldDefinition3.setDescription("State");
        flexFieldDefinition3.setDefaultValue("");
        flexFieldDefinition3.setDisplayName("State");
        flexFieldDefinition3.setRegEx(null);
        flexFieldDefinition3.setValidationMessage("");
        flexFieldDefinition3.setRequired(true);
        flexFieldDefinition3.setActiveFlag(true);
        flexFieldDefinition3.setDataType(FieldType.SELECTONE);
        Map<String, String> states = new HashMap<>();
        states.put("CA", "California");
        states.put("WA", "Washington");
        flexFieldDefinition3.setSelectOptions(states);

        FlexFieldDefinition flexFieldDefinition4 = new FlexFieldDefinition();
        flexFieldDefinition4.setId(599L);
        flexFieldDefinition4.setName("Insured");
        flexFieldDefinition4.setDescription("Insured");
        flexFieldDefinition4.setDefaultValue("false");
        flexFieldDefinition4.setDisplayName("Insured");
        flexFieldDefinition4.setRegEx(null);
        flexFieldDefinition4.setValidationMessage("");
        flexFieldDefinition4.setRequired(true);
        flexFieldDefinition4.setActiveFlag(true);
        flexFieldDefinition4.setDataType(FieldType.BOOLEAN);

        List<FlexFieldDefinition> flexFields = new ArrayList<>();
        flexFields.add(flexFieldDefinition);
        flexFields.add(flexFieldDefinition2);
        flexFields.add(flexFieldDefinition3);
        flexFields.add(flexFieldDefinition4);

        flexGroup.setFlexFieldsCollection(flexFields);
        return flexGroup;
    }
}
