package com.edifecs.epp.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.epp.security.data.CustomProperty;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.SecurityRealm;
import com.edifecs.epp.security.datastore.ISecurityDataStore;
import com.edifecs.epp.security.service.realm.DataStoreRealm;
import com.edifecs.epp.security.service.realm.SecurityActiveDirectoryRealm;
import com.edifecs.epp.security.service.realm.SecurityLdapRealm;

public class RealmManager {

	private static DataStoreRealm defaultDataStoreRealm;
	private static SecurityManager securityManager;
	private static ISecurityDataStore dataStore;

	private static Logger logger = LoggerFactory.getLogger(RealmManager.class);
	private static ConcurrentMap<Long, List<Realm>> realmCache = new ConcurrentHashMap<>();

	private static Object mutex = new Object();

	// TODO: separete exception class
	public static void loadRealmsForOrganization(Organization organization)
			throws Exception {
		if (!realmCache.containsKey(organization.getId())) {
			addOrgRealmsToSecurityManager(organization);
		} else {
			setRealms(realmCache.get(organization.getId()));
		}
	}

	private static void checkInitialized() {
		if (null == securityManager || null == defaultDataStoreRealm)
			throw new IllegalStateException(
					"Realm Manager not initialized, check security service setup");
	}

	private static List<Realm> parseRealms(Organization organization)
			throws Exception {
		List<Realm> realms = new ArrayList<>();

		for (SecurityRealm sr : organization.getSecurityRealms()) {
			if (sr.isEnabled()) {
				switch (sr.getRealmType()) {

				case LDAP:
					// add tenant/org info
					sr.getProperties().add(addTenantProperty(organization));
					sr.getProperties().add(
							addOrganizationProperty(organization));
					SecurityLdapRealm realm = new SecurityLdapRealm(dataStore,
							sr);
					realm.setCacheManager(new MemoryConstrainedCacheManager());
					realm.setCachingEnabled(false);
					realms.add(realm);
					logger.debug("Added LDAP realm : {} to security manager.",
							sr.getName());
					break;

				case ACTIVEDIRECTORY:
					SecurityActiveDirectoryRealm adRealm = new SecurityActiveDirectoryRealm(
							dataStore, sr);
					adRealm.setCacheManager(new MemoryConstrainedCacheManager());
					adRealm.setCachingEnabled(false);
					realms.add(adRealm);
					logger.debug(
							"Added Active Directory realm : {} to security manager.",
							sr.getName());
					break;

				case DATABASE:
					// TODO : figure out persistance of this realm
					break;
				default:
					throw new IllegalStateException(String.format(
							" Invalid Realm Type : '%s'.", sr.getRealmType()
									.toString()));
				}
			}
		}
		return realms;
	}

	private static CustomProperty addOrganizationProperty(
			Organization organization) {
		CustomProperty tenantCp = new CustomProperty();
		tenantCp.setName(SecurityLdapRealm.USER_ORGANIZATION);
		tenantCp.setValue(organization.getCanonicalName());
		tenantCp.setDescription("Organization for User attempting to Login");
		return tenantCp;
	}

	private static CustomProperty addTenantProperty(Organization organization) {
		CustomProperty tenantCp = new CustomProperty();
		tenantCp.setName(SecurityLdapRealm.USER_TENANT);
		tenantCp.setValue(organization.getTenant().getDomain());
		tenantCp.setDescription("Tenant Domain for User attempting to Login");
		return tenantCp;
	}

	private static void addOrgRealmsToSecurityManager(Organization org)
			throws Exception {
		List<Realm> orgRealms = parseRealms(org);
		// add def realm
		orgRealms.add(defaultDataStoreRealm);
		setRealms(orgRealms);
		cacheRealm(org.getId(), orgRealms);
	}

	private static void setRealms(List<Realm> realms) {
		checkInitialized();
		synchronized (mutex) {
			if (securityManager instanceof DefaultSecurityManager) {
				DefaultSecurityManager defaultSecurityManager = (DefaultSecurityManager) securityManager;
				defaultSecurityManager.setRealms(realms);
				logger.debug("set realms # " + realms.size());
			}
		}
	}

	private static void cacheRealm(Long id, List<Realm> realms) {
		realmCache.put(id, realms);
	}

	public static void refresh(Long orgId) {
		realmCache.remove(orgId);
	}

	public static void setDefaultDataStoreRealm(
			DataStoreRealm defaultDataStoreRealm) {
		RealmManager.defaultDataStoreRealm = defaultDataStoreRealm;
	}

	public static void setSecurityManager(SecurityManager securityManager) {
		RealmManager.securityManager = securityManager;
	}

	public static void setDataStore(ISecurityDataStore dataStore) {
		RealmManager.dataStore = dataStore;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		RealmManager.logger = logger;
	}

}
