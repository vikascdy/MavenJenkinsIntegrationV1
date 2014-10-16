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
import java.util.Arrays;

/**
 * <p>
 * A path to a node in a content repository. While repository paths are, by
 * default, represented by UNIX-style strings of slash-separated names (ex:
 * <code>"/folder/subfolder/file.ext"</code>), using NodePath objects simplifies
 * dealing with these paths and ensures that they conform to the expected
 * format.
 * </p>
 */
public class NodePath implements Serializable {

    private static final long    serialVersionUID = 1;

    private static final int     HASH_CODE_SALT   = 91;

    /**
     * <p>
     * The symbol used to split path strings into components. By default, it is
     * a slash (<code>/</code>), as in UNIX-like filesystems.
     * </p>
     */
    public static final String   SEPARATOR        = "/";

    /**
     * <p>
     * The root path of the repository.
     * </p>
     */
    public static final NodePath ROOT             = new NodePath();

    /**
     * <p>
     * Creates a new {@link NodePath} from a slash-separated path string (ex:
     * <code>"/folder/subfolder/file.ext"</code>).
     * </p>
     * 
     * @param path The path string to convert.
     * @return A new NodePath object representing the same path as the original
     *         string.
     */
    public static final NodePath fromString(String path) {
        if (path.startsWith(SEPARATOR)) {
            path = path.substring(SEPARATOR.length());
        }
        if (path.length() == 0) {
            return ROOT;
        }
        return new NodePath(path.split("[" + SEPARATOR + "]"));
    }

    private final String[] path;

    /**
     * <p>
     * Creates a new {@link NodePath} from a list of node names.
     * </p>
     * 
     * @param components The components of the path, starting with the highest
     *            parent folder and moving downward.
     */
    public NodePath(String... components) {
        path = components;
    }

    /**
     * <p>
     * Returns the last component of this path, the name of the node itself. May
     * return <code>null</code> if this path is the root path (<code>/</code>).
     * </p>
     */
    public String getFilename() {
        return path.length > 0 ? path[path.length - 1] : null;
    }

    /**
     * <p>
     * Returns an array of the components of the path; basically the same as
     * using {@link String#split(String)} to split the string representation of
     * the path by {@link #SEPARATOR}.
     * </p>
     */
    public String[] getComponents() {
        String[] copy = new String[length()];
        System.arraycopy(path, 0, copy, 0, length());
        return copy;
    }

    /**
     * <p>
     * Returns a specific component of the path. Path components are numbered
     * starting at 0, where 0 is the highest-level directory in the path (i.e.,
     * the beginning of the path string). Essentially the same as
     * <code>getComponents()[index]</code>.
     * </p>
     * 
     * @param index The index of the component to retrieve.
     * @return The component at the given index.
     */
    public String getComponent(int index) {
        return path[index];
    }

    /**
     * <p>
     * Returns the number of components in the path.
     * </p>
     */
    public int length() {
        return path.length;
    }

    /**
     * <p>
     * Returns a NodePath representing this path's parent directory. If this
     * path is the root path, returns this path.
     * </p>
     */
    public NodePath up() {
        if (path.length > 0) {
            String[] newPath = new String[path.length - 1];
            System.arraycopy(path, 0, newPath, 0, path.length - 1);
            return new NodePath(newPath);
        } else {
            return this;
        }
    }

    /**
     * <p>
     * Returns a NodePath representing a child node of this path with the given
     * name.
     * </p>
     * 
     * @param next The name of the child node to return a path to.
     */
    public NodePath down(String next) {
        String[] newPath = new String[path.length + 1];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[path.length] = next;
        return new NodePath(newPath);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(path) + HASH_CODE_SALT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NodePath other = (NodePath) obj;
        return Arrays.equals(path, other.path);
    }

    @Override
    public String toString() {
        if (path.length == 0) {
            return SEPARATOR;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String p : path) {
                sb.append(SEPARATOR);
                sb.append(p);
            }
            return sb.toString();
        }
    }
}
