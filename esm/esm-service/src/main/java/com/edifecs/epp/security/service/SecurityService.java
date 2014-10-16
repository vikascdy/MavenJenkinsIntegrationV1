package com.edifecs.epp.security.service;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.apps.handler.IAppStoreHandler;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.security.data.PasswordPolicy;
import com.edifecs.epp.security.flexfields.IFlexFieldHandler;
import com.edifecs.epp.security.service.handler.*;
import com.edifecs.epp.security.service.handler.rest.*;
import com.edifecs.servicemanager.api.AbstractService;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SecurityService extends AbstractService implements ISecurityService {

    SecurityServiceCore core = new SecurityServiceCore();
    private InputStream overrideCert = null;

    @Override
    public void start() throws Exception {
        core.doStart(
                getProperties(),
                getResources().get("Security Database"),
                (CommandCommunicator) getCommandCommunicator(),
                getAddress(),
                overrideCert == null ?
                        new FileInputStream(SystemVariables.SECURITY_CERTIFICATE_FILE) :
                        overrideCert
        );
    }

    @Override
    public Properties getTestProperties() throws Exception {
        Properties properties = new Properties();
        properties.put(PasswordPolicy.PASSWD_MAX_ATTEMPTS, "0");
        properties.put(PasswordPolicy.PASSWD_RESET_LOCKOUT_INTERVL, "5");
        properties.put(PasswordPolicy.PASSWD_HISTORY, "3");
        properties.put(PasswordPolicy.PASSWD_AGE, "120");
        properties.put(PasswordPolicy.PASSWD_RESET_LOGIN, "false");
        properties.put(PasswordPolicy.PASSWD_REGEX, "");
        properties.put(PasswordPolicy.PASSWD_REGEX_DESC, "");
        properties.put(PasswordPolicy.PASSWD_LOCKOUT_DURATION, "10");

        // Test Certificate
        overrideCert = this.getClass().getResourceAsStream("/keystore.jks");

        return properties;
    }

    @Override
    public Map<String, Properties> getTestResources() {
        Properties resourceProperties = new Properties();
        resourceProperties.put("Username", "sa");
        resourceProperties.put("Password", "");
        resourceProperties.put("Driver", "org.h2.Driver");
        resourceProperties.put("URL", "jdbc:h2:mem:TestDatabase");
        resourceProperties.put("Dialect", "org.hibernate.dialect.H2Dialect");
        resourceProperties.put("AutoCreate", true);

        Map<String, Properties> map = new HashMap<>();
        map.put("Security Database", resourceProperties);

        return map;
    }

    @Override
    public void stop() throws Exception {
        overrideCert = null;
        core.doStop();
    }

    @Override
    public AdministrativeDataCommandHandler administrativeData() {
        return core.administrativeDataHandler;
    }

    @Override
    public AuthenticationManager authentication() {
        return core.authenticationHandler;
    }

    @Override
    public AuthorizationManager authorization() {
        return core.authorizationHandler;
    }

    @Override
    public SessionManager sessions() {
        return core.sessionsHandler;
    }

    @Override
    public SubjectManager subjects() {
        return core.subjectsHandler;
    }

    @Override
    public OrganizationHandler organizations() {
        return core.organizationsHandler;
    }

    @Override
    public PermissionHandler permissions() {
        return core.permissionsHandler;
    }

    @Override
    public RoleHandler roles() {
        return core.rolesHandler;
    }

    @Override
    public SiteHandler sites() {
        return core.sitesHandler;
    }

    @Override
    public TenantHandler tenants() {
        return core.tenantsHandler;
    }

    @Override
    public UserGroupHandler groups() {
        return core.groupsHandler;
    }

    @Override
    public UserHandler users() {
        return core.usersHandler;
    }

    @Override
    public IFlexFieldHandler flexfields() {
        return new FlexFieldHandler();
    }

    @Override
    public IAppStoreHandler appstore() {
        return new AppStoreHandler();
    }


}
