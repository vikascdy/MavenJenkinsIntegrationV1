// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

package com.edifecs.epp.security.service;

import com.edifecs.epp.security.apps.handler.IAppStoreHandler;
import com.edifecs.epp.security.IAuthenticationManager;
import com.edifecs.epp.security.IAuthorizationManager;
import com.edifecs.epp.security.ISessionManager;
import com.edifecs.epp.security.ISubjectManager;
import com.edifecs.epp.security.data.PasswordPolicy;
import com.edifecs.epp.security.flexfields.IFlexFieldHandler;
import com.edifecs.epp.security.handler.*;
import com.edifecs.epp.security.handler.rest.*;
import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Property;
import com.edifecs.servicemanager.annotations.Property.PropertyType;
import com.edifecs.servicemanager.annotations.Resource;
import com.edifecs.servicemanager.annotations.Service;

@Service(
        name = "esm-service",
        version = "1.0",
        description = "esm-service",
        properties = {
                @Property(
                        name = PasswordPolicy.PASSWD_MAX_ATTEMPTS,
                        propertyType = PropertyType.STRING,
                        description = "Specifies how many unsuccessful logon attempts should happen before an" +
                                " account is locked.The value 0 means that the account is never going to be locked, i.e." +
                                " locking out mechanism is disabled.",
                        defaultValue = "0",
                        required = true),
                @Property(
                        name = PasswordPolicy.PASSWD_RESET_LOCKOUT_INTERVL,
                        propertyType = PropertyType.STRING,
                        description = "Specifies the interval in minutes that needs to pass since the last logon" +
                                " attempt (successful or unsuccessful) to automatically reset the number of unsuccessful" +
                                " logon attempts to 0.",
                        defaultValue = "5",
                        required = true),
                @Property(
                        name = PasswordPolicy.PASSWD_HISTORY,
                        propertyType = PropertyType.STRING,
                        description = "Passwords to remember",
                        defaultValue = "3",
                        required = true),
                @Property(
                        name = PasswordPolicy.PASSWD_AGE,
                        propertyType = PropertyType.STRING,
                        description = "Password validity in (Days)",
                        defaultValue = "120",
                        required = true),
                @Property(
                        name = PasswordPolicy.PASSWD_RESET_LOGIN,
                        propertyType = PropertyType.BOOLEAN,
                        description = " User must change password on first login",
                        defaultValue = "false",
                        required = true),
                @Property(
                        name = PasswordPolicy.PASSWD_REGEX,
                        propertyType = PropertyType.STRING,
                        description = " Password must meeting complexity requirements done through a Regular" +
                                " expression and Error description field",
                        defaultValue = "",
                        required = true),
                @Property(
                        name = PasswordPolicy.PASSWD_REGEX_DESC,
                        propertyType = PropertyType.STRING,
                        description = "Error description field",
                        defaultValue = "",
                        required = true),
                @Property(
                        name = PasswordPolicy.PASSWD_LOCKOUT_DURATION,
                        propertyType = PropertyType.STRING,
                        description = "Specifies how long the account is going to be locked out after a certain" +
                                " amount of unsuccessful logon attempts. The enterred value specifies the interval in" +
                                " minutes. A special value of 0 means that the account is locked out until the Site /" +
                                " CommunityGateway Admin explicitly unlocks the account.",
                        defaultValue = "10",
                        required = true)},
        resources = {
                @Resource(
                        type = "JDBC Database",
                        name = "Database Server",
                        unique = false)})
public interface ISecurityService {

    @Handler
    IAdministrativeDataCommandHandler administrativeData();

    @Handler
    IAuthenticationManager authentication();

    @Handler
    IAuthorizationManager authorization();

    @Handler
    ISessionManager sessions();

    @Handler
    ISubjectManager subjects();

    @Handler
    IOrganizationHandler organizations();

    @Handler
    IPermissionHandler permissions();

    @Handler
    IRoleHandler roles();

    @Handler
    ISiteCommandHandler sites();

    @Handler
    ITenantHandler tenants();

    @Handler
    IUserGroupHandler groups();

    @Handler
    IUserHandler users();

    @Handler
    IAppStoreHandler appstore();

    @Handler
    IFlexFieldHandler flexfields();
}

