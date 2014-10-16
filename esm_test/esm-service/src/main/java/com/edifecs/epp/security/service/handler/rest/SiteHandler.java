package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.data.Site;
import com.edifecs.epp.security.handler.rest.ISiteCommandHandler;
import com.edifecs.epp.security.service.SecurityContext;

public class SiteHandler extends AbstractCommandHandler implements ISiteCommandHandler {

    protected final SecurityContext sc;

    public SiteHandler(SecurityContext context) {
        sc = context;
    }

    public Site getSite() throws Exception {
        return sc.dataStore().getSiteDataStore().getSite();
    }

    public Site updateSite(Site site) throws Exception {
        site = sc.dataStore().getSiteDataStore().update(site, getSecurityManager().getSubjectManager().getUser());
        return site;
    }
}
