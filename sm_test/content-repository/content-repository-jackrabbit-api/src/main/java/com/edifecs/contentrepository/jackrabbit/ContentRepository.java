// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information"). You shall not disclose such Confidential
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

import com.edifecs.contentrepository.api.*;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.ISecurityManager;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.exception.SecurityManagerException;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.value.BinaryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.query.*;
import javax.jcr.security.Privilege;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author willclem
 * @author virendra
 * @author abhising
 */
public class ContentRepository implements IContentRepository {

    public static final String SEPARATOR = "/";
    /**
     * VARIABLES *
     */
    public final static String TENANT_USERS_FOLDER = "Users";
    public final static String TENANT_ORGS_FOLDER = "Organizations";
    public final static String TENANT_PUBLIC_FOLDER = "Public";
    public final static String TENANT_PRIVATE_FOLDER = "Private";
    public final static String TENANT_SYSTEM_FOLDER = "System";
    public final static String TENANT_SYSTEM_XBOARD_FOLDER = "xBoard";
    public final static String SECURITY_ADMINISTRATORS = "administrators";
    public final static String SHARED = "shared";
    /**
     * JCR/Custom Node Types *
     */
    protected static final String NT_FILE = "nt:file";
    protected static final String NT_FOLDER = "nt:folder";
    protected static final String NT_RESOURCE = "nt:resource";
    protected static final String DELETED_MIXIN = "edifecs:preserveAfterDeletion";
    protected static final String DELETED_PROPERTY = "edifecs:deleted";
    protected static final String MIX_VERSIONABLE = "mix:versionable";
    protected static final String JCR_DATA = "jcr:data";
    protected static final String JCR_LAST_MODIFIED = "jcr:lastModified";
    protected static final String JCR_CREATED = "jcr:created";
    protected static final String JCR_CREATED_BY = "jcr:createdBy";
    protected static final String JCR_LAST_MODIFIED_BY = "jcr:lastModifiedBy";
    protected static final String JCR_CONTENT = "jcr:content";
    protected static final String JCR_PRIMARY_TYPE = "jcr:primaryType";
    protected static final String JCR_BASE_VERSION = "jcr:baseVersion";
    protected static final String JCR_ROOT_VERSION = "jcr:rootVersion";
    private static final int PROPERTY_VALUE_MAX_LENGTH = 256;

    protected static Long DEFAULT_TENANT_ID;
    private static ISecurityManager securityManager;
    private static ConcurrentHashMap<Long, TenantRepositoryCache> repositories =
            new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ContentRepository(ISecurityManager securityManager)
            throws ContentRepositoryException {
        setSecurityManager(securityManager);
        repositories.clear();
    }

    // Hackish, simple way to get MIME type based on file extension.
    // Uses Java's builtin map of extensions to MIME types.
    // FIXME: Final version of this API should probably be smarter about MIME
    // types.
    private static String getMimeType(final String filename) {
        try {
            final FileNameMap fileNameMap = URLConnection.getFileNameMap();
            return fileNameMap.getContentTypeFor(filename);
        } catch (Exception ex) {
            return "error";
        }
    }

    public static ISecurityManager getSecurityManager() {
        return securityManager;
    }

    public static void setSecurityManager(ISecurityManager securityManager) {
        ContentRepository.securityManager = securityManager;
    }

    public String translateNodename(String nodeName) {
        return nodeName.replace('[', '-').replace(']', '-');
    }

    @Override
    public void setupTenantRepository(String repositoryPath, Tenant tenant)
            throws ContentRepositoryException {
        Long tenantId = tenant.getId();
        if (tenant.getCanonicalName().equals(SystemVariables.DEFAULT_TENANT_NAME)) {
            this.DEFAULT_TENANT_ID = tenantId;
        }
        User user = getCurrentUser();
        String username = user.getUsername();
        Long userId = user.getId();

        if (null == repositories.get(tenantId)) {
            try {
                File repoDir = new File(repositoryPath + File.separator + tenantId);
                repoDir.mkdirs();
                File repoXml = new File(repositoryPath + File.separator
                        + SystemVariables.CONTENT_REPOSITORY_DEFAULT_CONFIG_XML_FILENAME);

                RepositoryConfig config = RepositoryConfig.create(repoXml, repoDir);
//                if (username == null) {
//                    username = SystemVariables.SYSTEM_USERNAME;
//                }
                config.getSecurityConfig().getLoginModuleConfig().getParameters().put("adminId", username);
                Repository repo = RepositoryImpl.create(config);

                TenantRepositoryCache repoCache = new TenantRepositoryCache(repo, tenantId.toString());
                repoCache.setAdmin(username);
                repoCache.setAdminId(userId);
                repositories.put(tenantId, repoCache);
                getLogger().debug(
                        "repository created for tenant : {}, with super user : {}",
                        tenantId, username);

                Session session =
                        repo.login(new SimpleCredentials(username, username.toCharArray()));
                repoCache.getCachedSessions().put(userId, session);
                getLogger().debug("repository session established for tenant : {}",
                        tenantId);

                registerNodeTypes(session);
                createDefaultFolders(session);
                setupDefaultAccounts(session);
                getRepositories().put(tenantId, repoCache);
                getLogger().info("repository setup complete for tenant : {}", tenantId);

            } catch (RepositoryException e) {
                getLogger().error("Error configuring repository for tenant.", e);
                throw new ContentRepositoryException(e);
            }
        } else {
            logger.info("repository already configured for tenant : {}", tenantId);
        }
    }

