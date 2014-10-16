// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.epp.security.service.realm;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.LdapAuthenticationToken;
import com.edifecs.epp.security.datastore.ISecurityDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.service.SecurityContext;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.*;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.persistence.NoResultException;
import java.util.*;

/**
 * The Class SecurityLdapRealm, extends JndiLdapRealm. Overrides User
 * Authentication and Authorization, adding the ability to add LDAP users to the
 * security database, and define roles and permissions for the LDAP users.
 *
 * @author abhising
 */
public class SecurityLdapRealm extends JndiLdapRealm {
    public final static String USER_ORGANIZATION = "user.organization";
    public final static String USER_TENANT = "user.tenant";
    protected final static String URL = "URL";
    protected final static String SYSTEM_USER = "System User";
    protected final static String SYSTEM_PASS = "System Password";
    protected final static String USER_FILTER = "User Filter";
    protected final static String GROUP_FILTER = "Group Filter";
    protected final static String GROUP_SEARCH_BASE = "Group Search Base";
    protected final static String USER_SEARCH_BASE = "User Search Base";
    protected final static String ATT_FNAME = "First Name Attribute";
    protected final static String ATT_LNAME = "Last Name Attribute";
    protected final static String ATT_EMAIL = "Mail Attribute";
    protected final static String USER_DN = "User DN";
    private static Logger logger = LoggerFactory
            .getLogger(SecurityLdapRealm.class);
    protected String groupFilter = "(&(objectClass=*)(member={0}))";
    protected String userFilter = "(&(objectClass=*)(uid={0}))";
    protected String userSearchBase = "ou=users,ou=system";
    protected String groupSearchBase = "ou=groups,ou=system";
    protected String url;
    protected String systemUser;
    protected String systemPasswd;
    protected Organization organization;
    // private String att_username;
    protected String att_fName = "givenName";
    protected String att_lName = "sn";
    protected String att_email = "mail";
    private ISecurityDataStore dataStore;
    private boolean anonymous = false;
    private String DN_REPLACE_TOKEN = "{0}";
    private boolean test = false;

    public SecurityLdapRealm(ISecurityDataStore dataStore,
                             Properties properties, RealmType realmType) throws Exception {
        super();
        initialize(dataStore, properties, realmType);
    }

    public SecurityLdapRealm(ISecurityDataStore dataStore, Properties properties)
            throws Exception {
        super();
        initialize(dataStore, properties, RealmType.LDAP);
    }

    public SecurityLdapRealm(ISecurityDataStore dataStore, SecurityRealm realm)
            throws Exception {
        super();
        initialize(dataStore,
                CustomProperty.parseProperties(realm.getProperties()),
                RealmType.LDAP);
    }

    public static List<RealmConfig> getLdapConfigMeta() {
        List<RealmConfig> ldapConfigs = new ArrayList<>();

        // TODO: Move all Descriptions to the UI code to support easy change in
        // the future.
        ldapConfigs
                .add(new RealmConfig(
                        URL,
                        "URL the LDAP url to connect to. (e.g. ldap://ldapDirectoryHostname:port)",
                        "ldap://my.company.domain:389", true));

        ldapConfigs
                .add(new RealmConfig(
                        SYSTEM_USER,
                        "systemUsername the system username that will be used when creating an LDAP connection used for authorization queries.",
                        "", false));

        ldapConfigs
                .add(new RealmConfig(
                        SYSTEM_PASS,
                        "systemPassword the password of the systemUsername that will be used when creating an LDAP connection used for authorization queries.",
                        "", false));

        ldapConfigs
                .add(new RealmConfig(
                        GROUP_FILTER,
                        "filterExpr the filter expression to use for the search. The expression may contain variables of the form '{i}' where i is a nonnegative integer.",
                        "(&(objectClass=*)(member={0}))", true));

        ldapConfigs
                .add(new RealmConfig(
                        USER_FILTER,
                        "filterExpr the filter expression to use for the search. The expression may contain variables of the form '{i}' where i is a nonnegative integer.",
                        "(&(objectClass=*)(uid={0}))", true));

        ldapConfigs.add(new RealmConfig(USER_SEARCH_BASE,
                "name the name of the context or object to search",
                "ou=users,ou=system", true));

        ldapConfigs.add(new RealmConfig(GROUP_SEARCH_BASE,
                "name the name of the context or object to search",
                "ou=groups,ou=system", true));

        ldapConfigs
                .add(new RealmConfig(
                        USER_DN,
                        "User DN formats are unique to the LDAP directory's schema, and each environment differs - you will need to specify the format corresponding to your directory. You do this by specifying the full User DN as normal, but but you use a {0}} placeholder token in the string representing the location where the user's submitted principal (usually a username or uid) will be substituted at runtime. "
                                + "\n\nFor example, if your directory uses an LDAP uid attribute to represent usernames, the User DN for the jsmith user may look like this:\n\n uid=jsmith,ou=users,dc=mycompany,dc=com"
                                + "in which case you would set this property with the following template value: \n\n uid={0},ou=users,dc=mycompany,dc=com",
                        "uid={0},ou=users,ou=system", false));

        ldapConfigs.add(new RealmConfig(ATT_FNAME,
                "LDAP attribute representing first name", "givenName", false));

        ldapConfigs.add(new RealmConfig(ATT_LNAME,
                "LDAP attribute representing last name", "sn", false));

        ldapConfigs.add(new RealmConfig(ATT_EMAIL,
                "LDAP attribute representing user email", "mail", false));

        return ldapConfigs;
    }

