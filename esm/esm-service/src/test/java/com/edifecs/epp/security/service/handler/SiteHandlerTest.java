package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.exception.HandlerConfigurationException;
import com.edifecs.epp.security.data.Site;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.rest.SiteHandler;
import com.edifecs.epp.security.service.handler.rest.TenantHandler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SiteHandlerTest extends AbstractHandlerTest {

	private static SiteHandler siteHandler;
	private static TenantHandler tenantHandler;
    private static SecurityContext sc;

	@Before
	public void before() throws HandlerConfigurationException, SecurityManagerException {
		sc = new SecurityContext();
		sc.initDataStore(new DatabaseDataStore());
		sc.initManager(new com.edifecs.epp.security.remote.SecurityManager(
				null, commandCommunicator));
		siteHandler = new SiteHandler(sc);
		tenantHandler = new TenantHandler(sc);
		siteHandler.initialize(commandCommunicator, commandCommunicator);
		tenantHandler.initialize(commandCommunicator, commandCommunicator);
	}

	@Test
	public void testUpdateSite() throws Exception {
		Site s = new Site();
		s.setCanonicalName("s1");
        s = sc.dataStore().getSiteDataStore().getSite();
		s.setCanonicalName("s1 updated");
		s = siteHandler.updateSite(s);
		assertNotNull(siteHandler.getSite());
		assertEquals("s1 updated", siteHandler.getSite()
				.getCanonicalName());
	}
}
