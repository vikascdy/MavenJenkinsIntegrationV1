package com.edifecs.content.repository.upload.tool.helpers;

import java.io.InputStream;
import java.util.Properties;

import com.edifecs.epp.isc.CommandCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.epp.isc.Args;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;

public class CRConnection {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	private final CommandCommunicator commandCommunicator;

	private Address contentRepoAddr;
	
	public CRConnection(Properties prop) throws CRConnectionException, ServiceTypeNotFoundException {
		try {
			Address localAddress = new Address(
					ConfigParameters.getServerName(prop),
					ConfigParameters.getNodeName(prop));

			CommandCommunicatorBuilder builder = new CommandCommunicatorBuilder();
            builder.setClusterName(ConfigParameters.getClusterName(prop));
            builder.setAddress(localAddress);
            
            commandCommunicator = builder.initialize();
            commandCommunicator.connect();
		} catch (Exception e) {
			throw new CRConnectionException("Error in creating service", e);
		}

		contentRepoAddr = getContentRepoAddress(
				ConfigParameters.getContentRepoServiceName(prop), null);

	}

	public void uploadFile(InputStream stream, String path, String filename)
			throws CRConnectionException {
		try {
			commandCommunicator.sendSyncMessage(contentRepoAddr,
			        "addFile",
			            "inputStream", stream, new Args(
					    "path",        path,
					    "filename",    filename));
		} catch (Exception e) {
			throw new CRConnectionException("Unable to transfer file : "
					+ filename, e);
		}
	}
	
	public void createDirectoryIfNotExists(String path)
			throws CRConnectionException {
		
		String[] nodes = path.split("/");
		String cur = "/";
		
		try {			
			for (int i = 1; i < nodes.length; i++) {
				cur += nodes[i] + "/";
				commandCommunicator.sendSyncMessage(contentRepoAddr, "createFolder",
				        new Args("path", cur));
			}
		} catch (Exception e) {
			throw new CRConnectionException("Unable to create folder : " + cur, e);
		}
	}

	public void disconnect() {
		commandCommunicator.disconnect();
	}
	
	private Address getContentRepoAddress(String serviceType, String serviceName)
			throws ServiceTypeNotFoundException {

		Address address = commandCommunicator.getAddressRegistry()
				.getAddressForServiceTypeName(serviceType);

		if (null != serviceName) {
			if (address.getService().equals(serviceName)) {
				logger.info("Using address : " + address);
				return address;
			}
		}

		logger.info("Using default address, service name not specified."
				+ address);

		return address;
	}
}
