package com.edifecs.epp.flexfields.api;

import com.edifecs.epp.flexfields.datastore.IFlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.datastore.IFlexGroupDataStore;
import com.edifecs.epp.flexfields.exception.FlexFieldRegistryException;
import com.edifecs.epp.flexfields.exception.ItemNotFoundException;
import com.edifecs.epp.flexfields.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.jpa.helper.FlexGroupDataStore;
import com.edifecs.epp.flexfields.model.*;
import com.edifecs.epp.packaging.manifest.*;

import java.io.InputStream;
import java.util.*;

/**
 * Created by sandeep.kath on 5/11/2014.
 */
public class FlexFieldRegistry implements IFlexFieldRegistry {

    private IFlexFieldDefinitionDataStore flexFieldDefinitionDataStore;
    private IFlexGroupDataStore flexGroupDataStore;
    private DatabaseDataStore databaseDataStore;

    private Map<String, String> entityMap = new HashMap<>();

    public FlexFieldRegistry(Properties properties) throws Exception {
        if(properties == null) {
            databaseDataStore = new DatabaseDataStore();
        } else {
            databaseDataStore = new DatabaseDataStore(properties);
        }
        flexFieldDefinitionDataStore = new FlexFieldDefinitionDataStore();
        flexGroupDataStore = new FlexGroupDataStore();
        entityMap.put("user", "UserEntity");
        entityMap.put("tenant", "TenantEntity");
        entityMap.put("org", "OrganizationEntity");
        entityMap.put("site", "SiteEntity");
    }

    @Override
    public FlexFieldDefinition getFlexFieldDefinitionById(long id) throws Exception {
        return flexFieldDefinitionDataStore.getById(id);

    }


    @Override
    public boolean parseAppManifest(InputStream manifestStream) throws Exception {
        Manifest m = Manifest.fromYaml(manifestStream, "flexfield").get();
        return parseManifest(m);
    }

    private boolean parseManifest(Manifest m) throws Exception {
        String appName = m.name();
        for (PhysicalComponent physicalComponent : m.getPhysicalComponents()) {
            String componentName = physicalComponent.name();
            for (Property profileAttribute : physicalComponent.getProfileAttributes()) {
                FlexGroup flexGroupPersisted = createFlexGroup(appName, componentName, entityMap.get(profileAttribute.scope()), profileAttribute.displayName().get());
                createFlexFields(profileAttribute, flexGroupPersisted);
            }
        }
        return true;
    }

    @Override
    public boolean parseAppManifest(String manifestString) throws Exception {
        Manifest m =    Manifest.fromYaml(manifestString, "flexfield").get();
        return parseManifest(m);
    }

    @Override
    public FlexGroup getFlexGroupById(long id) throws ItemNotFoundException {
        return flexGroupDataStore.getById(id);
    }

