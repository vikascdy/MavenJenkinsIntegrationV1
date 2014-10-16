package com.edifecs.content.repository.upload;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.content.repository.upload.tool.CRUploadTool;
import com.edifecs.content.repository.upload.tool.helpers.ConfigParameters;

/**
 * CRDirectoryMonitor is an remote application that can be used to monitor a set
 * of directories specified in the setup.properties. This application allows us
 * to insert files, folder etc directly into the Content Repository. Once this
 * application is started, any file or folder inserted into these directories is
 * automatically inserted into Content Repository and removed from the folder.
 * Directories to monitor have to be created prior to running this application
 * 
 * @author ashipras
 */
public class CRDirectoryMonitor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	private static final long WATCH_POLL_TIMEOUT = 2;

	private WatchService watchService;
	private CRUploadTool uploadTool;
	private ConfigParameters configuration;

	private boolean notDone = true;
	private String[] directories;
	private Path path;

	public CRDirectoryMonitor() throws Exception {
		try {
			configuration = new ConfigParameters();
		} catch (Exception e) {
			throw new Exception("Error in initializing config parameters", e);
		}

		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (Exception e) {
			throw new Exception("FileSystem does not support watching "
					+ "file system objects for changes and events", e);
		}
	}
	
	public CRDirectoryMonitor(String[] args) throws Exception {
		
		this();
		
		for (String arg: args) {
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

	public void startMonitoring() throws IOException {
		directoriesToMonitor(configuration.getProperties(), watchService);

		for (String dir : directories) {
			File path = new File(dir);

			try {
				logger.info("Uploading Existing Files from " + dir);
				for (File existingFile : path.listFiles()) {
					uploadTool.upload(existingFile, true);
				}
			} catch (FileNotFoundException e) {
				logger.error("Can't find the files specified in " + dir);
			} catch (InterruptedException e) {
				logger.error("Thread is interrupted, either before or during the activity.");
			} catch (Exception e) {
				logger.error("Exception occured during upload from " + dir);
			}
		}

		while (notDone) {

			try {
				WatchKey watchKey;
				do {
					watchKey = watchService.poll(WATCH_POLL_TIMEOUT,
							TimeUnit.SECONDS);
				} while (watchKey == null);

				for (WatchEvent<?> event : watchKey.pollEvents()) {

					Path watchedPath = (Path) watchKey.watchable();
					WatchEvent.Kind<?> eventKind = event.kind();
					if (eventKind.equals(ENTRY_CREATE)) {
						Path target = (Path) event.context();

						File file = new File(watchedPath + File.separator
								+ target);
						uploadTool.upload(file, true);
					}
				}
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void stopMonitoring() {
		notDone = false;
	}
	
	@Override
	protected void finalize() throws Throwable {
		uploadTool.stopService();
	}

	private void directoriesToMonitor(Properties prop, WatchService watchService)
			throws IOException {
		directories = ConfigParameters.getMonitoringDir(prop);

		for (String dir : directories) {
			File dirToMonitor = new File(dir);

			try {
				if (!dirToMonitor.exists()) {
					throw new Exception("Directory to Monitor does not exist");
				}

				path = Paths.get(dir);
				path.register(watchService, ENTRY_CREATE);
				logger.info(path + " added to monitored directories.");
			} catch (Exception e) {
				logger.error("Unable to find the path", e);
			}
		}
	}

	private void setProperty(String name, String value) {
		configuration.getProperties().setProperty(name, value);
	}

	public static void main(String[] args) throws Exception {
		CRDirectoryMonitor folderMonitor = new CRDirectoryMonitor(args);
		folderMonitor.initializeUploadTool();
		folderMonitor.startMonitoring();
	}
}