    private void initialize(ISecurityDataStore dataStore,
                            Properties properties, RealmType realmType) throws Exception {
        this.dataStore = dataStore;
        super.setName(realmType.getVal());
        super.setCacheManager(new MemoryConstrainedCacheManager());
        if (properties == null || properties.isEmpty()) {
            throw new SecurityException("No properties found for " + getName()
                    + " configuration");
        }
        initContextFactory(properties);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    protected void initContextFactory(Properties properties) throws Exception {
        JndiLdapContextFactory contextFactory = new JndiLdapContextFactory();
        this.url = properties.getProperty(URL, URL);
        this.systemUser = properties.getProperty(SYSTEM_USER);
        this.systemPasswd = properties.getProperty(SYSTEM_PASS);
        contextFactory.setUrl(url);
        contextFactory.setSystemUsername(systemUser);
        contextFactory.setSystemPassword(systemPasswd);
        setContextFactory(contextFactory);
        String user_dn = properties.getProperty(USER_DN);
        if (null != user_dn)
            setUserDnTemplate(user_dn);
        this.groupFilter = properties.getProperty(GROUP_FILTER);
        this.userFilter = properties.getProperty(USER_FILTER);
        this.att_fName = properties.getProperty(ATT_FNAME, att_fName);
        this.att_lName = properties.getProperty(ATT_LNAME, att_lName);
        this.att_email = properties.getProperty(ATT_EMAIL, att_email);
        this.userSearchBase = properties.getProperty(USER_SEARCH_BASE);
        this.groupSearchBase = properties.getProperty(GROUP_SEARCH_BASE);

        String tenantName = properties.getProperty(USER_TENANT,
                SystemVariables.DEFAULT_TENANT_NAME);
        String orgName = properties.getProperty(USER_ORGANIZATION,
                SystemVariables.DEFAULT_ORG_NAME);

        if (null != dataStore) {
            // datastore is null during test ldap connection
            this.organization = dataStore.getOrganizationDataStore()
                    .getOrganizationByDomainAndOrganizationName(tenantName,
                            orgName);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.shiro.realm.ldap.JndiLdapRealm#queryForAuthenticationInfo(
     * org.apache.shiro.authc.AuthenticationToken,
     * org.apache.shiro.realm.ldap.LdapContextFactory)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        char[] password = ((UsernamePasswordToken) token).getPassword();
        if (password == null || password.length == 0) {
            throw new AuthenticationException("Password is required.");
        }
        if (getUserDnTemplate().isEmpty() || getUserDnTemplate().equals(DN_REPLACE_TOKEN)) {
            try {
                LdapContext tempCtx = getContextFactory().getSystemLdapContext();
                setUserDnTemplate(getUserDN(tempCtx, token.getPrincipal()));
                LdapUtils.closeContext(tempCtx);
            } catch (Exception e) {
                logger.warn("Failed to auto generate UserDn. Either Invalid configuration or Need to explicitly set user dn property", e);
            }
        }
        return super.doGetAuthenticationInfo(token);
    }

    @Override
    protected AuthenticationInfo createAuthenticationInfo(AuthenticationToken token, Object ldapPrincipal, Object ldapCredentials, LdapContext ldapContext) throws NamingException {
        if (!test) {
            User user = createSMUser(ldapContext, token);

            SimplePrincipalCollection principlCollection = new SimplePrincipalCollection();
            principlCollection.add(token.getPrincipal(), getName());
            principlCollection.add(user.getId(),
                    RealmType.DATABASE.getVal());

            return new SimpleAuthenticationInfo(principlCollection,
                    token.getCredentials());
        } else {
            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        return super.doGetAuthorizationInfo(principals);
    }

    public AuthorizationInfo doGetAuthorizationInfo(
            AuthenticationInfo authenInfo) {
        AuthorizationInfo info;
        try {
            info = queryForAuthorizationInfo(authenInfo, getContextFactory());
        } catch (NamingException e) {
            String msg = "LDAP naming error while attempting to retrieve authorization for user ["
                    + authenInfo.getPrincipals() + "].";
            throw new AuthorizationException(msg, e);
        }

        return info;
    }

    protected User createSMUser(LdapContext context, AuthenticationToken token) {
        boolean create;
        User user;
        String username = token.getPrincipal().toString();
        // Check to see if the user already exists.
        try {
            user = dataStore.getUserDataStore().getUserByUsername(
                    organization.getTenant().getDomain(), username);
            create = false;
            user.setLastLoginDateTime(new Date());
        } catch (NoResultException e) {
            create = true;
            // create SM user.
            user = new User();
            user.setActive(true);
            user.setDeleted(false);
            user.setCreatedDateTime(new Date());
            user.setHumanUser(true);
            user.setSuspended(false);
            user.setLastLoginDateTime(new Date());
        }

        // Try to build user contact information if available
        try {
            user.setContact(buildUserContact(context,
                    getUsername(token.getPrincipal())));
        } catch (Exception e) {
            logger.error(
                    "Error building user contact Information, this means there is a configuration issue with the LDAP User Search and Filter Variables.",
                    e);
        }

        try {
            if (create) {

                user = dataStore.getUserDataStore().create(
                        organization.getId(), user, SecurityContext.getSystemUser());
                LdapAuthenticationToken ldapToken = new LdapAuthenticationToken(
                        organization.getTenant().getDomain(),
                        organization.getCanonicalName(), username);
                dataStore.getUserDataStore().addAuthenticationTokenToUser(user,
                        ldapToken);
                logger.debug(
                        "created SM user for ldap user : {}, organization : {}",
                        username, organization.getCanonicalName());
            } else {
                user = dataStore.getUserDataStore().update(user, SecurityContext.getSystemUser());
                logger.debug(
                        "updated SM user for ldap user : {}, organization : {}",
                        username, organization.getCanonicalName());
            }
        } catch (ItemAlreadyExistsException | SecurityDataException
                | ItemNotFoundException e) {
            logger.error("", e);
        }
        return user;
    }

    protected Contact buildUserContact(LdapContext context, String username)
            throws NamingException {
        Contact contact = new Contact();

        String[] attrs = {att_fName, att_lName, att_email};

        logger.debug("searching with attrs " + userSearchBase + userFilter);
        Attributes attributes = getUserAttributes(context, username, attrs);

        Attribute fname = attributes.get(att_fName);
        if (null != fname) {
            contact.setFirstName(fname.get().toString());
        }
        Attribute lname = attributes.get(att_lName);
        if (null != lname) {
            contact.setLastName(lname.get().toString());
        }
        Attribute email = attributes.get(att_email);
        if (null != email) {
            contact.setEmailAddress(email.get().toString());
        }

        return contact;
    }

    public User createLdapUser(final String username) {
        User user = new User();
        user.setActive(true);
        user.setDeleted(false);
        user.setCreatedDateTime(new Date());
        user.setHumanUser(true);
        user.setSuspended(false);
        user.setLastLoginDateTime(new Date());

        LdapContext ldapCtx = null;
        try {
            ldapCtx = getContextFactory().getSystemLdapContext();
        } catch (NamingException e1) {
            // should work, becuse connection is always tested using
            // test connection
            logger.error("get ctx while creating ldap user", e1);
        }

        try {
            user.setContact(buildUserContact(ldapCtx, username));
        } catch (Exception e) {
            throw new SecurityException(
                    "Error building user contact Information, this means there is a configuration issue with the LDAP User Search and Filter Variables.",
                    e);
        }

        return user;
    }

    protected Attributes getUserAttributes(LdapContext context,
                                           Object principle, String[] attrs) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        if (attrs != null) {
            searchControls.setReturningAttributes(attrs);
        }
        String userFilterArg = getUsername(principle);
        Object[] searchArguments = new Object[]{userFilterArg};
        NamingEnumeration<SearchResult> renum = context.search(userSearchBase,
                userFilter, searchArguments, searchControls);
        if (!renum.hasMore()) {
            throw new NamingException(
                    "No user found. There is an issue with the LDAP Configuration.");
        }
        SearchResult result = renum.next();
        return result.getAttributes();
    }

    protected String getUserDN(LdapContext context, Object principle)
            throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String userFilterArg = getUsername(principle);
        Object[] searchArguments = new Object[]{userFilterArg};
        NamingEnumeration<SearchResult> renum = context.search(userSearchBase,
                userFilter, searchArguments, searchControls);
        if (!renum.hasMore()) {
            throw new NamingException(String.format(
                    "No user [%s] found. There is an issue with the LDAP Configuration.", principle));
        }
        SearchResult result = renum.next();
        String dn = result.getNameInNamespace();
        String[] dnTokens = dn.split(",");
        if (dnTokens.length != 0 && dnTokens[0].contains(userFilterArg)) {
            dnTokens[0] = dnTokens[0].replace(userFilterArg, DN_REPLACE_TOKEN);
            dn = StringUtils.join(dnTokens, ",");
        } else {
            dn = DN_REPLACE_TOKEN; // set to default, will fail for AD otherwise.
        }
        return dn;
    }