    private void createFlexFields(Property profileAttribute, FlexGroup flexGroupPersisted) throws Exception {
        if (profileAttribute.constraint().get().dataType().equals("group")) {
            GroupConstraint groupConstraint = (GroupConstraint) profileAttribute.constraint().get();
            FlexGroup flexGroupNestedPersisted = createFlexGroup(null, null,entityMap.get(profileAttribute.scope()), profileAttribute.displayName().get());
            setParent(flexGroupPersisted, flexGroupNestedPersisted);
            for (Property propertyNestedAttribute : groupConstraint.getProperties()) {
                createFlexFields(propertyNestedAttribute, flexGroupNestedPersisted);
            }
        } else {
            FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
            flexFieldDefinition.setName(profileAttribute.name());
            flexFieldDefinition.setDescription(profileAttribute.description().isEmpty() ? null : profileAttribute.description().get());
            flexFieldDefinition.setDisplayName(profileAttribute.displayName().isEmpty() ? null : profileAttribute.displayName().get());

            if (profileAttribute.constraint().get().dataType().equals("string")) {
                StringConstraint stringConstraint = (StringConstraint) profileAttribute.constraint().get();
                flexFieldDefinition.setDataType(FieldType.fromString((String) stringConstraint.dataType()));

                flexFieldDefinition.setRegEx(stringConstraint.regex().isEmpty() ? null : stringConstraint.regex().get());
                flexFieldDefinition.setValidationMessage(stringConstraint.validationMessage().isEmpty() ? null : stringConstraint.validationMessage().get());
                flexFieldDefinition.setDefaultValue(stringConstraint.defaultValue().isEmpty() ? null : stringConstraint.defaultValue().get());
                flexFieldDefinition.setRequired(stringConstraint.required());
                flexFieldDefinition.setRestricted(stringConstraint.restricted());
                if (!stringConstraint.enumerations().isEmpty()) {
                    flexFieldDefinition.setDataType(FieldType.fromString("SELECTONE"));
                    flexFieldDefinition.setSelectOptions(stringConstraint.getEnumerations());
                }
            }
            if (profileAttribute.constraint().get().dataType().equals("long")) {
                LongConstraint longConstraint = (LongConstraint) profileAttribute.constraint().get();
                flexFieldDefinition.setDataType(FieldType.fromString(longConstraint.dataType()));
                flexFieldDefinition.setDefaultValue(longConstraint.defaultValue().isEmpty() ? null : longConstraint.defaultValue().get().toString());
                flexFieldDefinition.setRequired(longConstraint.required());
                flexFieldDefinition.setRestricted(longConstraint.restricted());
                flexFieldDefinition.setValidationMessage(longConstraint.validationMessage().isEmpty() ? null : longConstraint.validationMessage().get());
            }
            if (profileAttribute.constraint().get().dataType().equals("boolean")) {
                BooleanConstraint booleanConstraint = (BooleanConstraint) profileAttribute.constraint().get();
                flexFieldDefinition.setDataType(FieldType.fromString(booleanConstraint.dataType()));
                flexFieldDefinition.setDefaultValue(Boolean.valueOf(booleanConstraint.defaultValue()).toString());
                flexFieldDefinition.setRequired(booleanConstraint.required());
                flexFieldDefinition.setRestricted(booleanConstraint.restricted());
                flexFieldDefinition.setValidationMessage(booleanConstraint.validationMessage().isEmpty() ? null : booleanConstraint.validationMessage().get());

            }

            FlexFieldDefinition flexFieldDefinitionPersisted = setFlexField(flexFieldDefinition);
            addFieldToGroup(flexFieldDefinitionPersisted, flexGroupPersisted);
        }

    }


    private FlexGroup createFlexGroup(String appName, String componentName, String scope, String name) throws Exception {
        FlexGroup flexGroup = new FlexGroup();
        flexGroup.setAppName(appName);
        flexGroup.setTenantName("*");
        flexGroup.setName(name);
        flexGroup.setComponentName(componentName);
        flexGroup.setEntityName(scope);
        flexGroup.setDisplayName(name);
        return setFlexGroup(flexGroup);
    }

    @Override
    public Collection<FlexFieldDefinition> getFlexFieldDefinitions(FlexGroup flexGroup) throws Exception {
        flexGroup = flexGroupDataStore.getById(flexGroup.getId());
        if (flexGroup != null) {
            return flexGroupDataStore.getFields(flexGroup);
        } else throw new ItemNotFoundException(FlexGroup.class, flexGroup);
    }

    @Override
    public List<FlexGroup> getFields(Map<Context, String> contextMap) throws Exception {
        long entityId = 0;

        if (contextMap.containsKey(Context.ENTITY_ID)) {
            entityId = Long.parseLong(contextMap.get(Context.ENTITY_ID));
            contextMap.remove(Context.ENTITY_ID);
        }
        Collection<FlexGroup> flexGroups = flexGroupDataStore.getFlexGroupsByContext(contextMap);

        List<FlexGroup> flexGroupWithChildren = new ArrayList<FlexGroup>();
        for (FlexGroup flexGroup : flexGroups) {
            //entityID required if we want to fetch values Otherwise pass zero.
            FlexGroup flexGroupHierarchy = flexGroupDataStore.getFlexGroupHierarchy(flexGroup, entityId);
            flexGroupWithChildren.add(flexGroupHierarchy);
        }
        return flexGroupWithChildren;
    }


    @Override
    public void setFlexGroup(Collection<FlexGroup> flexGroups) throws Exception {
        for (FlexGroup flexGroup : flexGroups) {
            if (flexGroup.getEntityName() == null) {
                throw new FlexFieldRegistryException("Entity Name is required.");
            }
            if (flexGroup.getId() == null) {
                flexGroup = flexGroupDataStore.createFlexGroup(flexGroup);
            } else {
                flexGroup = flexGroupDataStore.updateFlexGroup(flexGroup);
            }
        }

    }

