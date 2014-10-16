package com.edifecs.epp.flexfields.api;

import com.edifecs.epp.flexfields.model.FieldType;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;

/**
 * Created by sandeep.kath on 5/12/2014.
 */
public class FlexFieldRegistryTestBase {
    protected FlexFieldDefinition createFlexFieldDefinition() {
        int random = 1 + (int) (Math.random() * (10000 - 1) + 1);
        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
        flexFieldDefinition.setName("Provider" + random);
        flexFieldDefinition.setDescription("Provider flex field required to be captured at forms");
        flexFieldDefinition.setDefaultValue("Overlake Hospital");
        flexFieldDefinition.setDisplayName("Provider Name" + random);
        flexFieldDefinition.setRegEx("((mailto\\:|(news|(ht|f)tp(s?))\\://){1}\\S+)");
        flexFieldDefinition.setValidationMessage("Please enter the proper name");
        flexFieldDefinition.setActiveFlag(true);
        flexFieldDefinition.setDataType(FieldType.STRING);

        return flexFieldDefinition;
    }

    protected FlexFieldDefinition createFlexFieldDefinition(int id) {
        int random = 1 + (int) (Math.random() * (10000 - 1) + 1);
        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
        flexFieldDefinition.setName("Provider" + id);
        flexFieldDefinition.setDescription("Provider flex field required to be captured at forms");
        flexFieldDefinition.setDefaultValue("Overlake Hospital");
        flexFieldDefinition.setDisplayName("Provider Name" + random);
        flexFieldDefinition.setRegEx("((mailto\\:|(news|(ht|f)tp(s?))\\://){1}\\S+)");
        flexFieldDefinition.setValidationMessage("Please enter the proper name");
        flexFieldDefinition.setActiveFlag(true);
        flexFieldDefinition.setDataType(FieldType.STRING);

        return flexFieldDefinition;
    }

    protected FlexGroup createFlexGroup() {
        int random = 1 + (int) (Math.random() * (10000 - 1) + 1);
        FlexGroup flexGroup = new FlexGroup();
        flexGroup.setAppName("CM");
        flexGroup.setComponentName("ICD10");
        flexGroup.setDescription("CMS Link " + random);
        flexGroup.setEntityName("CMSData");
        flexGroup.setName("CMSLink" + random);
        flexGroup.setDisplayName("CMS Link");
        flexGroup.setTenantName("Tenant " + random);
        return flexGroup;
    }
    protected FlexFieldValue createFlexFieldValue(FlexGroup flexGroup, FlexFieldDefinition flexFieldDefinition, String value) {
        long random = 1 + (int) (Math.random() * (10000 - 1) + 1);
        FlexFieldValue flexFieldValue = new FlexFieldValue();
        flexFieldValue.setEntityName("User");
        flexFieldValue.setEntityID(random);
        flexFieldValue.setFlexGroupId(flexGroup.getId());
        flexFieldValue.setFlexFieldDefinitionId(flexFieldDefinition.getId());
        flexFieldValue.setValue(value);
        return flexFieldValue;

    }
}
