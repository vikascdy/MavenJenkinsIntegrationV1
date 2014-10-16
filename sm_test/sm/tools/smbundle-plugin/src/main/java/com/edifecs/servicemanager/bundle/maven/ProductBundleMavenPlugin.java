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
//package com.edifecs.servicemanager.bundle.maven;
//
//import java.io.File;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.maven.plugin.AbstractMojo;
//import org.apache.maven.plugin.MojoExecutionException;
//import org.apache.maven.plugin.MojoFailureException;
//import org.apache.maven.plugin.descriptor.PluginDescriptor;
//import org.apache.maven.plugins.annotations.Component;
//import org.apache.maven.plugins.annotations.Mojo;
//import org.apache.maven.plugins.annotations.Parameter;
//import org.apache.maven.plugins.annotations.ResolutionScope;
//import org.apache.maven.project.MavenProject;
//import org.apache.maven.settings.Settings;
//import org.sonatype.aether.RepositorySystem;
//import org.sonatype.aether.RepositorySystemSession;
//import org.sonatype.aether.artifact.Artifact;
//import org.sonatype.aether.repository.RemoteRepository;
//import org.sonatype.aether.resolution.ArtifactRequest;
//import org.sonatype.aether.resolution.ArtifactResolutionException;
//import org.sonatype.aether.resolution.ArtifactResult;
//import org.sonatype.aether.util.artifact.DefaultArtifact;
//
//import com.edifecs.core.configuration.helper.JAXBUtility;
//import com.edifecs.core.configuration.metadata.Bundle;
//import com.edifecs.core.configuration.metadata.MetadataConfiguration;
//import com.edifecs.core.configuration.metadata.Product;
//import com.edifecs.core.configuration.metadata.Property;
//import com.edifecs.core.configuration.metadata.ResourceTypeReference;
//import com.edifecs.core.configuration.metadata.Role;
//import com.edifecs.core.configuration.metadata.ServiceType;
//import com.edifecs.core.configuration.metadata.ServiceTypeReference;
//import com.edifecs.servicemanager.annotations.Resource;
//import com.edifecs.servicemanager.annotations.Service;
//import com.edifecs.servicemanager.annotations.ServiceDependency;
//
///**
// * Maven plugin used to generate metadata automatically based on the configured
// * service annotations.
// * 
// * @author willclem
// */
//
//@Mojo(name = "generate-product-bundle", aggregator = true, requiresProject = false, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
//public class ProductBundleMavenPlugin extends AbstractMojo {
//
//	@Parameter
//	private String productName;
//
//	@Parameter
//	private String productVersion;
//
//	
//	@Parameter
//	private String outputFile;
//
//	/*
//	 * Components
//	 */
//	
//	@Component
//	private MavenProject mavenProject;
//
//	@Component
//	private PluginDescriptor plugin;
//
//	@Component
//	private Settings settings;
//
//	@Component
//	private RepositorySystem repoSystem;
//
//	/**
//	 * The current repository/network configuration of Maven.
//	 */
//	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
//	private RepositorySystemSession repoSession;
//
//	/**
//	 * The project's remote repositories to use for the resolution of plugins
//	 * and their dependencies.
//	 */
//	@Parameter(defaultValue = "${project.remotePluginRepositories}", readonly = true)
//	private List<RemoteRepository> remoteRepos;
//
//	public void execute() throws MojoExecutionException, MojoFailureException {
//
//		// Resolve all MVN Dependencies and create a list of them for loading
//		List<URL> urls = new ArrayList<URL>();
//		for (org.apache.maven.artifact.Artifact mvnartifact : new ArrayList<org.apache.maven.artifact.Artifact>(
//				mavenProject.getDependencyArtifacts())) {
//			Artifact artifact;
//			try {
//				artifact = new DefaultArtifact(mvnartifact.getId());
//			} catch (IllegalArgumentException e) {
//				throw new MojoFailureException(e.getMessage(), e);
//			}
//
//			ArtifactRequest request = new ArtifactRequest();
//			request.setArtifact(artifact);
//			request.setRepositories(remoteRepos);
//
//			getLog().info(
//					"Resolving artifact " + artifact + " from " + remoteRepos);
//
//			ArtifactResult result;
//			try {
//				result = repoSystem.resolveArtifact(repoSession, request);
//			} catch (ArtifactResolutionException e) {
//				throw new MojoExecutionException(e.getMessage(), e);
//			}
//
//			getLog().info(
//					"Resolved artifact " + artifact + " to "
//							+ result.getArtifact().getFile() + " from "
//							+ result.getRepository());
//
//			try {
//				urls.add(result.getArtifact().getFile().toURI().toURL());
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//		}
//
//		// Load all the MVN Dependencies into the class classloader
//		URLClassLoader loader = null;
//		try {
//			URL url = new File(mavenProject.getBasedir().getAbsolutePath()
//					+ File.separator + "target" + File.separator
//					+ mavenProject.getArtifactId() + "-"
//					+ mavenProject.getVersion() + ".jar").toURI().toURL();
//			urls.add(url);
//
//			loader = new URLClassLoader(urls.toArray(new URL[] {}), getClass()
//					.getClassLoader());
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		}
//
//		
//		File rootPath = new File(mavenProject.getBasedir() + File.separator + sourcePath);
//
//		Collection<File> paths = FileUtils.listFiles(rootPath,
//				new String[] { "java" }, true);
//
//		// Generate generic Metadata objects
//		MetadataConfiguration configuration = new MetadataConfiguration();
//		Product product = new Product();
//
//		product.setName(productName);
//		product.setVersion(productVersion);
//
//		for (com.edifecs.servicemanager.metadata.tool.maven.Product dep : products) {
//			getLog().info("Adding Dependent Product => " + dep.toString());
//			Product depProduct = new Product();
//			depProduct.setName(dep.getName());
//			depProduct.setVersion(dep.getVersion());
//			product.getDependantProducts().add(depProduct);
//		}
//
//		// TODO: Roles and Product Details need to be defined separately or
//		// through input properties.
//		Role role = new Role();
//		if (roleName != null) {
//			role.setName(roleName);
//			product.getRoles().add(role);
//		}
//		configuration.getProducts().add(product);
//		for (File sourceFile : paths) {
//			if(sourceFile.getName().endsWith("package-info.java")) {
//				getLog().debug("Skipping: " + sourceFile);
//			} else {
//				getLog().info("Loading source file: " + sourceFile);
//	
//				String packageName = sourceFile.getAbsolutePath().replace(
//						rootPath.getAbsolutePath() + File.separator, "");
//				packageName = packageName.trim();
//				packageName = packageName.replace(File.separator, ".");
//				String className = packageName.replace(".java", "");
//				String fullName = className;
//	
//				getLog().info("Loading class: " + fullName);
//	
//				try {
//					// Load Class based on the class name
//					Class<?> clazz = loader.loadClass(fullName);
//	
//					// Get the Service Annotation from the class file
//					Service service = clazz.getAnnotation(Service.class);
//	
//					if (service != null) {
//						getLog().info("Service Annotations Found");
//	
//						// Translate METADATA of a service type for use in a role.
//						ServiceType serviceTypeRole = new ServiceType();
//						serviceTypeRole.setName(service.name());
//						serviceTypeRole.setVersion(service.version());
//	
//						if (roleName != null) {
//							role.getServices().add(serviceTypeRole);
//						}
//						
//						// Translate all METADATA information from the
//						// Annotations
//						ServiceType serviceType = new ServiceType();
//						serviceType.setName(service.name());
//						serviceType.setVersion(service.version());
//	
//						serviceType.setDescription(service.description());
//						serviceType.setMaxInstances(Integer.toString(service
//								.maxInstances()));
//						// If no classname if given, set the classname automatically
//						if (service.className() == null || service.className().equals("")) {
//							serviceType.setClassName(fullName);
//						} else {
//							serviceType.setClassName(service.className());
//						}
//						serviceType.setRequired(service.required());
//	
//						for (com.edifecs.servicemanager.annotations.Property propertyAn : service
//								.properties()) {
//							Property property = new Property();
//							property.setName(propertyAn.name());
//							property.setDefaultValue(propertyAn.defaultValue());
//							property.setDescription(propertyAn.description());
//							property.setRequired(propertyAn.required());
//							property.setType(propertyAn.propertyType());
//							property.setRegEx(propertyAn.regEx());
//							property.setRegExError(propertyAn.regExError());
//							property.setSelectOneValues(Arrays.asList(propertyAn
//									.selectValues()));
//							serviceType.getProperties().add(property);
//						}
//	
//						for (com.edifecs.servicemanager.annotations.Bundle bundleAn : service
//								.bundles()) {
//							Bundle bundle = new Bundle();
//							bundle.setName(bundleAn.name());
//							bundle.setVersion(bundleAn.version());
//							serviceType.getBundles().add(bundle);
//						}
//	
//						for (ServiceDependency servicesAn : service.services()) {
//							ServiceTypeReference services = new ServiceTypeReference();
//							services.setName(servicesAn.name());
//							services.setVersion(servicesAn.version());
//							services.setTypeName(servicesAn.typeName());
//							services.setUnique(servicesAn.unique());
//							serviceType.getServiceTypes().add(services);
//						}
//	
//						for (Resource resourcesAn : service.resources()) {
//							ResourceTypeReference resources = new ResourceTypeReference();
//							resources.setName(resourcesAn.name());
//							resources.setType(resourcesAn.type());
//							resources.setUnique(resourcesAn.unique());
//							serviceType.getResources().add(resources);
//						}
//						product.getServices().add(serviceType);
//					}
//				} catch (Exception e) {
//					getLog().error(e);
//					throw new MojoExecutionException(e.toString());
//				}
//			}
//		}
//		try {
//			Marshaller marshaller = JAXBUtility.METADATA.getContext()
//					.createMarshaller();
//			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
//					Boolean.TRUE);
//
//			File outputPath = new File(outputFile).getAbsoluteFile();
//			outputPath.getParentFile().mkdirs();
//			marshaller.marshal(configuration, outputPath);
//
//		} catch (JAXBException e) {
//			getLog().error(e);
//			throw new MojoExecutionException(e.toString());
//		}
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
//	public String getSourcePath() {
//		return sourcePath;
//	}
//
//	public void setSourcePath(String sourcePath) {
//		this.sourcePath = sourcePath;
//	}
//
//	public String getOutputFile() {
//		return outputFile;
//	}
//
//	public void setOutputFile(String outputFile) {
//		this.outputFile = outputFile;
//	}
//
//	public String getRoleName() {
//		return roleName;
//	}
//
//	public void setRoleName(String roleName) {
//		this.roleName = roleName;
//	}
//
//	public List<com.edifecs.servicemanager.metadata.tool.maven.Product> getProducts() {
//		return products;
//	}
//
//	public void setProducts(List<com.edifecs.servicemanager.metadata.tool.maven.Product> products) {
//		this.products = products;
//	}
//
//	public MavenProject getMavenProject() {
//		return mavenProject;
//	}
//
//	public void setMavenProject(MavenProject mavenProject) {
//		this.mavenProject = mavenProject;
//	}
//
//	public PluginDescriptor getPlugin() {
//		return plugin;
//	}
//
//	public void setPlugin(PluginDescriptor plugin) {
//		this.plugin = plugin;
//	}
//
//	public Settings getSettings() {
//		return settings;
//	}
//
//	public void setSettings(Settings settings) {
//		this.settings = settings;
//	}
//
//	public RepositorySystem getRepoSystem() {
//		return repoSystem;
//	}
//
//	public void setRepoSystem(RepositorySystem repoSystem) {
//		this.repoSystem = repoSystem;
//	}
//
//	public RepositorySystemSession getRepoSession() {
//		return repoSession;
//	}
//
//	public void setRepoSession(RepositorySystemSession repoSession) {
//		this.repoSession = repoSession;
//	}
//
//	public List<RemoteRepository> getRemoteRepos() {
//		return remoteRepos;
//	}
//
//	public void setRemoteRepos(List<RemoteRepository> remoteRepos) {
//		this.remoteRepos = remoteRepos;
//	}
//
//}
