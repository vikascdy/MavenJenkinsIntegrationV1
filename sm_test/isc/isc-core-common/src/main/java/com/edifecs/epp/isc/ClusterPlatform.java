package com.edifecs.epp.isc;

import static scala.collection.JavaConversions.asScalaIterable;
import static java.util.Arrays.asList;

/**
 * The types of platforms that a cluster can run on. Different platforms may
 * require different node discovery and/or communication strategies.
 *
 * The {@link #get()} method can be used to retrieve the platform that the
 * current cluster is running on, based on the system property
 * `cluster.platform`.
 *
 * @author c-adamnels
 */
public enum ClusterPlatform {

    /**
     * A node running on the bare metal (no virtualization). Uses UDP multicast
     * for node discovery. This is the default platform if `cluster.platform`
     * is not specified.
     */
    NATIVE,

    /**
     * A node running in an ECM+Docker environment. Uses etcd for node
     * discovery.
     */
    ECM;

    public static final String PROPERTY_NAME = "cluster.platform";

    private static ClusterPlatform current = null;

    /**
     * Returns a `ClusterPlatform` instance representing the platform that this
     * JVM node is running on, based on the system property `cluster.platform`.
     */
    public static synchronized ClusterPlatform get() {
        if (current != null) return current;
        final String property = System.getProperty(PROPERTY_NAME, NATIVE.name());
        for (ClusterPlatform platform : values()) {
            if (property.equalsIgnoreCase(platform.name())) {
                current = platform;
                return platform;
            }
        }
        throw new IllegalStateException("'" + property + "' is not a valid value for the" +
            " property '" + PROPERTY_NAME + "'. Supported values are (case-insensitively): " +
            asScalaIterable(asList(values())).mkString(", ") + "");
    }

    @Override public String toString() {
        return name().toLowerCase();
    }
}
