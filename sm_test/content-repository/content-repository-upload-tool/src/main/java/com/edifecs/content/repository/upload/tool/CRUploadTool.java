package com.edifecs.content.repository.upload.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.content.repository.upload.tool.helpers.CRConnection;
import com.edifecs.content.repository.upload.tool.helpers.CRConnectionException;
import com.edifecs.content.repository.upload.tool.helpers.ConfigParameters;

/**
 * Responsible for storing the given files or folders in the Content Repository
 * and deleting from the local machine.
 * 
 * @author ashipras
 */
public class CRUploadTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final int NUM_TRIALS = 5;
	private static final int THREAD_SLEEP_TIME = 10 * 1000;

	private final CRConnection connection;
	private final String crUploadDir;
	private final String uploadedList;
	
	public CRUploadTool(Properties prop) throws Exception {
		connection = new CRConnection(prop);
		crUploadDir = ConfigParameters.getContentRepoUploadPath(prop);
		uploadedList = ConfigParameters.getUploadedFilesList(prop);
	}
	
	/**
	 * Uploads added files or folders in the directories.
	 * 
	 * @param file
	 *          File that is to be inserted into Content Repository.
	 * @param crPath
	 *          Content Repository Path where file is to be uploaded.
	 * @param delete
	 * 			Whether to delete the parent directory.
	 *            
	 * @throws Exception
	 * 
	 */
	public void upload(File file, String crPath, boolean delete) throws Exception {
		if (file.isDirectory()) {
			Thread.sleep(THREAD_SLEEP_TIME);
			uploadFolder(file, crPath, delete);
		} else {			
			uploadFile(file, crPath, true);
		}
	}
	
	/**
	 * Uploads added files or folders in the directories.
	 * 
	 * @param file
	 *          File that is to be inserted into Content Repository.
	 * @param delete
	 * 			Whether to delete the parent directory
	 *            
	 * @throws Exception
	 * 
	 */
	public void upload(File file, boolean delete) throws Exception {
		if (file.isDirectory()) {
			Thread.sleep(THREAD_SLEEP_TIME);
			uploadFolder(file, crUploadDir, delete);
		} else {			
			uploadFile(file, crUploadDir, true);
		}
	}
	
	private void uploadFile(File file, String path, boolean delete) throws Exception {
		boolean uploaded = false;
		
		connection.createDirectoryIfNotExists(path);
		
		for (int i = 0; i < NUM_TRIALS; i++) {
			try (InputStream in = new FileInputStream(file)) {
				connection.uploadFile(in, path, file.getName());
				uploaded = true;
				break;
			} catch (FileNotFoundException ex) {
				Thread.sleep(1000);
			} catch (IOException ex) {
				Thread.sleep(1000);
			} catch (CRConnectionException e) {
				e.printStackTrace();
			}
		}
		
		if (!uploaded) {
			throw new Exception(file + " could not be uploaded to " + path);
		}

		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(
					new File(uploadedList), true));
			pw.printf("File: %s uploaded to %s.\n", file.getName(), path);

			pw.close();
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage());
		}
		
		if (delete) {
			logger.info(file.getName() + " Deleted ? " + file.delete());
		}
	}
	
	private void uploadFolder(File folder, String path, boolean delete) throws Exception {
		
		path += folder.getName() + "/";
				
		connection.createDirectoryIfNotExists(path);
		logger.info("Content Repository: {} created." + folder.getName());
		
		File[] filelist = folder.listFiles();

		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {
				uploadFolder(filelist[i], path, true);
			} else {
				logger.info("File: " + filelist[i].getName());
				uploadFile(filelist[i], path, true);
			}
		}
		
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(
					new File(uploadedList), true));
			pw.printf("Folder: %s uploaded to %s.\n", folder.getName(), path);

			pw.close();
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage());
		}

		if (delete) {
			logger.info("Folder: " + folder.getName() + " Deleted ? " + folder.delete());
		}
	}

	public void stopService() {
		connection.disconnect();
	}
	
	public static int getNumTrials() {
		return NUM_TRIALS;
	}

	public static int getThreadSleepTime() {
		return THREAD_SLEEP_TIME;
	}

	public CRConnection getConnection() {
		return connection;
	}

	public String getCRUploadDir() {
		return crUploadDir;
	}

	public String getUploadedList() {
		return uploadedList;
	}

	public Logger getLogger() {
		return logger;
	}
}