    protected String getUsername(Object principle) {
        if (principle.toString().split("\\\\").length > 1) {
            return principle.toString().split("\\\\")[1];
        }
        return principle.toString();
    }

    @Override
    protected AuthorizationInfo queryForAuthorizationInfo(
            PrincipalCollection principals,
            LdapContextFactory ldapContextFactory) throws NamingException {
        LdapContext context = null;
        SimpleAuthorizationInfo authorInfo = null;

        final Collection<?> principal = principals.fromRealm(getName());
        if (principal.isEmpty()) {
            return null;
        }
        String username = (String) principals.fromRealm(getName()).toArray()[0];

        try {
            context = ldapContextFactory.getSystemLdapContext();
            authorInfo = (SimpleAuthorizationInfo) buildAuthorizationInfo(
                    context, username);
            // FIXME: Fix this thrown Exception
        } catch (Exception ex) {
            logger.error(
                    "Incorrect LDAP Configuration Options Causing a Fatal LDAP Exception.",
                    ex);
            ConfigurationException exception = new ConfigurationException(
                    "Incorrect LDAP Configuration Options Causing a Fatal LDAP Exception.");
            exception.setRootCause(ex);
            throw exception;
        } finally {
            LdapUtils.closeContext(context);
        }
        return authorInfo;
    }

