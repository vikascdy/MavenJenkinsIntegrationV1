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

package com.edifecs.contentrepository.test.unit;

import com.edifecs.contentrepository.api.IContentRepository;
import com.edifecs.contentrepository.jackrabbit.ContentRepository;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JackrabbitRepositoryMultiTenancyTest {
    protected static final String TEST_PATH = ContentRepository.SEPARATOR
            + "test" + ContentRepository.SEPARATOR;
    protected static final String TEST_FILE_NAME = "test-file.txt";
    protected static final String TEST_FILE_PATH = "/test-file.txt";
    // private static final String TEST_DIRECTORY_PATH = "/test-directory/";
    protected static final String REPO_CONFIG_XML = "repository.xml";

    private static final String REPO_HOME = FileUtils.getFile("target", "test-classes", "repository").getAbsolutePath();
//    private static final String REPO_HOME = FileUtils.getFile("E:\\platform\\repo\\content-repository\\content-repository-jackrabbit-api\\src\\test\\resources\\repository").getAbsolutePath();

    protected static IContentRepository contentRepository;
    // users belonging to same/different tenants
    protected static Map<User, Tenant> users = new HashMap<User, Tenant>();
    protected static Organization org;

    @BeforeClass
    public static void setUp() throws Exception {

        File existRepo = FileUtils.getFile(REPO_HOME);
        if (existRepo.exists()) {
            for (File f : existRepo.listFiles())
                if (!f.getName().equalsIgnoreCase(REPO_CONFIG_XML))
                    FileUtils.deleteDirectory(f);
        }

        User siteAdmin = new User();
        siteAdmin.setId(1L);
        siteAdmin.setUsername("site_admin");

        Tenant tenant1 = new Tenant();
        tenant1.setCanonicalName(SystemVariables.DEFAULT_TENANT_NAME);
        tenant1.setId(1L);

        // via org
        org = new Organization();
        org.setCanonicalName("Test Def Org" + new Date());
        org.setId(1L);

        User u1T1 = new User();
        u1T1.setId(2L);
        u1T1.setUsername("u1t1");

        Tenant tenant2 = new Tenant();
        tenant2.setCanonicalName("Test Tenant 2");
        tenant2.setId(2L);

        User u1T2 = new User();
        u1T2.setId(3L);
        u1T2.setUsername("u1t2");

        Tenant tenant3 = new Tenant();
        tenant3.setCanonicalName("Test Tenant 3");
        tenant3.setId(3L);

        User u1T3 = new User();
        u1T3.setId(4L);
        u1T3.setUsername("u1t2");

        users.put(u1T1, tenant1);
        users.put(u1T2, tenant2);
        users.put(u1T3, tenant3);

        contentRepository = new MockCententRepository(siteAdmin);

        contentRepository.setupTenantRepository(REPO_HOME, tenant1);
        // contentRepository.addUserToTenantRepository(tenant1, u1T1.getUsername(),
        // "password", true);

        // contentRepository.addUserToTenantRepository(tenant1, u1T2.getUsername(),
        // "password", false);

        contentRepository.setupTenantRepository(REPO_HOME, tenant2);
        // contentRepository.addUserToTenantRepository(tenant2, u1T2.getUsername(),
        // "password", false);

        contentRepository.setupTenantRepository(REPO_HOME, tenant3);
        // contentRepository.addUserToTenantRepository(tenant3, u1T3.getUsername(),
        // "password", false);

        Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        contentRepository.shutdown();
    }


    @Test
    public void testMultiTenantRepoAccess() throws Exception {
        for (Entry<User, Tenant> entry : users.entrySet()) {
            ((MockCententRepository) contentRepository).setTestTenant(entry
                    .getValue());
            ((MockCententRepository) contentRepository).simulateUserLogin(entry
                    .getKey());

            contentRepository.createFolder(TEST_PATH);
            contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                    .getResourceAsStream(TEST_FILE_PATH));
            InputStream inputStream =
                    contentRepository.getFile(TEST_PATH + TEST_FILE_NAME);
            System.out.println("User : " + entry.getKey().getUsername()
                    + " Stream : " + inputStream);
            contentRepository.deleteFolder(TEST_PATH);

        }
    }
}
