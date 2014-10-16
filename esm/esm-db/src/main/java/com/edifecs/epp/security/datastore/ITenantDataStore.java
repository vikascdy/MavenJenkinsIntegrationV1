package com.edifecs.epp.security.datastore;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper;

public interface ITenantDataStore extends IBaseSlaveDataStore<Tenant> {

	public Tenant getTenantByUserId(Long userId) throws ItemNotFoundException, SecurityDataException;

    Tenant getTenantByName(final String canonicalName)
            throws ItemNotFoundException, SecurityDataException;

    Tenant updateTenantPasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy)
            throws SecurityDataException, ItemNotFoundException;

    Tenant getTenantByDomain(String defaultTenantName) throws ItemNotFoundException, SecurityDataException;

    Boolean updateLogo(Long tenantId, String imgData) throws SecurityDataException;

    String getLogo(String tenant) throws SecurityDataException;

    Boolean updateLandingPage(Long tenantId, String landingPage) throws SecurityDataException;

    String getLandingPage(Long tenantId) throws SecurityDataException;

	public PaginatedList<Tenant> getPaginatedRangeForSite(long siteId,
			long startRecord, long recordCount) throws SecurityDataException;
    
	TenantValidationHelper validateTenantImport(Tenant tenant)
			throws Exception;
}
