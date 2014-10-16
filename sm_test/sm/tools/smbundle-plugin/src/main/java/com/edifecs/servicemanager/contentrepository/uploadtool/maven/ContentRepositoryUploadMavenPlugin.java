//// -----------------------------------------------------------------------------
////  Copyright (c) Edifecs Inc. All Rights Reserved.
////
//// This software is the confidential and proprietary information of Edifecs Inc.
//// ("Confidential Information").  You shall not disclose such Confidential
//// Information and shall use it only in accordance with the terms of the license
//// agreement you entered into with Edifecs.
////
//// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
//// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
//// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
//// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
//// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
//// ITS DERIVATIVES.
//// -----------------------------------------------------------------------------
//package com.edifecs.servicemanager.contentrepository.uploadtool.maven;
//
//import java.io.File;
//
//import org.apache.maven.plugin.AbstractMojo;
//import org.apache.maven.plugin.MojoExecutionException;
//import org.apache.maven.plugins.annotations.Mojo;
//import org.apache.maven.plugins.annotations.Parameter;
//
//import com.edifecs.content.repository.upload.tool.CRProductUploadTool;
//import com.edifecs.content.repository.upload.tool.helpers.ConfigParameters;
//
///**
// * Maven plugin used to generate metadata automatically based on the configured
// * service annotations.
// * 
// * @author willclem
// */
//@Mojo ( name = "upload-bundle", aggregator = true, requiresProject = false )
//public class ContentRepositoryUploadMavenPlugin extends AbstractMojo {
//
//	@Parameter
//	private String productName;
//
//	@Parameter
//	private String productVersion;
//
//	@Parameter
//	private String distDir;
//
//	@Parameter
//	private String path;
//
//	public void execute() throws MojoExecutionException {
//		System.out.println("Uploading Product Bundle");
//
//		try {
//			ConfigParameters configuration = new ConfigParameters();
//			CRProductUploadTool uploader = new CRProductUploadTool(
//					configuration.getProperties());
//
//			Thread.sleep(1000);
//
//			uploader.createCRFoldersIfNotExists();
//
//			String unzippedPath = path.replace(".zip", "");
//			uploader.decompressFile(path, unzippedPath);
//
//			uploader.uploadProduct(new File(unzippedPath));
//		} catch (Exception e) {
//			throw new MojoExecutionException(
//					"Unable to upload product zip bundle", e);
//		}
//
//		System.out.println("Product Bundle Upload Complete");
//	}
//
//	public String getProductName() {
//		return productName;
//	}
//
//	public void setProductName(String productName) {
//		this.productName = productName;
//	}
//
//	public String getProductVersion() {
//		return productVersion;
//	}
//
//	public void setProductVersion(String productVersion) {
//		this.productVersion = productVersion;
//	}
//
//	public String getPath() {
//		return path;
//	}
//
//	public void setPath(String path) {
//		this.path = path;
//	}
//
//	public String getDistDir() {
//		return distDir;
//	}
//
//	public void setDistDir(String distDir) {
//		this.distDir = distDir;
//	}
//
//}
