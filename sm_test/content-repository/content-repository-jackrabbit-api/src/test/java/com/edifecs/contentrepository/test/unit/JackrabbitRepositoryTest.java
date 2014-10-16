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

import com.edifecs.contentrepository.api.ContentNode;
import com.edifecs.contentrepository.api.FileVersion;
import com.edifecs.contentrepository.api.IContentRepository;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.contentrepository.jackrabbit.ContentRepository;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

public class JackrabbitRepositoryTest {
    // UI will always send path like this :
    private static final String TEST_PATH = "test"
            + ContentRepository.SEPARATOR;

    private static final String TEST_FILE_NAME = "test-file.txt";
    private static final String TEST_FILE_PATH = "/test-file.txt";
    private static final String TEST_DIRECTORY_PATH = "/test-directory/";
    private static final String REPO_CONFIG_XML = "repository.xml";
    private static final String TENANT_ID = "1";
    private static final String REPO_HOME = FileUtils.getFile("target", "test-classes", "repository").getAbsolutePath();

    private static IContentRepository contentRepository;
    private static User user1;
    private static User user2;
    private static User user3;

    @BeforeClass
    public static void setUp() throws Exception {

        File existRepo = FileUtils.getFile(REPO_HOME);
        if (existRepo.exists()) {
            for (File f : existRepo.listFiles())
                if (!f.getName().equalsIgnoreCase(REPO_CONFIG_XML))
                    FileUtils.deleteDirectory(f);
        }

        Tenant tenant = new Tenant();
        tenant.setCanonicalName("Test Tenant");
        tenant.setId(Long.valueOf(TENANT_ID));

        Organization org = new Organization();
        org.setCanonicalName("Test Def Org");
        org.setId(1L);

        User root = new User();
        root.setId(1L);
        root.setUsername("admin");

        user1 = new User();
        user1.setId(2L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(3L);
        user2.setUsername("user2");

        user3 = new User();
        user3.setId(4L);
        user3.setUsername("user3");

        contentRepository = new MockCententRepository(tenant, root, org);
        contentRepository.setupTenantRepository(REPO_HOME, tenant);
        // contentRepository.addUserToTenantRepository(tenant, user1.getUsername(),
        // "password", false);
        contentRepository.addUserToTenantRepository(tenant, user2.getUsername(),
                "password", false);
        // contentRepository.addUserToTenantRepository(tenant, user3.getUsername(),
        // "password", false);

        Thread.sleep(1000);

        try {
            contentRepository.deleteNodePermanently(TEST_PATH + TEST_FILE_NAME);
        } catch (ContentRepositoryException e) {
            System.out.println("Test Paths already deleted");
        }
        try {
            contentRepository.deleteNodePermanently(TEST_PATH);
        } catch (ContentRepositoryException e) {
            System.out.println("Test Paths already deleted");
        }

        ((MockCententRepository) contentRepository).simulateUserLogin(user1);
    }

    @AfterClass
    public static void tearDown() throws Exception {

        try {
            contentRepository.deleteNodePermanently(TEST_PATH + TEST_FILE_NAME);
        } catch (ContentRepositoryException e) {
            System.out.println("Test Paths already deleted");
        }
        try {
            contentRepository.deleteNodePermanently(TEST_PATH);
        } catch (ContentRepositoryException e) {
            System.out.println("Test Paths already deleted");
        }

        contentRepository.shutdown();

    }

    @Test
    public void testAddFile() throws ContentRepositoryException {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        contentRepository.deleteFolder(TEST_PATH);

        assertTrue(true);
    }

    @Test
    @Ignore
    public void testShareContent() throws Exception {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        String sharedUrl =
                contentRepository.shareContentWithUser(TEST_PATH, TEST_FILE_NAME,
                        user2.getUsername());

        System.out.println("content shared : " + sharedUrl);

        ((MockCententRepository) contentRepository).simulateUserLogin(user2);
        InputStream in = contentRepository.getFile(sharedUrl);
        assertNotNull(in);

        ((MockCententRepository) contentRepository).simulateUserLogin(user1);
        contentRepository.deleteFolder(TEST_PATH);

    }

    @Test
    @Ignore
    public void testShareInvalidContent() throws Exception {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        try {
            contentRepository.shareContentWithUser(TEST_PATH + "Invalid",
                    TEST_FILE_NAME + "Invalid", user2.getUsername());
        } catch (ContentRepositoryException e) {
            System.out.println(e.getMessage());
            assertTrue(true);
        }

        contentRepository.deleteFolder(TEST_PATH);
    }

    @Test
    @Ignore
    public void testShareContentAccessFail() throws Exception {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        String sharedUrl =
                contentRepository.shareContentWithUser(TEST_PATH, TEST_FILE_NAME,
                        user2.getUsername());

        System.out.println("content shared : " + sharedUrl);

        ((MockCententRepository) contentRepository).simulateUserLogin(user3);
        try {
            contentRepository.getFile(sharedUrl);
        } catch (ContentRepositoryException e) {
            System.out.println(e.getMessage());
            assertTrue(true);
        }

        ((MockCententRepository) contentRepository).simulateUserLogin(user1);
        contentRepository.deleteFolder(TEST_PATH);
    }

    @Test
    public void testUpdateFile() throws ContentRepositoryException {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        contentRepository.updateFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        contentRepository.deleteFolder(TEST_PATH);

        assertTrue(true);
    }

    @Test
    public void testAddDuplicateFileFail() throws ContentRepositoryException {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        try {
            contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                    .getResourceAsStream(TEST_FILE_PATH));
        } catch (ContentRepositoryException e) {
            assertTrue(true);
        }
        contentRepository.deleteFolder(TEST_PATH);
    }

