/**
 *  -----------------------------------------------------------------------------
 * Copyright (c) Edifecs Inc. All Rights Reserved.
 * This software is the confidential and proprietary information of Edifecs Inc.
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Edifecs.
 *
 * EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 * -----------------------------------------------------------------------------
 */
package com.edifecs.epp.security.service.realm;

import com.edifecs.epp.security.data.CustomProperty;
import com.edifecs.epp.security.data.RealmConfig;
import com.edifecs.epp.security.data.RealmType;
import com.edifecs.epp.security.data.SecurityRealm;
import com.edifecs.epp.security.datastore.ISecurityDataStore;
import com.edifecs.epp.security.exception.RealmAuthenticationException;
import com.edifecs.epp.security.exception.RealmException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;
import java.util.*;

/**
 * The Class SecurityActiveDirectoryRealm, extends ActiveDirectoryRealm.
 * Overrides User Authentication and Authorization, adding the ability to add
 * LDAP users to the security database, and define roles and permissions for the
 * LDAP users.
 */
public class SecurityActiveDirectoryRealm extends SecurityLdapRealm {

    private static Logger logger = LoggerFactory
            .getLogger(SecurityActiveDirectoryRealm.class);

    protected final static String DOMAIN_NAME = "Domain Name";
    protected final static String ATT_MEMBER_OF = "Member Of Attribute";

    private String domainName;
    private String att_memberOf = "memberOf";

    /**
     * Instantiates a new security ldap realm, from ldap properties file.
     *
     * @param dataStore  the security data store
     * @param properties the LDAP properties @see LdapConfig
     */
    public SecurityActiveDirectoryRealm(ISecurityDataStore dataStore,
                                        Properties properties) throws Exception {
        super(dataStore, properties, RealmType.ACTIVEDIRECTORY);
    }

    public SecurityActiveDirectoryRealm(ISecurityDataStore dataStore,
                                        SecurityRealm realm) throws Exception {
        super(dataStore, CustomProperty.parseProperties(realm.getProperties()),
                RealmType.ACTIVEDIRECTORY);
    }

    @Override
    protected void initContextFactory(Properties properties) {
        this.userSearchBase = properties.getProperty(USER_SEARCH_BASE);
        this.groupSearchBase = properties.getProperty(GROUP_SEARCH_BASE);
        this.groupFilter = properties.getProperty(GROUP_FILTER);
        this.domainName = properties.getProperty(DOMAIN_NAME);
        this.userFilter = properties.getProperty(USER_FILTER);

        this.att_fName = properties.getProperty(ATT_FNAME, att_fName);
        this.att_lName = properties.getProperty(ATT_LNAME, att_lName);
        this.att_email = properties.getProperty(ATT_EMAIL, att_email);
        this.att_memberOf = properties.getProperty(ATT_MEMBER_OF, att_memberOf);

        JndiLdapContextFactory ldapContextFactory = new JndiLdapContextFactory();
        ldapContextFactory.setUrl(properties.getProperty(URL));
        if (domainName != null) {
            ldapContextFactory.setSystemUsername(domainName + "\\"
                    + properties.getProperty(SYSTEM_USER));
        } else {
            ldapContextFactory.setSystemUsername(properties
                    .getProperty(SYSTEM_USER));
        }
        ldapContextFactory.setSystemPassword(properties
                .getProperty(SYSTEM_PASS));
        setContextFactory(ldapContextFactory);
    }

    @Override
    protected Set<String> getUserGroupMembership(LdapContext context,
                                                 String userDN) throws NamingException {
        Set<String> groups = new HashSet<String>();
        Attributes attributes = context.getAttributes(userDN);
        return getTransientGroupMembers(context, groups, attributes);
    }

    private Set<String> getTransientGroupMembers(LdapContext context,
                                                 Set<String> groups, Attributes attributes) throws NamingException {
        Attribute memberOf = attributes.get(att_memberOf);
        if (memberOf != null) {
            Enumeration<?> e = memberOf.getAll();
            while (e.hasMoreElements()) {
                // Recursively Iterate through the LDAP Groups.
                String group = e.nextElement().toString();
                Attributes groupAttr = context.getAttributes(group);
                String groupName = groupAttr.get("cn").get().toString();
                if (!groups.contains(groupName)) {
                    logger.debug("Found Group {} ", groupName);
                    groups.add(groupName);
                    getTransientGroupMembers(context, groups, groupAttr);
                }
            }
        }

        return groups;
    }

    public static List<RealmConfig> getConfigMeta() {
        List<RealmConfig> ldapConfigs = new ArrayList<>();

        // TODO: Externalize these into resource files
        ldapConfigs
                .add(new RealmConfig(
                        URL,
                        "URL to the Active Directory server to connect to. (e.g. ldap://<ActiveDirectoryHostname>:<port>)",
                        "ldap://localhost:389", true));

        ldapConfigs
                .add(new RealmConfig(
                        SYSTEM_USER,
                        "systemUsername the system userName that will be used when creating an LDAP connection used for authorization queries.",
                        "uid=admin,ou=system", true));

        ldapConfigs
                .add(new RealmConfig(
                        SYSTEM_PASS,
                        "systemPassword the password of the systemUsername that will be used when creating an LDAP connection used for authorization queries.",
                        "", true));

        ldapConfigs
                .add(new RealmConfig(
                        GROUP_FILTER,
                        "filterExpr the filter expression to use for the search. The expression may contain variables of the form '{i}' where i is a nonnegative integer.",
                        "(&(objectClass=*)(member={0}))", false));

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
                "ou=groups,ou=system", false));

        ldapConfigs.add(new RealmConfig(ATT_FNAME,
                "LDAP attribute representing first name", "givenName", false));

        ldapConfigs.add(new RealmConfig(ATT_LNAME,
                "LDAP attribute representing last name", "sn", false));

        ldapConfigs.add(new RealmConfig(ATT_EMAIL,
                "LDAP attribute representing user email", "mail", false));

        ldapConfigs.add(new RealmConfig(DOMAIN_NAME,
                "Domain name for the Active Directory connection", "MYCORP",
                false));

        return ldapConfigs;
    }

    public static boolean testLdapAdministrativeConnection(String userName,
                                                           String password, String url) throws RealmException {
        try {
            JndiLdapContextFactory jndiLdapContextFactory = new JndiLdapContextFactory();
            jndiLdapContextFactory.setUrl(url);
            jndiLdapContextFactory.setSystemUsername(userName);
            jndiLdapContextFactory.setSystemPassword(password);
            jndiLdapContextFactory.getSystemLdapContext();

        } catch (AuthenticationException e) {
            throw new RealmAuthenticationException(e);
        } catch (NamingException e) {
            throw new RealmException(e);
        }
        return true;
    }

}
