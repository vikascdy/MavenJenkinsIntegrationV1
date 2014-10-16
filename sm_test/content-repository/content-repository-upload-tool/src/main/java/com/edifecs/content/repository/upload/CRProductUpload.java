package com.edifecs.content.repository.upload;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.edifecs.content.repository.upload.tool.CRProductUploadTool;
import com.edifecs.content.repository.upload.tool.helpers.ConfigParameters;

public class CRProductUpload {

	private CRProductUploadTool uploadTool;
	private ConfigParameters configuration;

	public void defaultInitialize() throws Exception {
		try {
			configuration = new ConfigParameters();
			uploadTool = new CRProductUploadTool(configuration.getProperties());
		} catch (Exception e) {
			throw new Exception("Error in initializing config parameters", e);
		}
	}

	public void shutdown() {
		uploadTool.stopService();
	}

	public void install(String path)
			throws Exception {

		File productDir = new File(path);

		if (!productDir.exists()) {
			throw new Exception(path + " doesn't exists.");
		}

		if (!productDir.getName().endsWith(".zip")) {
			throw new Exception(path + " is not a zip file.");
		}

		System.out.println("Uploading package => " + productDir.getName());
		uploadTool.uploadProductPackage(productDir, false);
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		CRProductUpload app = new CRProductUpload();
		try {
			Options options = new Options();
			
			options.addOption("help", false, "Help Message");
			
			Option option = new Option("install", false, "Install the solution package into the Content Repository");
			
			options.addOption(option);
			
			options.addOption(OptionBuilder.withLongOpt("path").withDescription("Path to the solution package.")
							.hasArg().withArgName("PATH").create());
			
			
			CommandLineParser parser = new BasicParser();
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "ant", options );
			} else if (cmd.hasOption("install")) {
				String path = cmd.getOptionValue("path");
				
				app.defaultInitialize();
				
				app.install(path);
				app.shutdown();
			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "ant", options );
			}
	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(1);
		}
	}
}