    @Test
    public void testAddInvalidFileFail() throws ContentRepositoryException {

        contentRepository.createFolder(TEST_PATH);

        try {
            contentRepository.addFile(TEST_PATH + "_invalid", TEST_FILE_NAME,
                    getClass().getResourceAsStream(TEST_FILE_PATH + "_invalid")); // test
            // file
            // path
            // is
            // invalid
        } catch (ContentRepositoryException e) {
            assertTrue(true);
        }

        contentRepository.deleteFolder(TEST_PATH);

    }

    @Test
    public void testAddDirectory() throws ContentRepositoryException,
            URISyntaxException {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addDirectory(TEST_PATH,
                new File(getClass().getResource(TEST_DIRECTORY_PATH).toURI()));

        assertTrue(true);
    }

    @Test
    public void testAddDuplicateDirectoryFail()
            throws ContentRepositoryException, URISyntaxException {

        contentRepository.createFolder(TEST_PATH);

        contentRepository.addDirectory(TEST_PATH,
                new File(getClass().getResource(TEST_DIRECTORY_PATH).toURI()));

        try {
            contentRepository.addDirectory(TEST_PATH, new File(getClass()
                    .getResource(TEST_DIRECTORY_PATH).toURI()));
        } catch (ContentRepositoryException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDeleteFile() throws ContentRepositoryException {

        contentRepository.createFolder(TEST_PATH);

        List<ContentNode> nodes = contentRepository.viewFolder(TEST_PATH);

        assertEquals(0, nodes.size());

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        nodes = contentRepository.viewFolder(TEST_PATH);

        assertEquals(1, nodes.size());

        contentRepository.deleteFile(TEST_PATH + TEST_FILE_NAME);

        nodes = contentRepository.viewFolder(TEST_PATH);

        assertEquals(0, nodes.size());
    }

    @Test
    public void testDeleteFileFail() {
        try {
            contentRepository.deleteFile(TEST_PATH + "_invalid" + TEST_FILE_NAME);
        } catch (ContentRepositoryException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testGetFile() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        InputStream inputStream =
                contentRepository.getFile(TEST_PATH + TEST_FILE_NAME);

        contentRepository.deleteFolder(TEST_PATH);

        assertNotNull(inputStream);
    }

    @Test
    public void testGetFileFail() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));
        InputStream inputStream = null;
        try {
            inputStream =
                    contentRepository.getFile(TEST_PATH + TEST_FILE_NAME + "_invalid");
        } catch (ContentRepositoryException e) {
            assertNull(inputStream);
        }
        contentRepository.deleteFolder(TEST_PATH);
    }

    @Test
    public void testListFolder() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        List<ContentNode> view = contentRepository.viewFolder(TEST_PATH);

        contentRepository.deleteFolder(TEST_PATH);

        System.out.println(view);

        assertNotNull(view);
    }

    @Test
    public void testListFolderFail() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        List<ContentNode> view = null;
        try {
            view = contentRepository.viewFolder(TEST_PATH + "_invalid");
        } catch (Exception e) {
            assertNull(view);
        }

        contentRepository.deleteFolder(TEST_PATH);

        System.out.println(view);

    }

    @Test
    public void testCreateFolder() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        assertTrue(true);
    }

    @Test
    public void testCreateFolderFail() throws ContentRepositoryException {
        try {
            contentRepository.createFolder(null);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDeleteFolder() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        contentRepository.deleteFolder(TEST_PATH);

        assertTrue(true);
    }

    @Test
    public void testDeleteFolderFail() throws ContentRepositoryException {

        try {
            contentRepository.deleteFolder(TEST_PATH + "_invalid");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    @Ignore
    public void testGetStatistics() throws ContentRepositoryException {
        String statistics = contentRepository.getStatistics();

        System.out.println(statistics);

        assertNotNull(statistics);
    }

    @Test
    @Ignore
    public void testGetHistory() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        contentRepository.updateFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        List<FileVersion> history =
                contentRepository.getHistory(TEST_PATH + TEST_FILE_NAME);

        System.out.println("HISTORY:");

        for (FileVersion p : history) {
            System.out.println("- " + p.getName() + " version " + p.getVersion()
                    + " at " + p.getDateUpdated());
        }
        contentRepository.deleteFolder(TEST_PATH);

        System.out.println(history);

        assertNotNull(history);
    }

    @Test
    @Ignore
    public void testGetHistoryFail() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        contentRepository.addFile(TEST_PATH, TEST_FILE_NAME, getClass()
                .getResourceAsStream(TEST_FILE_PATH));

        List<FileVersion> history = null;
        try {
            history =
                    contentRepository.getHistory(TEST_PATH + TEST_FILE_NAME + "invalid");
        } catch (Exception e) {
            assertNull(history);
        }

        contentRepository.deleteFolder(TEST_PATH);

    }

    @Test
    public void testDumpNode() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        String dump = contentRepository.dumpNode(TEST_PATH);

        System.out.println(dump);

        assertNotNull(dump);
    }

    @Test
    public void testDumpNodeFail() throws ContentRepositoryException {
        contentRepository.createFolder(TEST_PATH);

        String dump = null;
        try {
            dump = contentRepository.dumpNode(null);
        } catch (NullPointerException e) {
            assertNull(dump);
        }
    }
}