    @Override
    public FlexGroup addFieldToGroup(FlexFieldDefinition flexFieldDefinition, FlexGroup flexGroup) throws Exception {
        if (flexGroup.getEntityName() == null) {
            throw new FlexFieldRegistryException("Entity Name is required.");
        }

        if (flexFieldDefinition.getDataType() == null) {
            throw new FlexFieldRegistryException("Invalid Data Type for field " + flexFieldDefinition.getName());
        }

        if (flexGroup.getId() == null) {
            flexGroup = flexGroupDataStore.createFlexGroup(flexGroup);
        } else {
            flexGroup = flexGroupDataStore.updateFlexGroup(flexGroup);
        }

        if (flexFieldDefinition.getId() == null) {
            flexFieldDefinition = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);

        } else {
            flexFieldDefinition = flexFieldDefinitionDataStore.getById(flexFieldDefinition.getId());
        }
        flexGroupDataStore.addFlexFieldToGroup(flexGroup, flexFieldDefinition, 1);

        Collection<FlexFieldDefinition> fieldDefinitions = flexGroupDataStore.getFields(flexGroup);
        flexGroup.setFlexFieldsCollection(fieldDefinitions);
        return flexGroup;
    }


    @Override
    public FlexGroup addFieldsToGroup(Collection<FlexFieldDefinition> flexFields, FlexGroup flexGroup) throws Exception {
        if (flexGroup.getEntityName() == null) {
            throw new FlexFieldRegistryException("Entity Name is required.");
        }
        for (FlexFieldDefinition flexFieldDefinition : flexFields) {
            if (flexFieldDefinition.getDataType() == null) {
                throw new FlexFieldRegistryException("Invalid Data Type for field " + flexFieldDefinition.getName());
            }
        }
        if (flexGroup.getId() == null) {
            flexGroup = flexGroupDataStore.createFlexGroup(flexGroup);
        } else {
            flexGroup = flexGroupDataStore.updateFlexGroup(flexGroup);
        }
        for (FlexFieldDefinition flexFieldDefinition : flexFields) {
            if (flexFieldDefinition.getId() == null) {
                flexFieldDefinition = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);

            } else {
                flexFieldDefinition = flexFieldDefinitionDataStore.getById(flexFieldDefinition.getId());
            }
            flexGroupDataStore.addFlexFieldToGroup(flexGroup, flexFieldDefinition, 1);
        }
        Collection<FlexFieldDefinition> fieldDefinitions = flexGroupDataStore.getFields(flexGroup);
        flexGroup.setFlexFieldsCollection(fieldDefinitions);
        return flexGroup;
    }

    @Override
    public FlexFieldDefinition setFlexField(FlexFieldDefinition flexFieldDefinition) throws Exception {
        if (flexFieldDefinition.getId() != null) {
            flexFieldDefinition = flexFieldDefinitionDataStore.updateFlexField(flexFieldDefinition);
        } else {
            flexFieldDefinition = flexFieldDefinitionDataStore.createFlexField(flexFieldDefinition);
        }
        return flexFieldDefinition;
    }

    @Override
    public FlexGroup setFlexGroup(FlexGroup flexGroup) throws Exception {
        if (flexGroup.getId() != null) {
            flexGroup = flexGroupDataStore.updateFlexGroup(flexGroup);
        } else {
            flexGroup = flexGroupDataStore.createFlexGroup(flexGroup);
        }
        return flexGroup;
    }

    @Override
    public void deleteFlexGroup(FlexGroup flexGroup) throws Exception {
        if (flexGroup.getId() == null) {
            throw new FlexFieldRegistryException("Invalid Flex Group ID");
        } else {
            flexGroupDataStore.deleteFlexGroup(flexGroup);
        }
    }

    @Override
    public void deleteFlexFieldDefinition(FlexFieldDefinition flexFieldDefinition) throws Exception {
        if (flexFieldDefinition.getId() == null) {
            throw new FlexFieldRegistryException("Invalid Flex Field ID");
        } else {
            flexFieldDefinitionDataStore.deleteFlexField(flexFieldDefinition);
        }
    }

    @Override
    public FlexGroup setParent(FlexGroup parent, FlexGroup child) throws Exception {
        FlexGroup parentPersisted = setFlexGroup(parent);
        FlexGroup childPersisted = setFlexGroup(child);
        return flexGroupDataStore.setParent(parentPersisted, childPersisted);

    }



}
