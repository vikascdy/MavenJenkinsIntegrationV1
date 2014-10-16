package com.edifecs.content.repository.upload.tool;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.edifecs.content.repository.upload.tool.helpers.CRConnectionException;
import com.edifecs.content.repository.upload.tool.helpers.ConfigParameters;

/**
 * Responsible for uploading the product packages to Content Repository
 * 
 * Product Package Structure:
 * 	Zip File – Product Name and Version
 * 		Install.jar
 * 		Install.bat
 * 		Install.sh
 * 		
 * 		Products – Stores all product specific code
 * 			<Product Name>
 * 				Metadata.META
 * 				Bundles
 * 					<Required Bundles for the Product>
 * 				Artifacts
 * 					<Required Artifacts for the Product>
 * 
 * 		Artifacts – Stores all custom components
 * 			<Product Name>
 * 
 * @author ashipras
 */
public class CRProductUploadTool extends CRUploadTool {
	
	private static final String PRODUCTS = "products";
	private static final String ARTIFACTS = "artifacts";
	private static final String BUNDLES = "bundles";
	
	private static final String META_EXTENSION = ".META";
	
	private static final int BUFFER_SIZE = 1024;
	
	private final String crBundlePath;
	private final String crMetaPath;
	private final String crProductArtifactPath;
	private final String crArtifactPath;
	
	public CRProductUploadTool(Properties properties) throws Exception {
		super(properties);
		crBundlePath = ConfigParameters.getContentRepoBundlePath(properties);
		crMetaPath = ConfigParameters.getContentRepoMetaPath(properties);
		crProductArtifactPath = ConfigParameters.getContentRepoProductArtifactsPath(properties);
		crArtifactPath = ConfigParameters.getContentRepoArtifactsPath(properties);
	}
	
	/**
	 * Uploads product packages to content repository
	 * 
	 * @param file
	 *          Product Package that is to be inserted into Content Repository.
	 * @param delete
	 * 			Whether to delete the package directory
	 *            
	 * @throws Exception
	 * 
	 */
	public void uploadProductPackage(File file, boolean delete) throws Exception {
		String[] product = file.getName().replace(".zip", "").split(" ");
		
		if (2 > product.length) {
			throw new Exception("The product package should be named <Name> <Version>");
		}
		
		String name = new String(product[0]);
		
		for (int i = 1; i < product.length - 1; i++) {
			name += " " + product[i];
		}
		
		String version = product[product.length - 1];
		
		getLogger().info("Product Name: {}, Version: {}", name, version);
		
		String tempProductPath = file.getParent() + File.separator + "install_temp" + File.separator + name + " " + version;
		
		new File(tempProductPath).mkdirs();
				
		getLogger().info("Decompressing the zipped file {} to {}", file.getName(), tempProductPath);
		decompressFile(file.getCanonicalPath(), tempProductPath);
		
		File tempProduct = new File(tempProductPath);
		
		createCRFoldersIfNotExists();
		
		getLogger().info("Uploading the products " + tempProductPath + File.separator + PRODUCTS);
		uploadProducts(tempProductPath + File.separator + PRODUCTS);
		
		getLogger().info("Uploading the artifacts " + tempProductPath + File.separator + ARTIFACTS);
		uploadArtifacts(tempProductPath + File.separator + ARTIFACTS);
		
		getLogger().info("Deleting the decompressed file " + tempProduct.getParentFile().getName());
		delete(tempProduct.getParentFile());
		
		if (delete) {
			getLogger().info("Product: " + file.getName() + " Deleted ? " + file.delete());
		}
	}
	