    protected AuthorizationInfo queryForAuthorizationInfo(
            AuthenticationInfo authenInfo, LdapContextFactory ldapContextFactory)
            throws NamingException {
        final PrincipalCollection principals = authenInfo.getPrincipals();
        final Collection<?> principal = principals.fromRealm(getName());
        SimpleAuthorizationInfo authorInfo = null;
        LdapContext context = null;

        if (principal.isEmpty()) {
            return null;
        }
        String username = (String) principals.fromRealm(getName()).toArray()[0];

        try {
            context = ldapContextFactory.getLdapContext(
                    principals.getPrimaryPrincipal(),
                    authenInfo.getCredentials());
            authorInfo = (SimpleAuthorizationInfo) buildAuthorizationInfo(
                    context, username);
            // FIXME: Fix this thrown Exception
        } catch (Exception ex) {
            logger.error(
                    "Incorrect LDAP Configuration Options Causing a Fatal LDAP Exception.",
                    ex);
            ConfigurationException exception = new ConfigurationException(
                    "Incorrect LDAP Configuration Options Causing a Fatal LDAP Exception.");
            exception.setRootCause(ex);
            throw exception;
        } finally {
            LdapUtils.closeContext(context);
        }

        return authorInfo;
    }

    protected AuthorizationInfo buildAuthorizationInfo(LdapContext context,
                                                       String username) throws NamingException, SecurityDataException {
        SimpleAuthorizationInfo authorInfo = new SimpleAuthorizationInfo();
        authorInfo
                .setObjectPermissions(new HashSet<org.apache.shiro.authz.Permission>());
        authorInfo.setRoles(new HashSet<String>());
        authorInfo.setStringPermissions(new HashSet<String>());

        String userDN = getUserDN(context, getUsername(username));
        List<UserGroup> sMGroups = mapLdapGroupsToSMGroups(getUserGroupMembership(
                context, userDN));

        // Get Transitive Roles and Permissions for the groups
        Map<Class<?>, Collection<?>> collection = dataStore.getRoleDataStore()
                .getTransitiveRolesAndPermissionsForGroups(sMGroups);

        for (Object o : collection.get(Permission.class)) {
            Permission p = (Permission) o;
            authorInfo.addStringPermission(p.toString());
        }
        for (Object o : collection.get(Role.class)) {
            Role r = (Role) o;
            authorInfo.addRole(r.getCanonicalName());
        }

        return authorInfo;
    }

