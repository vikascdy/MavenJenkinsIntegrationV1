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

package com.edifecs.contentrepository.jackrabbit;

import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.contentrepository.api.model.TenantRepository;

public class TenantRepositoryCache extends TenantRepository {

    private final static Logger logger = LoggerFactory
            .getLogger(TenantRepositoryCache.class);

    private Repository repository;

    private ConcurrentHashMap<Long, Session> cachedSessions =
            new ConcurrentHashMap<>();

    public TenantRepositoryCache(Repository repository, String tenantId) {
        super();
        this.repository = repository;
        setTenantId(tenantId);
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ConcurrentHashMap<Long, Session> getCachedSessions() {
        return cachedSessions;
    }

    public void setCachedSessions(ConcurrentHashMap<Long, Session> cachedSessions) {
        this.cachedSessions = cachedSessions;
    }

}
