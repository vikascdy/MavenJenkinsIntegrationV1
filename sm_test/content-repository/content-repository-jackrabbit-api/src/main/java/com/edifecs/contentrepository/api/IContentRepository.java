// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.contentrepository.api;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.epp.security.data.Tenant;

public interface IContentRepository {

    /**
     * Adds a file to the JCR repository.
     * 
     * @param path
     * @param filename
     * @param file
     * @throws ContentRepositoryException
     */
    void addFile(String path, String filename, byte[] file) throws ContentRepositoryException;

    void addFile(String path, File file) throws ContentRepositoryException;

    void addFile(String path, String filename, InputStream file) throws ContentRepositoryException;

    /**
     * Updates the version of the file
     * 
     * @param path
     * @param filename
     * @param file Input stream to file
     * @throws ContentRepositoryException
     */
    void updateFile(String path, String filename, InputStream file) throws ContentRepositoryException;

    void updateFile(String path, String filename, byte[] file) throws ContentRepositoryException;

    void updateFile(String path, File file) throws ContentRepositoryException;

    /**
     * Adds an entire directory on the local machine too the JCR.
     * 
     * @param repositoryPath
     * @param localPath
     * @throws ContentRepositoryException
     */
    void addDirectory(String repositoryPath, String localPath) throws ContentRepositoryException;

    void addDirectory(String repositoryPath, File localPath) throws ContentRepositoryException;

    /**
     * Deletes a file from the repository.
     * 
     * @param path
     * @throws ContentRepositoryException
     */
    void deleteFile(String path) throws ContentRepositoryException;

    void deleteFile(String path, String filename) throws ContentRepositoryException;
    
    void deleteNodePermanently(String path) throws ContentRepositoryException;

    /**
     * Returns a file from the repository.
     * 
     * @param path
     * @return
     * @throws ContentRepositoryException
     */
    InputStream getFile(String path) throws ContentRepositoryException;

    InputStream getFile(String path, String filename) throws ContentRepositoryException;

    InputStream getFile(String path, String filename, String version) throws ContentRepositoryException;

    ContentNode getNode(String path) throws ContentRepositoryException;

    void moveNode(String srcPath, String destPath) throws ContentRepositoryException;

    void copyNode(String srcPath, String destPath) throws ContentRepositoryException;

    /**
     * Returns a list of file and directories found within the path on the JCR.
     * 
     * @param path
     * @return
     * @throws ContentRepositoryException
     */
    List<ContentNode> viewFolder(String path) throws ContentRepositoryException;

    /**
     * Creates a new folder in the JCR.
     * 
     * @param path
     * @throws ContentRepositoryException
     */
    void createFolder(String path) throws ContentRepositoryException;

    /**
     * Deletes a folder and all child folders and files in the JCR repository.
     * 
     * @param path
     * @throws ContentRepositoryException
     */
    void deleteFolder(String path) throws ContentRepositoryException;

    /**
     * Returns a String of information about the JCR repository.
     * 
     * @return
     * @throws ContentRepositoryException
     */
    String getStatistics() throws ContentRepositoryException;

    Map<String, String> getProperties(String path) throws ContentRepositoryException;

    /**
     * Gets the history information about the specified location.
     * 
     * @param path
     * @return
     * @throws ContentRepositoryException
     */
    List<FileVersion> getHistory(String path) throws ContentRepositoryException;

    /**
     * Gets the history information about the specified location.
     * 
     * @param path
     * @param filename
     * @return
     * @throws ContentRepositoryException
     */
    List<FileVersion> getHistory(String path, String filename) throws ContentRepositoryException;

    /**
     * Shutsdown the JCR.
     */
    void shutdown() throws Exception;

    /**
     * Returns a string of the entire contents of the JCR repo.
     * 
     * @param path
     * @return
     * @throws ContentRepositoryException
     */
    String dumpNode(String path) throws ContentRepositoryException;

    /*
     * Methods to remove
     */

    boolean folderExists(String path) throws ContentRepositoryException;


    void setupTenantRepository(String repositoryPath, Tenant tenant)
            throws ContentRepositoryException;

    void addUserToTenantRepository(Tenant tenant, String username,
                                   String password, boolean admin) throws ContentRepositoryException;

    String shareContentWithUser(String path, String filename, String username)
            throws ContentRepositoryException;

    void search(String keyword) throws ContentRepositoryException;
}
