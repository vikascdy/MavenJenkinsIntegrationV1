package com.edifecs.content.repository.upload;

import java.io.File;

import com.edifecs.content.repository.upload.tool.CRUploadTool;
import com.edifecs.content.repository.upload.tool.helpers.ConfigParameters;

public class CRDirectoryUpload {
	private CRUploadTool uploadTool;
	private ConfigParameters configuration;

	public CRDirectoryUpload() throws Exception {
		try {
			configuration = new ConfigParameters();
		} catch (Exception e) {
			throw new Exception("Error in initializing config parameters", e);
		}
	}

	public CRDirectoryUpload(String[] args) throws Exception {

		this();

		for (String arg : args) {
			String[] property = arg.split("=");

			if (property.length != 2) {
				throw new Exception("Bad Argument: " + arg
								+ "\nArguments must be of the form : <property_name>=<property_value>");
			}

			setProperty(property[0], property[1]);
		}
	}

	public void initializeUploadTool() throws Exception {
		uploadTool = new CRUploadTool(configuration.getProperties());
	}

	public void upload() throws Exception {
		String[] directories = ConfigParameters.getDirToBeUploaded(configuration
				.getProperties());

		for (String dir : directories) {
			File dirToUpload = new File(dir);
			
			if (!dirToUpload.exists()) {
				throw new Exception(dir + " doesn't exists");
			}
			
			for (File file : dirToUpload.listFiles()) {
				uploadTool.upload(file, true);
			}
		}
	}
	
	public void shutdown() {
		uploadTool.stopService();
	}

	private void setProperty(String name, String value) {
		configuration.getProperties().setProperty(name, value);
	}

	public static void main(String[] args) throws Exception {
		CRDirectoryUpload folderUpload = new CRDirectoryUpload(args);
		
		folderUpload.initializeUploadTool();
		folderUpload.upload();
		folderUpload.shutdown();
		
		System.exit(0);
	}
}
