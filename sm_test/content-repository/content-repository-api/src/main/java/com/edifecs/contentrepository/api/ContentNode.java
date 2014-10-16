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

/**
 * <p>
 * Provides basic information about a single <i>node</i> (file, folder, or other
 * piece of data) stored in a content repository. Does not provide as much data
 * as a {@link FileVersion} object, but provides enough to determine the type of
 * node and what type of data it represents.
 * </p>
 */
public class ContentNode implements Serializable {

    private static final long serialVersionUID = 1;

    private final NodePath path;
    private final String   version;
    private final String   mimeType;
    private final boolean  directory;
    private final NodeType nodeType;

    /**
     * <p>
     * Creates a new ContentNode describing a particular node.
     * </p>
     * 
     * @param path The path that the node is located at in the repository.
     * @param version The version of the most recent revision of the node.
     * @param mimeType If the node represents a file, this should contain the
     *            MIME type of the file. If it does not represent a file, this
     *            should be {@code null}.
     * @param nodeType The type of node that this object represents (file,
     *            folder, etc.)
     */
    public ContentNode(NodePath path, String version, String mimeType,
            boolean directory, NodeType nodeType) {
        this.path = path;
        this.version = version;
        this.mimeType = mimeType;
        this.directory = directory;
        this.nodeType = nodeType;
    }

    /**
     * <p>
     * Returns the name of this node (the last element of its path).
     * </p>
     */
    public String getName() {
        return path.getFilename();
    }

    /**
     * <p>
     * Returns the version of the most recent revision of this node.
     * </p>
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>
     * <i>Used by the EXT JS client.</i>
     * </p>
     */
    public String getId() { // Used in EXT JS as a Node ID.
        return path.toString();
    }

    /**
     * <p>
     * Returns the path this node is located at in the repository.
     * </p>
     */
    public NodePath getPath() {
        return path;
    }

    /**
     * <p>
     * If this node represents a file, returns the MIME type of the file;
     * otherwise, returns {@code null}.
     * </p>
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * <p>
     * Returns the type of this node (file, folder, etc.).
     * </p>
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    public String getTypeName() {
        return nodeType.name().toLowerCase();
    }

    /**
     * <p>
     * Returns {@code true} if this node may contain child nodes.
     * </p>
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * <p>
     * <i>Used by the EXT JS client.</i>
     * </p>
     */
    public boolean isLeaf() { // For EXT JS tree compatibility.
        return !isDirectory();
    }

    @Override
    public String toString() {
        return "Node: " + getName() + "-" + getVersion();
    }
}
