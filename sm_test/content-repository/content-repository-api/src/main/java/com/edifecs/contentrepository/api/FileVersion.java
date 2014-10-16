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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Provides detailed information about a specific version of a specific node in
 * a content repository. FileProperties objects do not contain any kind of
 * reference to the exact node that they represent; they are simply a convenient
 * way to store extra node information retrieved from the repository.
 * </p>
 */
public final class FileVersion implements Serializable, Comparable<FileVersion> {

    private static final long serialVersionUID = 1;

    private final Date        dateUpdated;
    private final String      filename;
    private final long        fileSize;
    private final String      lastEditor;
    private final String      version;

    /**
     * <p>
     * Creates a new FileProperties object.
     * </p>
     * 
     * @param filename The name (last path component) of the node.
     * @param version The version of the node that this properties object
     *            represents.
     * @param lastEditor The name of the user who uploaded/edited this version
     *            of the node, or <code>null</code> if the username is unknown.
     * @param fileSize The size on disk, in bytes, of this version of this node.
     * @param dateUpdated The date that this version of the node was
     *            uploaded/edited.
     */
    public FileVersion(
            final String filename,
            final String version,
            final String lastEditor,
            final Long fileSize,
            final Date dateUpdated) {
        this.filename = filename;
        this.version = version;
        this.lastEditor = lastEditor;
        this.fileSize = fileSize;
        this.dateUpdated = dateUpdated;
    }

    @Override
    public int compareTo(final FileVersion o) {
        return getVersion().compareTo(o.getVersion());
    }

    /**
     * <p>
     * Returns the date at which this version of this node was edited or
     * uploaded.
     * </p>
     */
    public Date getDateUpdated() {
        return dateUpdated;
    }

    /**
     * <p>
     * Returns the size on disk of this version of the node, in bytes.
     * </p>
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * <p>
     * Returns the name of the user that edited or uploaded this version of the
     * node, or <code>null</code> if the username is unknown.
     * </p>
     */
    public String getLastEditor() {
        return lastEditor;
    }

    /**
     * <p>
     * Returns the name (last path component) of the node.
     * </p>
     */
    public String getName() {
        return filename;
    }

    /**
     * <p>
     * Returns the version of this revision of the node.
     * </p>
     */
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "{name=" + filename + ", version=" + version + ", lastEditor=" + lastEditor + ", fileSize=" + fileSize
                + ", dateUpdated=" + dateUpdated + "}";
    }
}
