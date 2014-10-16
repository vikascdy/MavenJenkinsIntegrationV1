package com.edifecs.epp.isc.exception;

public class NoClusterNameConfiguredException extends ClusterBuilderException {
	private static final long serialVersionUID = 1L;

	public NoClusterNameConfiguredException() {
		super(
				"No cluster name has been configured for this cluster.\r\n"
						+ "    If you are programatically connecting to the cluster, please verify that a valid clustername is being provided.\r\n"
						+ "    If you are using the application platform, please make sure a valid cluster.name has been added to the config.properties file.");
	}

	public NoClusterNameConfiguredException(String string) {
		super(string);
	}

	public NoClusterNameConfiguredException(Exception e) {
		super(e);
	}

	public NoClusterNameConfiguredException(String string, Exception e) {
		super(string, e);
	}
}