    private void createDefaultFolders(Session session) throws RepositoryException {
        Node root = session.getRootNode();
        if (!session.nodeExists(SEPARATOR + TENANT_ORGS_FOLDER)) {
            Node orgs = root.addNode(TENANT_ORGS_FOLDER, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED);
            orgs.addMixin(DELETED_MIXIN);
            if (!orgs.hasNode(TENANT_USERS_FOLDER))
                orgs.addNode(TENANT_USERS_FOLDER, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED)
                        .addMixin(DELETED_MIXIN);
        }
        if (!session.nodeExists(SEPARATOR + TENANT_PRIVATE_FOLDER))
            root.addNode(TENANT_PRIVATE_FOLDER, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED)
                    .addMixin(DELETED_MIXIN);
        if (!session.nodeExists(SEPARATOR + TENANT_SYSTEM_FOLDER)) {
            Node system = root.addNode(TENANT_SYSTEM_FOLDER, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED);
            system.addMixin(DELETED_MIXIN);
            system.addNode(TENANT_SYSTEM_XBOARD_FOLDER, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED)
                    .addMixin(DELETED_MIXIN);
        }
        if (!session.nodeExists(SEPARATOR + TENANT_PUBLIC_FOLDER))
            root.addNode(TENANT_PUBLIC_FOLDER, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED)
                    .addMixin(DELETED_MIXIN);
        session.save();
    }

    private void setupDefaultAccounts(Session session) throws RepositoryException, ContentRepositoryException {
        UserManager um = ((JackrabbitSession) session).getUserManager();
        Group adminGroup = (Group) um.getAuthorizable(SECURITY_ADMINISTRATORS);
        if (null == adminGroup) {
            adminGroup = um.createGroup(SECURITY_ADMINISTRATORS);
        }

        setACLForAdminGroup(session, adminGroup.getPrincipal());
        org.apache.jackrabbit.api.security.user.User admin =
                createJCRUser(session.getUserID(), session.getUserID(), session);
        adminGroup.addMember(admin);
        session.save();

    }

    private void setACLForAdminGroup(Session session, Principal adminPrincipal)
            throws PathNotFoundException, RepositoryException {
        addStrictPrivelige(session.getNode(SEPARATOR + TENANT_SYSTEM_FOLDER),
                new String[]{Privilege.JCR_ALL}, session, adminPrincipal);
        addStrictPrivelige(session.getNode(SEPARATOR + TENANT_ORGS_FOLDER),
                new String[]{Privilege.JCR_ALL}, session, adminPrincipal);
        addStrictPrivelige(session.getNode(SEPARATOR + TENANT_PRIVATE_FOLDER),
                new String[]{Privilege.JCR_ALL}, session, adminPrincipal);
        addStrictPrivelige(session.getNode(SEPARATOR + TENANT_PUBLIC_FOLDER),
                new String[]{Privilege.JCR_ALL}, session, adminPrincipal);
    }

    private void addStrictPrivelige(Node node, String[] privileges,
                                    Session session, Principal principal) throws RepositoryException {
        String path = node.getPath();
        AccessControlUtils.denyAllToEveryone(session, path);
        AccessControlUtils.addAccessControlEntry(session, path, principal, privileges, true);
        session.save();
    }

    private void addPrivelige(Node node, String[] privileges,
                              Session session, Principal principal) throws RepositoryException {
        String path = node.getPath();
        AccessControlUtils.addAccessControlEntry(session, path, principal, privileges, true);
        session.save();
    }

    @Override
    public void addUserToTenantRepository(Tenant tenant, final String username,
                                          String password, boolean isAdmin) throws ContentRepositoryException {

        password = username;
        Long tenantId = tenant.getId();
        // admin access
        Session session = getRootSession(tenantId);
        try {
            org.apache.jackrabbit.api.security.user.User user =
                    createJCRUser(username, password, session);
            if (isAdmin) {
                UserManager um = ((JackrabbitSession) session).getUserManager();
                Group adminGrp = (Group) um.getAuthorizable(SECURITY_ADMINISTRATORS);
                if (adminGrp != null)
                    adminGrp.addMember(user);
                else
                    throw new ContentRepositoryException(
                            "Error creating admin user for tenant : " + tenantId);

            }
            session.logout();
        } catch (RepositoryException e) {
            getLogger().error("Error creating user in reposiory for tenant.", e);
            throw new ContentRepositoryException(e);
        }
    }

