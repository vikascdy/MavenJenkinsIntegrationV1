package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SiteDataStoreTest extends AbstractDataStoreTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testUpdate() {
        try {
            Site site = dds.getSiteDataStore().getSite();
            site.setCanonicalName(getClass().getSimpleName() + ":Site 2 updated");
            dds.getSiteDataStore().update(site, superUser);

            assertNotNull(dds.getSiteDataStore().getSite());
            assertEquals(getClass().getSimpleName() + ":Site 2 updated", dds.getSiteDataStore().getSite()
                    .getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
