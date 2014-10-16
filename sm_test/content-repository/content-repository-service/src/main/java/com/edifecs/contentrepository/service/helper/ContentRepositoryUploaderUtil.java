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

package com.edifecs.contentrepository.service.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.contentrepository.api.IContentRepository;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.core.configuration.helper.SystemVariables;

/**
 * Utility class to auto-upload artifacts into Content Repository at startup.
 * 
 * @author VioricaA
 */
public final class ContentRepositoryUploaderUtil {
    private static final Logger logger = LoggerFactory.getLogger(ContentRepositoryUploaderUtil.class);
    
    private static IContentRepository contentRepository;

    private static final String       LOCAL_ARTIFACTS_FOLDER = SystemVariables.SERVICE_MANAGER_ROOT_PATH + "artifacts";

    /**
     * Default constructor intentionally left blank for utility class.
     */
    private ContentRepositoryUploaderUtil() {
        // Auto-generated constructor stub
    }

    /**
     * @param localContentRepository
     * @throws ContentRepositoryException
     * @throws IOException
     */
    public static void uploadFilesToCR(IContentRepository localContentRepository) throws ContentRepositoryException, IOException {
        new File(SystemVariables.APPS_PATH).mkdirs();
        
        contentRepository = localContentRepository;

        uploadCore();

        uploadArtifacts();
    }

    /**
     * Uploads the artifacts found in the Artifacts folder.
     * 
     * @throws IOException
     * @throws ContentRepositoryException
     */
    private static void uploadArtifacts() throws IOException, ContentRepositoryException {
        
        // Copy all Artifacts from all product and component sub folders into the artifacts folder.
        uploadArtifacts(SystemVariables.APPS_PATH);

        // Checks if the Artifacts folder exists
        if (new File(LOCAL_ARTIFACTS_FOLDER).exists()) {
            Path artifacts = new File(LOCAL_ARTIFACTS_FOLDER).toPath();
            DirectoryStream<Path> stream = Files.newDirectoryStream(artifacts);
            for (Path entry : stream) {
                File entryF = entry.toFile();
                if (entryF.isDirectory()) {
                    if (!contentRepository.folderExists(SystemVariables.CONTENT_REPOSITORY_ARTIFACTS)) {
                        logger.debug("Generating Artifacts folder into Contenet Repository");
                        contentRepository.createFolder(SystemVariables.CONTENT_REPOSITORY_ARTIFACTS);
                    }
                    contentRepository.addDirectory(SystemVariables.CONTENT_REPOSITORY_ARTIFACTS, entryF);
                    logger.debug("Uploaded " + entry.toString() + " folder to " + SystemVariables.CONTENT_REPOSITORY_ARTIFACTS + entryF.getName() + " to Content Repository");
                } else {
                    contentRepository.addFile(SystemVariables.CONTENT_REPOSITORY_ARTIFACTS, entryF);
                    logger.debug("Uploaded " + entry.toString() + " file to " + SystemVariables.CONTENT_REPOSITORY_ARTIFACTS + " to Content Repository");
                }
            }
        } else {
            logger.debug("Artifacts folder doesn't exist");
        }
        
    }
    
    private static void uploadArtifacts(String path) throws IOException, ContentRepositoryException {
        Path apps = new File(path).toPath();
        DirectoryStream<Path> stream = Files.newDirectoryStream(apps);
        for (Path entry : stream) {
            File entryF = entry.toFile();
            entryF = new File(entryF, SystemVariables.ARTIFACTS_FOLDER_NAME);
            if (entryF.exists() && entryF.isDirectory()) {
                contentRepository.addDirectory(
                        SystemVariables.CONTENT_REPOSITORY_ROOT_PATH,
                        entryF.getAbsolutePath());
            }
        }
    }

    /**
     * Uploads the core of servicemanager to Content Repository.
     * 
     * @throws ContentRepositoryException
     */
    private static void uploadCore() throws ContentRepositoryException {
        // Create folder in case there is no folder already in the JCR
        contentRepository.createFolder(SystemVariables.CONTENT_REPOSITORY_SERVICE_MANAGER);

        // Add all files found within the following directories to the JCR
        contentRepository.addDirectory(
                SystemVariables.CONTENT_REPOSITORY_ROOT_PATH,
                SystemVariables.PLATFORM_PATH);
        contentRepository.addDirectory(
                SystemVariables.CONTENT_REPOSITORY_ROOT_PATH,
                SystemVariables.APPS_PATH);
        contentRepository.addDirectory(
                SystemVariables.CONTENT_REPOSITORY_ROOT_PATH,
                SystemVariables.CONFIGURATION_PATH);
        logger.debug("Service Manager core uploaded");
    }
}