	public void createCRFoldersIfNotExists() {
		try {
			getConnection().createDirectoryIfNotExists(crArtifactPath);
		} catch (CRConnectionException e) {
			getLogger().error(e.getMessage(), e);
		}
		
		try {
			getConnection().createDirectoryIfNotExists(crBundlePath);
		} catch (CRConnectionException e) {
			getLogger().error(e.getMessage(), e);
		}
		
		try {
			getConnection().createDirectoryIfNotExists(crMetaPath);
		} catch (CRConnectionException e) {
			getLogger().error(e.getMessage(), e);
		}
		
		try {
			getConnection().createDirectoryIfNotExists(crProductArtifactPath);
		} catch (CRConnectionException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
	    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	    byte[] bytesIn = new byte[BUFFER_SIZE];
	    int read = 0;
	    while ((read = zipIn.read(bytesIn)) != -1) {
	        bos.write(bytesIn, 0, read);
	    }
	    bos.close();
	}
	
	public void decompressFile(String zipFilePath, String destDirectory) throws IOException {
	    File destDir = new File(destDirectory);
	    if (!destDir.exists()) {
	        destDir.mkdir();
	    }
	    
	    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
	    
	    ZipEntry entry = zipIn.getNextEntry();
	    
	    while (entry != null) {
	        String filePath = destDirectory + File.separator + entry.getName();
	        if (!entry.isDirectory()) {
	            extractFile(zipIn, filePath);
	        } else {
	            File dir = new File(filePath);
	            dir.mkdir();
	        }
	        zipIn.closeEntry();
	        entry = zipIn.getNextEntry();
	    }
	    zipIn.close();
	}
	
	private void uploadProducts(String productPath) throws Exception {
		File product = new File(productPath);
		
		if (!product.exists()) {
			throw new Exception("Product path " + productPath + " doesn't exist.");
		}
		
		File[] zippedProd = product.listFiles(getZipFilter());
		
		for (File zip: zippedProd) {
			String dest = productPath + File.separator + getFileNameWithoutExt(zip.getName());
			decompressFile(zip.getCanonicalPath(), dest);
			delete(zip);
		}
		
		File[] productList = product.listFiles(getDirFilter());
		
		for (File prod : productList) {
			uploadProduct(prod);
		}
	}
	
	public void uploadProduct(File prod) throws Exception {
		for (File file : prod.listFiles()) {
			if (file.isDirectory()) {
				switch (file.getName()) {
				case BUNDLES:
					for (File f: file.listFiles()) {
						upload(f, crBundlePath, true);
					}
					break;
				case ARTIFACTS:
					for (File f: file.listFiles()) {
						upload(f, crProductArtifactPath + prod.getName() + crArtifactPath, true);
					}
				default:
					break;
				}
			} else if (file.getName().endsWith(META_EXTENSION)) {
				upload(file, crMetaPath, true);
			}
		}
	}

	private void uploadArtifacts(String artifactPath) throws Exception {
		File artifact = new File(artifactPath);
		
		if (!artifact.exists()) {
			throw new Exception("Artifact path " + artifactPath + " doesn't exist.");
		}
		
		File[] artList = artifact.listFiles(getZipFilter());
		
		for (File zip: artList) {
			String dest = artifactPath + File.separator + getFileNameWithoutExt(zip.getName());
			decompressFile(zip.getCanonicalPath(), dest);
			delete(zip);
		}
		
		for (File file: artifact.listFiles()) {
			upload(file, crArtifactPath, true);
		}
	}
	
	private void delete(File file) throws IOException {
		if (file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
				getLogger().info("Directory is deleted : " + file.getAbsolutePath());
			} else {
				// list all the directory contents
				String[] files = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					getLogger().info("Directory is deleted : " + file.getAbsolutePath());
				}
			}
		} else {
			// if file, then delete it
			file.delete();
			getLogger().info("File is deleted : " + file.getAbsolutePath());
		}
	}
	
	private FileFilter getDirFilter() {
		FileFilter dirFilter = new FileFilter() {
            @Override
            public boolean accept(File path) {
                if (path.isDirectory()) {
                    return true;
                }
                return false;
            }
        };
        return dirFilter;
	}
	
	private FileFilter getZipFilter() {
		FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        };
        
        return fileFilter;
	}
	
	private String getFileNameWithoutExt(String filename) {
		return filename.replaceFirst("[.][^.]+$", "");
	}
}