    private org.apache.jackrabbit.api.security.user.User createJCRUser(
            final String username, String password, Session session)
            throws ContentRepositoryException, AccessDeniedException,
            UnsupportedRepositoryOperationException, RepositoryException {
        Authorizable user = null;
        UserManager um = ((JackrabbitSession) session).getUserManager();
        user = um.getAuthorizable(username);
        if (null == user) {
            user = um.createUser(username, password);
            session.save();
        } else {
            getLogger().debug("user : {} already exists in repository for tenant.",
                    username);
        }

        String orgName = getOrganization().getCanonicalName();
        Node root = session.getRootNode();
        String orgPath = root.getPath() +
                TENANT_ORGS_FOLDER +
                SEPARATOR +
                orgName;
        String userspath = orgPath + SEPARATOR +
                TENANT_USERS_FOLDER;

        String userPath = userspath + SEPARATOR + username;

        if (!session.nodeExists(userPath)) {
            Node orgsNode = session.getNode(session.getRootNode().getPath() + TENANT_ORGS_FOLDER);
            //check for org node
            Node usersNode = null;
            if (!orgsNode.hasNode(orgName)) {
                Node orgNode = orgsNode.addNode(orgName, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED);
                orgNode.addMixin(DELETED_MIXIN);
                usersNode = orgNode.addNode(TENANT_USERS_FOLDER, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED);
                usersNode.addMixin(DELETED_MIXIN);
            } else {
                usersNode = session.getNode(userspath);
            }

            Node userNode =
                    usersNode.addNode(username, javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED);
            userNode.addMixin(DELETED_MIXIN);
            addStrictPrivelige(userNode, new String[]{Privilege.JCR_ALL}, session,
                    user.getPrincipal());
            // read only for system folder
            addPrivelige(root.getNode(TENANT_SYSTEM_FOLDER), new String[]{Privilege.JCR_READ}, session, user.getPrincipal());
            session.save();
            logger.debug("created home directory for user : {} , path : {}",
                    username, userPath);
        }
        return (org.apache.jackrabbit.api.security.user.User) user;
    }

    @Override
    public String shareContentWithUser(String path, String filename,
                                       String username) throws ContentRepositoryException {
        try {
            Session session = getSession();
            Node content = nodeByUserPath(path);
            if (!session.nodeExists(content.getPath() + SEPARATOR + filename)) {
                throw new ContentRepositoryException(String.format(
                        "Invalid path, Node at path '%s' has no child named '%s'", path,
                        filename));
            }


            UserManager um;
            um = ((JackrabbitSession) session).getUserManager();
            Authorizable user = um.getAuthorizable(username);
            if (null != user) {
                AccessControlUtils
                        .addAccessControlEntry(session, content.getPath(), user
                                .getPrincipal(), AccessControlUtils.privilegesFromNames(
                                session, Privilege.JCR_READ), true);
                session.save();
                String url =
                        SEPARATOR + SHARED + content.getPath() + SEPARATOR + filename;
                return url;
            }

            throw new ContentRepositoryException("User not found : " + username);

        } catch (RepositoryException e) {
            throw new ContentRepositoryException("Failed to share : " + path
                    + filename, e);
        }
    }

    @Override
    public final void shutdown() throws Exception {
        // TODO: Add a timer to wait a certain time until all insertion into the
        // CR is finished before closing all connections. Currently it throws an
        // error if its shutdown in the middle of a transaction.
        for (TenantRepositoryCache cache : repositories.values()) {
            for (Session session : cache.getCachedSessions().values()) {
                session.logout();
            }
            ((RepositoryImpl) cache.getRepository()).shutdown();
        }
    }

    @Override
    public final void addFile(final String path, final String filename,
                              final byte[] file) throws ContentRepositoryException {
        addFile(path, filename, new ByteArrayInputStream(file));
    }

