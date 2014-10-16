package com.edifecs.epp.security.service;

import com.edifecs.core.configuration.Configuration;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.CertificateAuthenticationToken;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.datastore.ISecurityDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.remote.SecurityManager;
import com.edifecs.epp.security.service.handler.*;
import com.edifecs.epp.security.service.handler.rest.*;
import com.edifecs.epp.security.service.realm.DataStoreRealm;
import com.edifecs.epp.security.service.realm.SecurityLdapRealm;
import com.edifecs.epp.security.service.util.JsonObjectLoader;
import com.edifecs.epp.security.service.util.SecurityJsonHelper;
import com.edifecs.epp.security.service.util.SecurityJsonHelper.RolesWithPermissions;
import com.edifecs.epp.security.service.util.SecurityJsonHelper.SecurityJsonPermission;
import com.edifecs.epp.security.utils.JKSKeyStoreManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.NoResultException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class SecurityServiceCore {
    private static final String DEFAULT_USERS_DAT_FILE = "/default-users.dat";
    private final SecurityContext sc = new SecurityContext();
    // Handlers are package-scope so they can be accessed by SecurityService's getter methods.
    final AdministrativeDataCommandHandler administrativeDataHandler =
            new AdministrativeDataCommandHandler(sc);
    final AuthenticationManager authenticationHandler =
            new AuthenticationManager(sc);
    final AuthorizationManager authorizationHandler =
            new AuthorizationManager(sc);
    final SessionManager sessionsHandler =
            new SessionManager(sc);
    final SubjectManager subjectsHandler =
            new SubjectManager(sc);
    final OrganizationHandler organizationsHandler =
            new OrganizationHandler(sc);
    final PermissionHandler permissionsHandler =
            new PermissionHandler(sc);
    final RoleHandler rolesHandler =
            new RoleHandler(sc);
    final SiteHandler sitesHandler =
            new SiteHandler(sc);
    final TenantHandler tenantsHandler =
            new TenantHandler(sc);
    final UserGroupHandler groupsHandler =
            new UserGroupHandler(sc);
    final UserHandler usersHandler =
            new UserHandler(sc);
    private Logger logger = LoggerFactory.getLogger(getClass());
    private CommandCommunicator commandCommunicator;
    private File ldapConf;
    private Properties properties;
    private User system = null;

    private Logger getLogger() {
        return logger;
    }

    public void doStart(Properties properties, Properties resources,
                        CommandCommunicator commandCommunicator, Address address,
                        InputStream keystoreStream) throws Exception {
        this.commandCommunicator = commandCommunicator;
        this.properties = properties;
        sc.initDataStore(initializeDataStore(resources));
        sc.initManager(configureDefaultBackend(properties));
        JKSKeyStoreManager keyStore = new JKSKeyStoreManager(keystoreStream);

        tenantsHandler.setDefPasswordPolicy(getDefaultPasswordPolicy());

        // Create default user accounts
        createDefaultAccounts(keyStore);

        getLogger().debug("esm-service started.");
    }

    public void doStop() throws Exception {
        sc.dataStore().disconnect();
        getLogger().debug("esm-service stopped.");
    }

    private PasswordPolicy getDefaultPasswordPolicy() {
        return PasswordPolicy.fromProperties(properties);
    }

    private final SecurityManager configureSecurityBackend(
            Collection<? extends Realm> realms,
            CommandCommunicator commandCommunicator) {
        final DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealms(new ArrayList<Realm>(realms));
        return new SecurityManager(securityManager, commandCommunicator);
    }

    /*
     * load security files, default roles and permissions, LDAP cong etc.
     */
    private void setup(Tenant tenant) throws SecurityDataException {
        List<File> files = Configuration
                .getSecurityRolesAndPermissionsFiles(SystemVariables.SERVICE_MANAGER_ROOT_PATH);
        for (File f : files) {
            if (f.getName().contains("security")
                    && f.getName().endsWith("json")
                    || f.getName().endsWith(".security-defaults")
                    || f.getName().endsWith(".agent-defaults")
                    || f.getName().endsWith(".node-defaults")) {
                getLogger().debug(
                        "Found security roles and permissions file : {}",
                        f.getAbsolutePath());
                createAppRolesAndPermissions(tenant.getId(), f, true);
                getLogger().debug(
                        "Created default roles and permissions from : {}",
                        f.getName());

            } else if (f.getName().contains("security-ldap")
                    && f.getName().endsWith("properties")) {
                getLogger().debug("LDAP configuration found.");
                ldapConf = f;
            }
        }
    }

    protected ISecurityDataStore initializeDataStore(Properties properties)
            throws SecurityManagerException {
        return new DatabaseDataStore(properties);
    }

    private SecurityManager configureDefaultBackend(Properties properties) throws Exception {
        final List<Realm> realms = new ArrayList<Realm>();

        // Realm Configuration
        final DataStoreRealm dataStoreRealm = new DataStoreRealm(sc.dataStore(),
                new MemoryConstrainedCacheManager());
        realms.add(dataStoreRealm);

        getLogger().debug("DataStore Realm configured.");

        if (null != ldapConf) {
            realms.addAll(configureLdapRealms());
            getLogger().debug("Ldap Realm configured");
        }

        getLogger().debug("# Realms : {}", realms.size());
        // End Realm Configuration
        // ------------------------------------------------
        // conf realm manager
        RealmManager.setDefaultDataStoreRealm(dataStoreRealm);
        RealmManager.setDataStore(sc.dataStore());
        SecurityManager edifecsSecurityManager = configureSecurityBackend(realms, commandCommunicator);
        RealmManager.setSecurityManager(edifecsSecurityManager.getSecurityManager());
        return edifecsSecurityManager;
    }

    private List<SecurityLdapRealm> configureLdapRealms() throws Exception {

        List<SecurityLdapRealm> realms = new ArrayList<>();

        // configure using properties file
        Properties ldapProperties = new Properties();
        try {
            ldapProperties.load(new FileInputStream(ldapConf));
        } catch (IOException e) {
            getLogger().error("error accessing ldap properties file : {}",
                    ldapConf.getAbsolutePath(), e);
        }
        SecurityLdapRealm realm = new SecurityLdapRealm(sc.dataStore(), ldapProperties);
        realm.setCacheManager(new MemoryConstrainedCacheManager());
        realm.setCachingEnabled(false);
        realms.add(realm);
        addRealmToDefaultOrg(ldapProperties);

        return realms;
    }

    private void addRealmToDefaultOrg(Properties ldapProperties) {
        // add ldap realm to default org
        SecurityRealm realm = new SecurityRealm();
        realm.setName(ldapProperties.getProperty("name"));

        Properties p = new Properties();
        for (Entry<Object, Object> prop : ldapProperties.entrySet()) {
            p.put(prop.getKey().toString(), prop.getValue().toString());
        }
        realm.setRealmType(RealmType.LDAP);
        realm.setEnabled(true);
        realm.setProperties(CustomProperty.fromProperties(p));
        try {
            Organization myOrg = sc.dataStore().getOrganizationDataStore().getOrganizationByName(SystemVariables.DEFAULT_ORG_NAME);
            if (null != myOrg) {
                if (myOrg.getSecurityRealms().size() == 0) {
                    sc.dataStore().getOrganizationDataStore()
                            .addRealmToOrganization(myOrg.getId(), realm);
                    getLogger().info("added ldap realm {}, to organization {}",
                            ldapProperties.getProperty("name"), SystemVariables.DEFAULT_ORG_NAME);
                } else {
                    getLogger().debug("The default ldap realm {}, for organization {} already exists, ignoring...",
                            ldapProperties.getProperty("name"), SystemVariables.DEFAULT_ORG_NAME);
                }
            }
        } catch (Exception e) {
            getLogger().error("error, adding realm to organization", e);
        }
    }

    private Tenant createDefaultTenant(Site site) {
        Tenant tenant = new Tenant();
        tenant.setCanonicalName(SystemVariables.DEFAULT_TENANT_NAME);
        tenant.setDescription("Default Edifecs Tenant");
        tenant.setDomain(SystemVariables.DEFAULT_TENANT_NAME);
        tenant.setPasswordPolicy(getDefaultPasswordPolicy());
        try {
            tenant = sc.dataStore().getTenantDataStore().create(site.getId(), tenant, system);
        } catch (Exception e) {
            getLogger().error("error creating default tenant.", e);
        }
        return tenant;
    }

    protected void createDefaultAccounts(JKSKeyStoreManager keyStore)
            throws ItemAlreadyExistsException, SecurityDataException,
            IOException, ItemNotFoundException {
        try {
            CertificateAuthenticationToken systemToken = new CertificateAuthenticationToken(
                    SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                    keyStore.getRSAEncodedKey("security-system"),SystemVariables.DEFAULT_SYSTEM_USER);

            try {
                system = sc.dataStore().getUserDataStore().getUserByUsername(
                        SystemVariables.DEFAULT_TENANT_NAME,
                        systemToken.getUsername());
                sc.setSystemUser(system);
            } catch (Exception e) {
                getLogger().debug("Empty database found, creating base system accounts.", e);

                system = createSystemAccount(systemToken);
                sc.setSystemUser(system);
                Site site = createDefaultSite();
                Tenant tenant = createDefaultTenant(site);
                Organization org = createDefaultOrganization(tenant);
                sc.dataStore().getUserDataStore().addOrganizationToUser(org, system);

                Permission permission = createDefaultPermissions();
                Role systemRole = createSystemRole(tenant, permission);
                Role adminRole = createAdminRole(tenant, permission);

                sc.dataStore().getRoleDataStore().addRoleToUser(system, systemRole);
                createAdminAccount(adminRole, org);
                setup(tenant);

                logger.info("Edifecs Site setup completed.");
            }
        } catch (InvalidKeyException | KeyStoreException
                | NoSuchAlgorithmException | NoSuchPaddingException
                | UnrecoverableEntryException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new SecurityDataException(e);
        }
    }

    private Site createDefaultSite() {
        Site site = new Site();
        site.setCanonicalName(SystemVariables.DEFAULT_SITE_NAME);
        site.setDescription("Default Edifecs Site");

        try {
            site = sc.dataStore().getSiteDataStore().create(site, system);
        } catch (Exception e) {
            getLogger().error("error creating default site.", e);
        }
        return site;
    }

    private Organization createDefaultOrganization(Tenant tenant) {
        Organization org = new Organization();
        org.setCanonicalName(SystemVariables.DEFAULT_ORG_NAME);
        org.setDescription("Default Organization");

        try {
            org = sc.dataStore().getOrganizationDataStore().create(tenant.getId(), org, system);
        } catch (Exception e) {
            getLogger().error("error creating default organization.", e);
        }
        return org;
    }

    private User createSystemAccount(CertificateAuthenticationToken token) throws SecurityDataException {
        User user = null;
        try {

            user = new User();
            user.setActive(true);

            Contact contact = new Contact();
            contact.setFirstName("System");
            contact.setLastName("Account");
            user.setContact(contact);

            user = sc.dataStore().getUserDataStore().create(user, null);
            sc.dataStore().getUserDataStore().addAuthenticationTokenToUser(user, token);
            user.setUsername(SystemVariables.DEFAULT_SYSTEM_USER);
        } catch(Exception e) {
            // FIXME check for user exiss exception
            getLogger().debug("System account already exists");
        }
        return user;
    }

    private User createAdminAccount(Role adminRole,
                                    Organization org) throws SecurityDataException {
        User user = null;
        try {

            user = new User();
            user.setActive(true);

            Contact contact = new Contact();
            contact.setFirstName("Admin");
            contact.setLastName("Admin");
            user.setContact(contact);

            user = sc.dataStore().getUserDataStore().create(
                    org.getId(),
                    user,
                    new UsernamePasswordAuthenticationToken(
                            SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME, "admin",
                            "admin"), system
            );
            sc.dataStore().getRoleDataStore().addRoleToUser(user, adminRole);

            user.setUsername("admin");
        } catch (ItemAlreadyExistsException e) {
            getLogger().debug("Admin account already exists", e);
        }
        return user;
    }

    private Permission createDefaultPermissions() throws SecurityDataException,
            ItemNotFoundException {
        try {
            Permission permission = new Permission();
            permission.setCanonicalName("*");
            permission.setProductCanonicalName("*");
            permission.setCategoryCanonicalName("*");
            permission.setTypeCanonicalName("*");
            permission.setSubTypeCanonicalName("*");

            return sc.dataStore().getPermissionDataStore().create(permission, system);
        } catch (ItemAlreadyExistsException e) {
            getLogger().debug("Default Permission already exists");
            return sc.dataStore().getPermissionDataStore().getById(1L);
        }
    }

    private Role createSystemRole(Tenant tenant, Permission permission)
            throws ItemAlreadyExistsException, SecurityDataException {
        Role role = new Role();
        role.setCanonicalName("System Role");
        role.setReadOnly(true);

        role = sc.dataStore().getRoleDataStore().create(tenant.getId(), role, system);

        sc.dataStore().getPermissionDataStore()
                .addPermissionToRole(role, permission);

        return role;
    }

    private Role createAdminRole(Tenant tenant, Permission permission)
            throws ItemAlreadyExistsException, SecurityDataException {
        Role role = new Role();
        role.setCanonicalName("Admin Role");
        role.setReadOnly(true);
        role = sc.dataStore().getRoleDataStore().create(tenant.getId(), role, system);

        sc.dataStore().getPermissionDataStore()
                .addPermissionToRole(role, permission);
        return role;
    }

    public void createAppRolesAndPermissions(Long tenantId, File securityJson, boolean readOnly) {
        try {
            createAppRolesAndPermissions(tenantId, new FileInputStream(securityJson), securityJson.getAbsolutePath(), readOnly);
        } catch (FileNotFoundException e) {
            logger.error("No Security.json file found at '" + securityJson.getAbsolutePath() + "'");
        }
    }

        /**
         * Loads a json file with a definition of permissions and roles into the
         * Security DB. If the permission or role already exists, it will update it,
         * if not it will create it. The Method will never delete an already creatd
         * role or permission and is safe to be executed evertime at startup.<br/>
         * <br/>
         * Sample File Format:<br/>
         * <br/>
         * <p/>
         * <pre>
         * {
         *     "permissions": [
         *         {
         *              "permission" : "testProduct1:testCategory1:testTypeName1:testSubTypeName1:testName1",
         *              "name" : "Test Product Permission",
         *              "description" : "This is a sample permission that can be used in an application somewhere",
         *              "id" : "testPermission1"
         *         },
         *         {
         *             "permission" : "testProduct2:testCategory2:testTypeName2:testSubTypeName2:testName2",
         *             "name" : "Test Product Permission 2",
         *             "description" : "This is a sample permission 2 that can be used in an application somewhere",
         *             "id" : "testPermission2"
         *         },
         *         {
         *             "permission" : "testProduct3:testCategory3:testTypeName3:testSubTypeName3:testName3",
         *             "name" : "Test Product Permission 3",
         *             "description" : "This is a sample permission 3 that can be used in an application somewhere",
         *             "id" : "testPermission3"
         *         }
         *     ],
         *
         *     "roles" : [
         *         {
         *             "name": "This is my Role",
         *             "description" : "This is my Description",
         *             "permissions": ["testPermission1"]
         *         },
         *         {
         *             "name": "This is my Role 2",
         *             "description" : "This is my Description 2",
         *             "permissions": ["testPermission1","testPermission2"]
         *         }
         *     ]
         * }
         * </pre>
         *
         * @param securityJson File reference to the security.json file.
         * @param readOnly     if this is set to true, the user will be unable to edit the
         *                     roles.
         */
    //FIXME: This needs to be reviewed as the Role block should just reference the permission String not an id
    public void createAppRolesAndPermissions(Long tenantId, InputStream securityJson, String path, boolean readOnly) {
        try {
            SecurityJsonHelper securityJsonHelper = JsonObjectLoader.load(securityJson, SecurityJsonHelper.class);

            if (securityJsonHelper == null) {
                logger.error("Invalid Security.json file found at '" + path + "'");
            } else {
                List<SecurityJsonPermission> persistedPermissions = new ArrayList<>();
                for (SecurityJsonPermission p : securityJsonHelper.getPermissions()) {
                    Permission pData = securityJsonHelper.getPermissionDataObj(p);
                    try {
                        pData = sc.dataStore().getPermissionDataStore().create(pData, system);
                        p.setEntityId(pData.getId());
                        persistedPermissions.add(p);

                        getLogger().debug("created permission : {}", pData.toString());
                    // FIXME: The exception handling is busted. This is a hack, Fix please.
                    } catch (ItemAlreadyExistsException e) {
                        getLogger().debug("Permission : {}, already exists, ignoring.", pData.toString());
                    }
                }

                securityJsonHelper.setPermissions(persistedPermissions);

                for (RolesWithPermissions r : securityJsonHelper.getRoles()) {

                    Role role = new Role();
                    role.setCanonicalName(r.getName());
                    role.setDescription(r.getDescription());
                    role.setReadOnly(readOnly);

                    try {
                        sc.dataStore().getRoleDataStore().getRoleByRoleName(role.getCanonicalName());
                        getLogger().debug("Role : {}, already exists, ignoring.", role.getCanonicalName());
                    } catch (NoResultException nre) {
                        try {
                            role = sc.dataStore().getRoleDataStore().create(tenantId, role, system);
                            getLogger().debug("created role : {}", role.getCanonicalName());

                            for (String refId : r.getPermissions()) {
                                Permission p = securityJsonHelper.getJsonPermissionByRefId(refId);
                                if (p != null) {
                                    sc.dataStore().getPermissionDataStore().addPermissionToRole(role, p);
                                    getLogger().debug("added permission : {}, to role : {}",
                                            p.getCanonicalName(),
                                            role.getCanonicalName());
                                } else {
                                    try {
                                        p = securityJsonHelper.getPermissionFromString(refId);
                                        Permission permission;
                                        try {
                                            permission = sc.dataStore().getPermissionDataStore().getPermission(p);
                                        } catch (NoResultException e) {
                                            p = sc.dataStore().getPermissionDataStore().create(p, system);
                                            permission = p;
                                        }
                                        sc.dataStore().getPermissionDataStore().addPermissionToRole(role, permission);
                                    } catch (SecurityException e) {
                                        throw new SecurityException("The permission is in the incorrect format, or the referenced permission is missing.", e);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            getLogger().error("Error creating role : {} : reason : {}",
                                    role.getCanonicalName(),
                                    e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (SecurityDataException e) {
            getLogger().error("security exception", e);
        }
    }
}
