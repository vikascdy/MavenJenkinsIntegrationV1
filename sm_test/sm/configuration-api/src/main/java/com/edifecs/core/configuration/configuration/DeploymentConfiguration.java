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
package com.edifecs.core.configuration.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Root of the configuration file. This stores information about the
 * configuration file such as creation date.
 * 
 * @author willclem
 */
@XmlRootElement(name = "DeploymentConfiguration")
public class DeploymentConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date              createdDate;

    private String            name;

    private String            version;

    private String            description;

    private List<Cluster>     clusters;

    /**
     * Gets the date that the configuration file was created.
     * 
     * @return CreatedDate
     */
    @XmlElement(name = "CreatedDate")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public final Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the date that the configuration file was created.
     * 
     * @param newCreatedDate CreatedDate
     */
    public final void setCreatedDate(final Date newCreatedDate) {
        this.createdDate = newCreatedDate;
    }

    /**
     * Gets the name of the configuration.
     * 
     * @return Name
     */
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the configuration.
     * 
     * @param newName Name
     */
    public final void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Gets the version of the configuration file.
     * 
     * @return Version
     */
    @XmlElement(name = "Version")
    public final String getVersion() {
        return version;
    }

    /**
     * Sets the version of the configuration file.
     * 
     * @param newVersion Version
     */
    public final void setVersion(final String newVersion) {
        this.version = newVersion;
    }

    /**
     * Gets the desctiption of the configuration file.
     * 
     * @return Description
     */
    @XmlElement(name = "Description")
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the configuration file.
     * 
     * @param newDescription Description
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Gets the list of clusters within the configuration file.
     * 
     * @return List of Clusters
     */
    @XmlElement(name = "Cluster")
    public final List<Cluster> getClusters() {
        if (clusters == null) {
            clusters = new ArrayList<Cluster>();
        }
        return clusters;
    }

    /**
     * Sets the list of clusters within the configuration file.
     * 
     * @param newClusters List of Clusters
     */
    public final void setClusters(final List<Cluster> newClusters) {
        this.clusters = newClusters;
    }

    /**
     * Gets the cluster within the configuration file with the name supplied.
     * 
     * @param clusterName ClusterName
     * @return The Cluster with the supplied clusterName. Returns null if no
     *         cluster exists.
     */
    public final Cluster getClusterByName(final String clusterName) {
        for (Cluster cluster : getClusters()) {
            if (cluster.getName().equals(clusterName)) {
                return cluster;
            }
        }
        return null;
    }
}