    @Override
    public final void addFile(final String path, final File file)
            throws ContentRepositoryException {
        try {
            addFile(path, file.getName(), new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final void addFile(String path, final String filename,
                              final InputStream inputStream) throws ContentRepositoryException {
        try {
            Node folderNode = nodeByUserPath(path);

            // Create the file node
            // if node already exists, it will throw ItemExistsException, upon
            // which updateFile() is called
            Node node = folderNode.addNode(filename, NT_FILE);

            // Enable Version Control
            node.addMixin(MIX_VERSIONABLE);

            // Enable preservation of deleted nodes.
            node.addMixin(DELETED_MIXIN);

            // Create the Content Node
            Node nodeContent = node.addNode(JCR_CONTENT, NT_RESOURCE);
            BinaryImpl binary = new BinaryImpl(inputStream);
            nodeContent.setProperty(JCR_DATA, binary);
            nodeContent.setProperty(JCR_LAST_MODIFIED, Calendar.getInstance());

            // Save the data to JCR
            getSession().save();

            // Checkin the file to Version Control
            // first version
            getVersionManager().checkin(node.getPath());

        } catch (ItemExistsException e) {
            // Check if a 'deleted file' placeholder exists at this path.
            try {
                final Node folderNode = nodeByUserPath(path);
                final Node fileNode = folderNode.getNode(filename);
                if (!isFile(fileNode)) {
                    throw new ContentRepositoryException(e);
                }
                if (isDeleted(fileNode)) {
                    // Don't overwrite place holders for non-file nodes.
                    if (!isFile(fileNode)) {
                        throw new ContentRepositoryException("The path '" + path + "/"
                                + filename + "' was previously a folder"
                                + " or other non-file node. A file cannot be created"
                                + " at this path.");
                    }
                    // If a 'deleted file' flag exists, remove it.
                    markNotDeleted(fileNode);
                }
                updateFile(path, filename, inputStream);
            } catch (RepositoryException e2) {
                throw new ContentRepositoryException(e2);
            }
        } catch (RepositoryException | IOException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final void updateFile(final String path, final String filename,
                                 final byte[] file) throws ContentRepositoryException {
        updateFile(path, filename, new ByteArrayInputStream(file));
    }

    @Override
    public final void updateFile(final String path, final File file)
            throws ContentRepositoryException {
        try {
            updateFile(path, file.getName(), new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final void updateFile(final String path, final String filename,
                                 final InputStream file) throws ContentRepositoryException {
        try {
            Session session = getSession();
            Node folderNode = nodeByUserPath(path);

            // add new version
            Node child = folderNode.getNode(filename);
            if (child == null || !isFile(child) || isDeleted(child)) {
                throw new ContentRepositoryException("The node '" + path + "/"
                        + filename + "' does not exist or is not a file node.");
            }
            getVersionManager().checkout(child.getPath());
            BinaryImpl binary = new BinaryImpl(file);
            Node nodeContent = child.getNode(JCR_CONTENT);
            nodeContent.setProperty(JCR_DATA, binary);
            nodeContent.setProperty(JCR_LAST_MODIFIED, Calendar.getInstance());
            nodeContent.setProperty(JCR_LAST_MODIFIED_BY, session.getUserID());
            session.save();
            getVersionManager().checkin(child.getPath());
        } catch (RepositoryException | IOException e) {
            throw new ContentRepositoryException(e);
        }

    }

    @Override
    public final void addDirectory(final String repositoryPath,
                                   final String localPath) throws ContentRepositoryException {
        addDirectory(repositoryPath, new File(localPath));
    }

    @Override
    public final void addDirectory(final String repositoryPath,
                                   final File localPath) throws ContentRepositoryException {
        try {
            Node nodeParent = nodeByUserPath(repositoryPath);
            String nodeName = translateNodename(localPath.getName());
            if (!localPath.isDirectory()) {
                VersionManager vm = getVersionManager();

                // If the file already exists
                if (nodeParent.hasNode(nodeName)) {
                    Node node = nodeParent.getNode(nodeName);

                    // If the file or folder is marked as deleted, undelete the
                    // folder.
                    if (!isFolder(node)) {
                        if (isDeleted(node)) {
                            throw new ContentRepositoryException(
                                    "The path '"
                                            + repositoryPath
                                            + "' was previously a file or other"
                                            + " non-folder node. A folder cannot be created at this path.");
                        }
            /*
             * throw new ContentRepositoryException("The path '" + repositoryPath +
             * "' is already occupied by a file or other non-folder node." );
             */
                    } else if (isDeleted(node)) {
                        markNotDeleted(node);
                    }

                    // Get the last modified time for the file and the JR Node
                    // and see if there was a recent update.
                    Node nodeContent = node.getNode(JCR_CONTENT);
                    long calendar =
                            nodeContent.getProperty(JCR_LAST_MODIFIED).getDate()
                                    .getTimeInMillis();
                    long filetime = localPath.lastModified();

                    // If the local file is newer then the remote one, upload
                    // the newer version
                    if (calendar < filetime) {
                        vm.checkout(node.getPath());

                        BinaryImpl binary = new BinaryImpl(new FileInputStream(localPath));
                        nodeContent.setProperty(JCR_DATA, binary);
                        nodeContent.setProperty(JCR_LAST_MODIFIED, Calendar.getInstance());
                        getSession().save();

                        vm.checkin(node.getPath());
                    }
                    // If the file does not exist yet exist, upload
                } else {
                    if (localPath.exists()) {
                        Node node =
                                nodeParent.addNode(translateNodename(localPath.getName()),
                                        NT_FILE);
                        // Enable versioning
                        node.addMixin(MIX_VERSIONABLE);
                        // Enable preservation of deleted nodes.
                        node.addMixin(DELETED_MIXIN);
                        Node nodeContent = node.addNode(JCR_CONTENT, NT_RESOURCE);
                        BinaryImpl binary = new BinaryImpl(new FileInputStream(localPath));
                        nodeContent.setProperty(JCR_DATA, binary);
                        nodeContent.setProperty(JCR_LAST_MODIFIED, Calendar.getInstance());
                        getSession().save();
                        vm.checkin(node.getPath()); // first version
                    }
                }

            } else {
                if (!nodeParent.hasNode(nodeName)) {
                    nodeParent.addNode(nodeName, NT_FOLDER);
                }
                getSession().save();
                for (File child : localPath.listFiles()) {
                    if (repositoryPath.endsWith(SEPARATOR)) {
                        addDirectory(repositoryPath + nodeName, child);
                    } else {
                        addDirectory(repositoryPath + SEPARATOR + nodeName, child);
                    }
                }

            }
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        } catch (FileNotFoundException e) {
            throw new ContentRepositoryException(e);
        } catch (IOException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final void deleteFile(final String path)
            throws ContentRepositoryException {
        Node fileNode = nodeByUserPath(path);
        if (fileNode == null || !isFile(fileNode) || isDeleted(fileNode)) {
            throw new ContentRepositoryException("The node '" + path
                    + "' does not exist or is not a file node.");
        }
        markDeleted(fileNode);
    }

    @Override
    public final void deleteFile(final String path, final String filename)
            throws ContentRepositoryException {
        deleteFile(path + filename);
    }

    @Override
    public final void deleteNodePermanently(final String path)
            throws ContentRepositoryException {
        try {
            Node n = nodeByUserPath(path);
            n.remove();
            getSession().save();
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final InputStream getFile(final String path)
            throws ContentRepositoryException {

        boolean shared = false;
        try {
            Node fileNode;
            if (!path.contains(SHARED)) {
                fileNode = nodeByUserPath(path);
            } else {
                shared = true;
                String newPath = path.replace(SEPARATOR + SHARED, "");
                fileNode = getSession().getNode(newPath);
            }

            if (fileNode == null || !isFile(fileNode) || isDeleted(fileNode)) {
                throw new ContentRepositoryException("The node '" + path
                        + "' does not exist or is not a file node.");
            }

            Node jcrContent = fileNode.getNode(JCR_CONTENT);

            if (jcrContent == null) {
                throw new ContentRepositoryException("The file node '" + path
                        + "' does not have a content subnode; its contents cannot be read.");
            }

            return jcrContent.getProperty(JCR_DATA).getBinary().getStream();

        } catch (RepositoryException e) {
            if (shared) {
                throw new ContentRepositoryException(
                        "Access Denied, you do not have sufficient privileges to access "
                                + path.replace(SEPARATOR + SHARED, ""));
            }
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final InputStream getFile(final String path, final String filename)
            throws ContentRepositoryException {
        if (path.endsWith(SEPARATOR)) {
            return getFile(path + filename);
        } else {
            return getFile(path + SEPARATOR + filename);
        }
    }

    @Override
    public final InputStream getFile(final String path, final String filename,
                                     final String version) throws ContentRepositoryException {

        // restoring old version

        try {
            Node rootNode = nodeByUserPath(path);
            Node fileNode = rootNode.getNode(filename);

            // getVersion
            VersionHistory history =
                    getVersionManager().getVersionHistory(fileNode.getPath());
            Version baseVersion = null;
            for (VersionIterator it = history.getAllVersions(); it.hasNext(); ) {
                Version ver = (Version) it.next();
                if (ver.getName().equals(version)) {
                    baseVersion = ver;
                    break;
                }
            }

            // restore to the baseVersion

            getVersionManager().checkout(fileNode.getPath());
            if (baseVersion != null) {
                getVersionManager().restore(baseVersion, true);
            }

            // getContent
            Node jcrContent = fileNode.getNode(JCR_CONTENT);

            return jcrContent.getProperty(JCR_DATA).getBinary().getStream();

        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }

    }

    protected final Node nodeByUserPath(String path) throws ContentRepositoryException {
        try {
            // temp hack for UI issue
            if (null != path)
                path = path.replace("\"", "").replace("//", "/");
            return getSession().getNode(buildPathforUser(path));
        } catch (PathNotFoundException e) {
            throw new ContentRepositoryException(e);
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    private ContentNode nodeToContentNode(final Node n)
            throws ContentRepositoryException {
        try {
            String version = "0";
            if (isFile(n)) {
                try {
                    version = getVersionManager().getBaseVersion(n.getPath()).getName();
                } catch (UnsupportedRepositoryOperationException ex) {
                    ex.printStackTrace();
                }
            }
            com.edifecs.contentrepository.api.NodeType nt = NodeType.SYSTEM;
            boolean directory = false;
            if (isFolder(n)) {
                nt = NodeType.FOLDER;
            }
            if (n.hasNodes() || nt == NodeType.FOLDER) {
                directory = true;
            }
            if (directory && nt != NodeType.FOLDER
                    && JCR_CONTENT.equals(n.getNodes().nextNode().getName())) {
                directory = false;
                nt = NodeType.FILE;
            }
            String mimeType = "unknown";
            if (nt == NodeType.FILE) {
                final String foundMimeType = getMimeType(n.getName());
                if (foundMimeType != null) {
                    mimeType = foundMimeType;
                }
            }
            return new ContentNode(NodePath.fromString(n.getPath()), version,
                    mimeType, directory, nt);
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final ContentNode getNode(final String path)
            throws ContentRepositoryException {
        try {
            Node node = getSession().getNode(path);
            if (isDeleted(node)) {
                return null;
            }
            return nodeToContentNode(node);
        } catch (PathNotFoundException e) {
            return null;
            // throw new ContentRepositoryException(e);
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final void moveNode(final String srcPath, final String destPath)
            throws ContentRepositoryException {
        try {
            getSession().move(srcPath, destPath);
            // TODO: Check for deleted nodes in the destination path!
        } catch (ItemExistsException e) {
            throw new ContentRepositoryException(
                    "Node already exists at destination path '" + destPath + "'.");
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final void copyNode(final String srcPath, final String destPath)
            throws ContentRepositoryException {
        try {
            getSession().getWorkspace().copy(srcPath, destPath);
            // TODO: Check for deleted nodes in the destination path!
        } catch (ItemExistsException e) {
            throw new ContentRepositoryException(
                    "Node already exists at destination path '" + destPath + "'.", e);
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final List<ContentNode> viewFolder(final String path)
            throws ContentRepositoryException {
        List<ContentNode> nodeList = new ArrayList<ContentNode>();
        try {
            Node node = nodeByUserPath(path);
            NodeIterator nodeIterator = node.getNodes();
            while (nodeIterator.hasNext()) {
                Node temp = (Node) nodeIterator.next();
                if (!isDeleted(temp)) {
                    nodeList.add(nodeToContentNode(temp));
                }
            }
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }

        return nodeList;
    }

    @Override
    public final void createFolder(String path) throws ContentRepositoryException {
        try {

            // TODO : admin content shud go fo admin folder or system?
            Session session = getSession();
            Node node;
            if (isAdminUser(getCurrentUser().getUsername())) {
                node = session.getRootNode();
            } else {
                String home = pathToUserHome();
                node = session.getNode(home);
                path = path.replace(home, "");
            }

            for (String directoryName : path
                    .split(ContentRepository.SEPARATOR)) {
                if (!directoryName.equals("")) {
                    try {
                        node = node.getNode(directoryName);
                        if (isDeleted(node)) {
                            if (!isFolder(node)) {
                                throw new ContentRepositoryException(
                                        "The path '"
                                                + node.getPath()
                                                + "' was previously a file or other"
                                                + " non-folder node. A folder cannot be created at this path.");
                            }
                            markNotDeleted(node);
                        }
                    } catch (PathNotFoundException e) {
                        node =
                                node.addNode(directoryName,
                                        javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED);
                        // Enable preservation of deleted nodes.
                        node.addMixin(DELETED_MIXIN);
                        logger.info("added node : {}", path);
                    }
                }
            }

            session.save();

        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final void deleteFolder(final String path)
            throws ContentRepositoryException {
        try {
            nodeByUserPath(path).remove();
            getSession().save();
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final String getStatistics() throws ContentRepositoryException {
        Session session = getSession();
        String user = session.getUserID();
        String name =
                session.getRepository().getDescriptor(Repository.REP_NAME_DESC);

        return new StringBuilder().append("Logged in as ").append(user)
                .append(" to a ").append(name).append(" repository.").toString();
    }

    private FileVersion versionToFileVersion(final Node n, final Version v)
            throws ContentRepositoryException {
        try {
            long size = 0;
            try {
                size = n.getNode(JCR_DATA).getProperty(JCR_CONTENT).getLength();
            } catch (PathNotFoundException e) {
                return new FileVersion(n.getName(), v.getName(), "unknown", size, v
                        .getCreated().getTime());
            }
            return new FileVersion(n.getName(), v.getName(), "unknown", size, v
                    .getCreated().getTime());
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    @Override
    public final Map<String, String> getProperties(final String path)
            throws ContentRepositoryException {
        final Node node = nodeByUserPath(path);

        final Map<String, String> map = new HashMap<String, String>();
        try {

            if (node != null && !isDeleted(node)) {

                final PropertyIterator iter = node.getProperties();
                while (iter.hasNext()) {
                    final Property p = iter.nextProperty();
                    if (p.isMultiple()) {
                        map.put(p.getName(), "[Multiple Values]");
                    } else {
                        final long length = p.getLength();
                        if (length < PROPERTY_VALUE_MAX_LENGTH) {
                            map.put(p.getName(), p.getString());
                        } else {
                            map.put(p.getName(), "[" + length + " bytes]");
                        }
                    }
                }
                NodeIterator nodes = node.getNodes();
                while (nodes.hasNext()) {
                    final Node n = nodes.nextNode();
                    if (JCR_CONTENT.equals(n.getName())) {
                        Node nodeContent = node.getNode(JCR_CONTENT);

                        final PropertyIterator iterContent = nodeContent.getProperties();

                        while (iterContent.hasNext()) {
                            final Property pContent = iterContent.nextProperty();
                            if (pContent.isMultiple()) {
                                map.put(pContent.getName(), "[Multiple Values]");
                            } else {
                                final long length = pContent.getLength();
                                if (length < PROPERTY_VALUE_MAX_LENGTH) {
                                    map.put(pContent.getName(), pContent.getString());
                                } else {
                                    map.put(pContent.getName(), "[" + length + " bytes]");
                                }
                            }
                        }

                        break;
                    }
                }

            }
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
        return map;
    }

    @Override
    public final List<FileVersion> getHistory(final String path)
            throws ContentRepositoryException {
        Node node = nodeByUserPath(path);
        List<FileVersion> versions = new ArrayList<FileVersion>();
        if (node != null) {
            try {
                VersionHistory history =
                        getVersionManager().getVersionHistory(node.getPath());
                for (VersionIterator it = history.getAllVersions(); it.hasNext(); ) {
                    Version version = (Version) it.next();
                    if (JCR_ROOT_VERSION.equals(version.getName())) {
                        continue;
                    }
                    versions.add(versionToFileVersion(node, version));
                }
            } catch (UnsupportedRepositoryOperationException ex) {
                // Return an empty list if the node is not versionable.
                return versions;
            } catch (RepositoryException | ContentRepositoryException e) {
                throw new ContentRepositoryException(e);
            }
        }
        return versions;
    }

    @Override
    public final List<FileVersion> getHistory(final String path,
                                              final String filename) throws ContentRepositoryException {
        if (path.endsWith(SEPARATOR)) {
            return getHistory(path + filename);
        } else {
            return getHistory(path + SEPARATOR + filename);
        }
    }

    @Override
    public void search(String keyword) throws ContentRepositoryException {
        // temp
        try {
            QueryManager queryManager = getSession().getWorkspace().getQueryManager();
            Query query =
                    queryManager.createQuery("select * from [nt:folder]", Query.JCR_SQL2);
            QueryResult result = query.execute();
            RowIterator ri = result.getRows();
            while (ri.hasNext()) {
                Row row = ri.nextRow();
                System.out.println("Row : " + row.toString());
                for (Value s : row.getValues()) {
                    System.out.println("val " + s.getString());
                }
                Node n = row.getNode();
                System.out.println(" user "
                        + getCurrentUser().getUsername()
                        + " has access to "
                        + n.getPath()
                        + getSession().getAccessControlManager().hasPrivileges(
                        n.getPath(),
                        AccessControlUtils.privilegesFromNames(getSession()
                                .getAccessControlManager(), Privilege.JCR_READ)));
            }
        } catch (InvalidQueryException e) {
        } catch (RepositoryException e) {
        }
    }

    /*
     * Protected Methods
     */
    protected Tenant getTenant() {
        try {
            return securityManager.getSubjectManager().getTenant();
        } catch (SecurityManagerException e) {
            throw new IllegalStateException(
                    "Content Repository is unable to get Tenant information", e);
        }
    }

    protected Organization getOrganization() {
        try {
            return securityManager.getSubjectManager().getOrganization();
        } catch (SecurityManagerException e) {
            throw new IllegalStateException(
                    "Content Repository is unable to get Organization information", e);
        }
    }

    protected User getCurrentUser() {
        try {
            return securityManager.getSubjectManager().getUser();
        } catch (SecurityManagerException e) {
            throw new IllegalStateException(
                    "Content Repository is unable to get current User Information", e);
        }
    }

    /*
     * get session for current users's repository(tenant)
     */
    protected final Session getSession() throws ContentRepositoryException {
        return getSession(null);
    }

    /*
     * get session for specific repository(tenant)
     */
    protected final Session getSession(Long tenantId)
            throws ContentRepositoryException {
        if (repositories.isEmpty())
            throw new ContentRepositoryException(
                    "No Repositories Available, Please set up a Repository");

        Long tId = (null == tenantId) ? getTenant().getId() : tenantId;
        User user = getCurrentUser();
        return getActiveSession(user, tId);
    }

    protected final Session getRootSession(Long tenantId)
            throws ContentRepositoryException {
        if (repositories.isEmpty())
            throw new ContentRepositoryException(
                    "No Repositories Available, Please set up a Repository");

        Long tId = (null == tenantId) ? getTenant().getId() : tenantId;
        TenantRepositoryCache repo = repositories.get(tId);
        if (null == repo) {
            throw new ContentRepositoryException(
                    "Fatal Error, No repository has been configured for this Tenant : "
                            + tenantId);
        }
        try {
//            Session session = repo.getCachedSessions().get(repo.getAdminId());
//            if (null == session || !session.isLive()) {
            Session session = repo.getRepository().login(
                    new SimpleCredentials(repo.getAdmin(), repo.getAdmin()
                            .toCharArray()));
//            repo.getCachedSessions().put(repo.getAdminId(), session);
//            }
            return session;
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    public Session getActiveSession(User user, Long tenantId)
            throws ContentRepositoryException {
        Long userId = user.getId();
        TenantRepositoryCache repo = repositories.get(tenantId);
        if (null == repo) {
            throw new ContentRepositoryException(
                    "Fatal Error, No repository has been configured for this Tenant : "
                            + tenantId);
        }

        Session session = repo.getCachedSessions().get(userId);
        if (session == null || !session.isLive()) {

            Tenant t = new Tenant();
            t.setId(tenantId);
            addUserToTenantRepository(t, user.getUsername(), user.getUsername(),
                    false);
            logger
                    .warn(
                            "no cached session found for userId : {}, creating new repository session.",
                            userId);
            try {
                session =
                        repo.getRepository().login(
                                new SimpleCredentials(user.getUsername(), user.getUsername()
                                        .toCharArray()));
            } catch (RepositoryException e) {
                throw new ContentRepositoryException(e);
            }

            repo.getCachedSessions().put(userId, session);
        }
        return session;
    }

    protected final String buildPathforUser(String path) {
        String username = getCurrentUser().getUsername();
        if (isAdminUser(username)) {
            if (!path.startsWith(SEPARATOR)) {
                path = SEPARATOR + path;
            }
            return path;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(pathToUserHome());
        if (null != path) {
            if (path.contains(sb.toString()))
                return path;
            if (!path.startsWith(SEPARATOR)) {
                sb.append(SEPARATOR);
            }
            sb.append(path);
        }
        return sb.toString();
    }

    protected final String pathToUserHome() {
        String username = getCurrentUser().getUsername();
        String orgName = getOrganization().getCanonicalName();
        StringBuilder sb = new StringBuilder();
        sb.append(SEPARATOR);
        sb.append(TENANT_ORGS_FOLDER);
        sb.append(SEPARATOR);
        sb.append(orgName);
        sb.append(SEPARATOR);
        sb.append(TENANT_USERS_FOLDER);
        sb.append(SEPARATOR);
        sb.append(username);
        return sb.toString();
    }

    private boolean isAdminUser(String username) {
        Session session;
        try {
            session = getSession();
            UserManager um = ((JackrabbitSession) session).getUserManager();
            Group adminGrp = (Group) um.getAuthorizable("Administrators");
            return adminGrp.isMember(um.getAuthorizable(username));
        } catch (Exception e) {
            logger.error("error checking is admin user", e);
        }
        return false;
    }

    protected final ConcurrentHashMap<Long, TenantRepositoryCache> getRepositories() {
        return repositories;
    }

  /*
   * 
   * OLD Methods
   */

    protected final void registerNodeTypes(final Session session)
            throws ContentRepositoryException {
        if (session == null) {
            return;
        }
        try {
            NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
            boolean alreadyRegistered = false;
            NodeTypeIterator iter = ntm.getMixinNodeTypes();
            while (iter.hasNext()) {
                if (iter.nextNodeType().getName().equals(DELETED_MIXIN)) {
                    alreadyRegistered = true;
                    break;
                }
            }
            if (!alreadyRegistered) {
                final InputStream resourceStream =
                        getClass().getClassLoader().getResourceAsStream("edifecs.cnd");
                if (resourceStream == null) {
                    throw new ContentRepositoryException(
                            "Could not load the 'edifecs.cnd'"
                                    + " Content Node Definition file. Ensure that this file exists.");
                }
                logger
                        .debug("Registering Edifecs JCR extensions from 'edifecs.cnd'...");
                for (javax.jcr.nodetype.NodeType type : CndImporter.registerNodeTypes(
                        new InputStreamReader(resourceStream), session)) {
                    logger.debug("Registered: " + type.getName());
                }
            }
        } catch (RepositoryException | ParseException | IOException ex) {
            ex.printStackTrace();
            throw new ContentRepositoryException(ex);
        }
    }

    protected VersionManager getVersionManager() throws ContentRepositoryException {
        try {
            return getSession().getWorkspace().getVersionManager();
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    final String getStringValue(final Property property)
            throws RepositoryException {
        if (property.getType() == PropertyType.BINARY) {
            return "[binary]";
        } else {
            return property.getString();
        }
    }

    @Override
    public final String dumpNode(final String path)
            throws ContentRepositoryException {
        try {
            return dumpNode(nodeByUserPath(path));
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
    }

    /**
     * Recursively outputs the contents of the given node.
     *
     * @throws ContentRepositoryException
     */
    public final String dumpNode(final Node node) throws RepositoryException,
            ContentRepositoryException {

        // First output the node path
        StringBuilder output = new StringBuilder();

        output.append(node.getPath());

        if (node.hasProperties()) {
            output.append("(");
        }

        // Then output the properties
        PropertyIterator properties = node.getProperties();
        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            if (property.getDefinition().isMultiple()) {
                output.append(property.getName() + " = [");
                // A multi-valued property, print all values
                Value[] values = property.getValues();
                for (int i = 0; i < values.length; i++) {
                    output.append(values[i].getString());
                    if (i - 1 < values.length) {
                        output.append(", ");
                    }
                }
                output.append(property.getName() + "]");
            } else {
                // A single-valued property
                output.append(property.getName() + " = " + getStringValue(property));
                if (properties.hasNext()) {
                    output.append(", ");
                }
            }
        }
        if (node.hasProperties()) {
            output.append(")");
        }
        // Versions
        // print version history

        if (isFile(node)) {
            try {
                VersionHistory history =
                        getVersionManager().getVersionHistory(node.getPath());
                output.append(" Versions=");
                output.append(history.getAllVersions().getSize());
            } catch (RepositoryException ex) {
                ex.printStackTrace();
                // ignore
            }
            output.append("\r\n");
        }

        // Finally output all the child nodes recursively
        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            output.append(dumpNode(nodes.nextNode()));
        }

        return output.toString();
    }

    @Override
    public final boolean folderExists(final String path)
            throws ContentRepositoryException {
        try {
            Node node = getSession().getNode(path);

            if (node.isNode() && !isDeleted(node)) {
                return true;
            }
        } catch (RepositoryException e) {
            return false;
        }
        return false;
    }

    private boolean isFile(final Node n) throws ContentRepositoryException {
        try {
            return (n.hasProperties() && n.hasProperty(JCR_PRIMARY_TYPE) && NT_FILE
                    .equals(n.getProperty(JCR_PRIMARY_TYPE).getString()));
        } catch (RepositoryException ex) {
            throw new ContentRepositoryException(ex);
        }
    }

    private boolean isFolder(final Node n) throws ContentRepositoryException {
        try {
            return (n.hasProperties() && n.hasProperty(JCR_PRIMARY_TYPE) && NT_FOLDER
                    .equals(n.getProperty(JCR_PRIMARY_TYPE).getString()));
        } catch (RepositoryException ex) {
            throw new ContentRepositoryException(ex);
        }
    }

    protected boolean isDeleted(final Node n) throws ContentRepositoryException {
        try {
            return (n.hasProperties() && n.hasProperty(DELETED_PROPERTY));
        } catch (RepositoryException ex) {
            throw new ContentRepositoryException(ex);
        }
    }

    protected void markDeleted(final Node n) throws ContentRepositoryException {
        try {
            final VersionManager vm = getVersionManager();
            vm.checkout(n.getPath());
            boolean hasMixin = false;
            for (javax.jcr.nodetype.NodeType mixin : n.getMixinNodeTypes()) {
                if (mixin.getName().equals(DELETED_MIXIN)) {
                    hasMixin = true;
                    break;
                }
            }
            if (!hasMixin) {
                n.addMixin(DELETED_MIXIN);
            }
            n.setProperty(DELETED_PROPERTY, true);
            getSession().save();
            vm.checkin(n.getPath());
        } catch (RepositoryException ex) {
            throw new ContentRepositoryException(ex);
        }
    }

    private void markNotDeleted(final Node n) throws ContentRepositoryException {
        try {
            final Property property = n.getProperty(DELETED_PROPERTY);
            if (property != null) {
                final VersionManager vm = getVersionManager();
                vm.checkout(n.getPath());
                property.remove();
                getSession().save();
                vm.checkin(n.getPath());
            }
        } catch (RepositoryException ex) {
            throw new ContentRepositoryException(ex);
        }
    }

    /**
     * Getters & Setters **
     */
    public Logger getLogger() {
        return logger;
    }

}
