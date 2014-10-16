package com.edifecs.epp.security.datastore;

import java.util.Collection;
import java.util.Map;

import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.OrganizationDetail;
import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.SecurityRealm;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.jpa.entity.OrganizationEntity;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;

public interface IOrganizationDataStore extends IBaseSlaveDataStore<Organization> {

	SecurityRealm addRealmToOrganization(Long organizationId, SecurityRealm realm);

    Organization getOrganizationByDomainAndOrganizationName(String domain, String orgName) throws ItemNotFoundException, SecurityDataException;

	Organization getOrganizationByName(String name)
			throws ItemNotFoundException, SecurityDataException;

	SecurityRealm updateRealm(SecurityRealm realm)
			throws ItemNotFoundException, SecurityDataException;

	Organization getUserOrganizationByUserId(Long userId)
			throws ItemNotFoundException, SecurityDataException;

	Collection<Organization> getChildOrganizationsById(Long organizationId)
			throws SecurityDataException;

	void addChildOrganization(Long organizationId,
			Organization childOrganization) throws SecurityDataException;

	void removeChildOrganization(Long organizationId,
			Organization childOrganization) throws SecurityDataException;

	Tenant getTenantByOrganizationId(Long organizationId)
			throws ItemNotFoundException, SecurityDataException;

	PaginatedList<Organization> getOrganizationsForTenant(Long id,
			long startRecord, long recordCount) throws SecurityDataException;

	PaginatedList<Organization> getOrganizationsForGroup(Long id,
			long startRecord, long recordCount) throws SecurityDataException;

	void addRoleToOrganization(Long organizationId, Long roleId)
			throws SecurityDataException;

	void removeRoleFromOrganization(Long organizationId, Long roleId)
			throws SecurityDataException;

	OrganizationDetail getTransitiveChildOrganizationsForOrganization(
			Long organizationId) throws SecurityDataException;

	Organization ImportOrganization(Long tenantId, OrganizationEntity organization, 
			Map<String, String> myUserCredential, User auditor) 
			throws ItemAlreadyExistsException, SecurityDataException;
	
	OrgValidationHelper validateOrgImports(Long tenantId, Organization objOrg);

}
