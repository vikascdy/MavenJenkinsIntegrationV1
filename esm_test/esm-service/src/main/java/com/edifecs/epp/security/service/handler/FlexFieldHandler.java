package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.IAuthorizationManager;
import com.edifecs.epp.security.ISubjectManager;
import com.edifecs.epp.security.exception.AuthorizationFailureException;
import com.edifecs.epp.security.flexfields.IFlexFieldHandler;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sandeep.kath on 7/18/2014.
 */
public class FlexFieldHandler extends AbstractCommandHandler implements IFlexFieldHandler {
    public static final String FLEX_FIELD_SERVICE = "flexfields-service";

    //TODO: Need review and it is the test code
    @Override
    public Collection<FlexGroup> getFields(@Arg(name = "contextMap", required = false) HashMap<String, String> map) throws Exception {
        String entityName = map.get("entityName");
        String entityId = map.get("entityId");
        String tenantName = map.get("tenantName");
        // check for null value
        if (tenantName == null || entityId == null || entityName == null) {
            //exception
            throw new IllegalArgumentException("Invalid Entity Name");
        }

        ISubjectManager subjectManager = getCommandCommunicator().getSecurityManager().getSubjectManager();

        switch (entityName) {
            case "UserEntity":
                if (!entityId.equals(subjectManager.getUser().getId().toString())) {
                    //TODO : can user see other users fields? how to check if user is admin or system user?
                    throw new AuthorizationFailureException();
                }
                return getFlexFields(map);
            case "TenantEntity":
                if (!entityId.equals(subjectManager.getTenant().getId().toString())) {
                    throw new AuthorizationFailureException();
                }
                return getFlexFields(map);
            case "OrganizationEntity":
                if (!entityId.equals(subjectManager.getOrganization().getId().toString())) {
                    throw new AuthorizationFailureException();
                }
                return getFlexFields(map);
            case "SiteEntity":
                if (!entityId.equals(subjectManager.getSite().getId().toString())) {
                    throw new AuthorizationFailureException();
                }
                return getFlexFields(map);
        }
        return null;
    }


    @Override
    public FlexFieldValue setFlexFieldValue(@Arg(name = "flexFieldValue", required = true) FlexFieldValue flexFieldValue) throws Exception {

        ISubjectManager subjectManager = getCommandCommunicator().getSecurityManager().getSubjectManager();
        IAuthorizationManager authorizationManager = getCommandCommunicator().getSecurityManager().getAuthorizationManager();
        switch (flexFieldValue.getEntityName()) {
            case "UserEntity":
                if (!authorizationManager.isPermitted("platform.security.administrative.user.edit") && flexFieldValue.getEntityID() != subjectManager.getUser().getId()) {
                    throw new AuthorizationFailureException();
                }
                return setFieldValue(flexFieldValue);
            case "TenantEntity":
                if (!authorizationManager.isPermitted("platform.security.administrative.tenant.edit") && flexFieldValue.getEntityID() != subjectManager.getTenant().getId()) {
                    throw new AuthorizationFailureException();
                }
                return setFieldValue(flexFieldValue);
            case "OrganizationEntity":
                if (!authorizationManager.isPermitted("platform.security.administrative.organization.edit") && flexFieldValue.getEntityID() != subjectManager.getOrganization().getId()) {
                    throw new AuthorizationFailureException();
                }
                return setFieldValue(flexFieldValue);

            case "SiteEntity":
                if (!authorizationManager.isPermitted("platform.security.administrative.site.edit") && flexFieldValue.getEntityID() != subjectManager.getSite().getId()) {
                    throw new AuthorizationFailureException();
                }
                return setFieldValue(flexFieldValue);

        }
        return null;
    }

    private List<FlexGroup> getFlexFields(HashMap contextMap) throws Exception {
        Map<String, Serializable> args = new HashMap();
        args.put("contextMap", contextMap);
        Address serviceAddress = getCommandCommunicator().getAddressRegistry().getAddressForServiceTypeName(FLEX_FIELD_SERVICE);
        Object response = getCommandCommunicator().sendSyncMessage(serviceAddress, "FlexField.getFields", args);
        return (List<FlexGroup>) response;
    }

    private FlexFieldValue setFieldValue(FlexFieldValue flexFieldValue) throws Exception {
        Map<String, Serializable> args = new HashMap();
        args.put("flexFieldValue", flexFieldValue);
        Address serviceAddress = getCommandCommunicator().getAddressRegistry().getAddressForServiceTypeName(FLEX_FIELD_SERVICE);
        Object response = getCommandCommunicator().sendSyncMessage(serviceAddress, "FlexField.setFlexFieldValue", args);
        return (FlexFieldValue) response;
    }
}