    // TODO: Move the exceptions thrown in here out into the actual LDAP
    // Implementation, so the error are consistent.
    public boolean testLdapConnection(String username, String password)
            throws ConfigurationException {
        test = true;
        LdapContext context;
        try {
            JndiLdapContextFactory jndiLdapContextFactory = new JndiLdapContextFactory();
            jndiLdapContextFactory.setUrl(url);
            if (null != systemUser && null != systemPasswd) {
                jndiLdapContextFactory.setSystemUsername(systemUser);
                jndiLdapContextFactory.setSystemPassword(systemPasswd);
            } else {
                this.anonymous = true;
            }
            context = jndiLdapContextFactory.getSystemLdapContext();

            // set DN Template
            if (getUserDnTemplate() == DN_REPLACE_TOKEN) {
                String dn = getUserDN(context, username);
                setUserDnTemplate(dn);
            }
        } catch (NumberFormatException e) {
            String msg = String.format(
                    "Invalid port defined in the LDAP URL '%1$s'", url);
            logger.error(msg, e);
            throw new ConfigurationException(msg);
        } catch (javax.naming.AuthenticationException e) {
            String msg = String
                    .format("System account authentication error. Invalid username and password for system account '%1$s'",
                            systemUser);
            logger.error(msg, e);
            throw new ConfigurationException(msg);
        } catch (CommunicationException e) {
            String msg = String.format("LDAP server not found at url: '%1$s'",
                    url);
            logger.error(msg, e);
            throw new ConfigurationException(msg);
        } catch (NamingException e) {
            String msg = "Connection Failure";
            logger.error(msg, e);
            throw new ConfigurationException(msg + " : " + e.getMessage());
        }

        try {
            super.doGetAuthenticationInfo(new UsernamePasswordToken(username,
                    password.toCharArray()));
        } catch (AuthenticationException e) {
            String msg = String
                    .format("Failed to authenticate '%1$s' on ldap server. Invalid username and password. Either Invalid Username or Password or User DN property is required for this Ldap Connection.",
                            username);
            logger.error(msg, e);
            throw new ConfigurationException(msg);
        }

        String[] attrs = {att_fName, att_lName, att_email};
        try {
            getUserAttributes(context, username, attrs);
        } catch (NamingException e) {
            String msg;
            msg = (anonymous) ? "Failed to find user attributes in LDAP, with 'anonymous' access."
                    : "Failed to find user attributes in LDAP. Invalid user filter or user search base";
            logger.error(msg, e);
            throw new ConfigurationException(msg);
        }

        try {
            getUserGroupMembership(context, getUserDn(username));
        } catch (Exception e) {
            String msg = "Failed to get user group membership. Invalid group filter or group search base";
            logger.error(msg, e);
            throw new ConfigurationException(msg);
        }
        return true;
    }

    protected Set<String> getUserGroupMembership(LdapContext context,
                                                 String userDN) throws NamingException {
        Set<String> ldapGroups = new HashSet<>();
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        Object[] searchArguments = new Object[]{userDN};

        try {
            NamingEnumeration<SearchResult> renum = context.search(
                    groupSearchBase, groupFilter, searchArguments, controls);
            while (renum.hasMore()) {
                SearchResult result = renum.next();
                Attribute grp = result.getAttributes().get("cn");
                if (null != grp)
                    ldapGroups.add(grp.get().toString());
            }
        } catch (NameNotFoundException e) {
            logger.warn("No groups found for user :{}" + userDN);
        }
        return ldapGroups;
    }

    protected List<UserGroup> mapLdapGroupsToSMGroups(Set<String> ldapGroupNames) {
        List<UserGroup> groups = new ArrayList<>();
        if (ldapGroupNames == null || ldapGroupNames.isEmpty()) {
            return groups;
        }

        for (String grpName : ldapGroupNames) {
            try {
                UserGroup group = dataStore.getUserGroupDataStore()
                        .getUserGroupByUserGroupName(grpName);
                groups.add(group);
                logger.debug("Mapped group : {}, to ldap group : {}",
                        group.getCanonicalName(), grpName);
            } catch (Exception e) {
                logger.debug("Error getting group with group name : {}",
                        grpName);
            }
        }
        return groups;
    }

    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        logger.error("----------------");
        return super.getAuthorizationCacheKey(principals);
    }
}
