// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.servicemanager.metadata.tool.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.edifecs.core.configuration.helper.JAXBUtility;
import com.edifecs.core.configuration.metadata.Bundle;
import com.edifecs.core.configuration.metadata.MetadataConfiguration;
import com.edifecs.core.configuration.metadata.Product;
import com.edifecs.core.configuration.metadata.Property;
import com.edifecs.core.configuration.metadata.ResourceTypeReference;
import com.edifecs.core.configuration.metadata.Role;
import com.edifecs.core.configuration.metadata.ServiceType;
import com.edifecs.core.configuration.metadata.ServiceTypeReference;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.isc.annotations.RequiresPermissions;
import com.edifecs.epp.isc.annotations.RequiresRoles;
import com.edifecs.epp.isc.core.exception.HandlerConfigurationException;
import com.edifecs.security.data.Permission;
import com.edifecs.servicemanager.annotations.Resource;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.annotations.ServiceDependency;
import com.google.gson.stream.JsonWriter;

/**
 * Maven plugin used to generate metadata automatically based on the configured
 * service annotations.
 * 
 * @author willclem
 */
@Mojo(name = "generate-metadata", aggregator = true, requiresProject = false, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class MetadataGeneratorMavenPlugin extends AbstractMojo {

	private static final String NAV_JSON_FILENAME = "nav.json";

	@Parameter
	private String productName;

	@Parameter
	private String productVersion;

	@Parameter(defaultValue = "src/main/java")
	private String sourcePath;

	@Parameter
	private String outputFile;

	@Parameter
	private String outputDir;

	@Parameter
	private String roleName;

	@Parameter
	private List<com.edifecs.servicemanager.metadata.tool.maven.Product> products = new ArrayList<com.edifecs.servicemanager.metadata.tool.maven.Product>();

	@Component
	private MavenProject mavenProject;

	@Component
	// for Maven 3 only
	private PluginDescriptor plugin;

	@Component
	private Settings settings;

	@Component
	private RepositorySystem repoSystem;

	/**
	 * The current repository/network configuration of Maven.
	 */
	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
	private RepositorySystemSession repoSession;

	/**
	 * The project's remote repositories to use for the resolution of plugins
	 * and their dependencies.
	 */
	@Parameter(defaultValue = "${project.remotePluginRepositories}", readonly = true)
	private List<RemoteRepository> remoteRepos;

	public void execute() throws MojoExecutionException, MojoFailureException {

		// Resolve all MVN Dependencies and create a list of them for loading
		List<URL> urls = new ArrayList<URL>();
		for (org.apache.maven.artifact.Artifact mvnartifact : new ArrayList<org.apache.maven.artifact.Artifact>(
				mavenProject.getDependencyArtifacts())) {
			Artifact artifact;
			try {
				artifact = new DefaultArtifact(mvnartifact.getId());
			} catch (IllegalArgumentException e) {
				throw new MojoFailureException(e.getMessage(), e);
			}

			ArtifactRequest request = new ArtifactRequest();
			request.setArtifact(artifact);
			request.setRepositories(remoteRepos);

			getLog().info(
					"Resolving artifact " + artifact + " from " + remoteRepos);

			ArtifactResult result;
			try {
				result = repoSystem.resolveArtifact(repoSession, request);
			} catch (ArtifactResolutionException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}

			getLog().info(
					"Resolved artifact " + artifact + " to "
							+ result.getArtifact().getFile() + " from "
							+ result.getRepository());

			try {
				urls.add(result.getArtifact().getFile().toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		// Load all the MVN Dependencies into the class classloader
		URLClassLoader loader = null;
		try {
			File directory = new File(mavenProject.getBasedir()
					.getAbsolutePath() + File.separator + "target");

			File[] files = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});

			// add dependencies
			File directoryDep = new File(mavenProject.getBasedir()
					.getAbsolutePath()
					+ File.separator
					+ "target"
					+ File.separator + "dependencies");

			if (directoryDep.exists()) {
				File[] depFiles = directoryDep.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".jar");
					}
				});
				for (File file : depFiles) {
					URL url = file.toURI().toURL();
					urls.add(url);
				}
			}

			for (File file : files) {
				URL url = file.toURI().toURL();
				urls.add(url);
			}

			loader = new URLClassLoader(urls.toArray(new URL[] {}), getClass()
					.getClassLoader());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		File rootPath = new File(mavenProject.getBasedir() + File.separator
				+ sourcePath);

		Collection<File> paths = FileUtils.listFiles(rootPath,
				new String[] { "java" }, true);

		// Permission and Roles
		List<Permission> permissions = new ArrayList<>();
		List<com.edifecs.security.data.Role> roles = new ArrayList<>();

		// Generate generic Metadata objects
		MetadataConfiguration configuration = new MetadataConfiguration();
		Product product = new Product();

		product.setName(productName);
		product.setVersion(productVersion);

		for (com.edifecs.servicemanager.metadata.tool.maven.Product dep : products) {
			getLog().info("Adding Dependent Product => " + dep.toString());
			Product depProduct = new Product();
			depProduct.setName(dep.getName());
			depProduct.setVersion(dep.getVersion());
			product.getDependentProducts().add(depProduct);
		}

		// TODO: Roles and Product Details need to be defined separately or
		// through input properties.
		Role role = new Role();
		if (roleName != null) {
			role.setName(roleName);
			product.getRoles().add(role);
		}
		configuration.getProducts().add(product);
		for (File sourceFile : paths) {
			if (sourceFile.getName().endsWith("package-info.java")) {
				getLog().debug("Skipping: " + sourceFile);
			} else {
				getLog().info("Loading source file: " + sourceFile);

				String packageName = sourceFile.getAbsolutePath().replace(
						rootPath.getAbsolutePath() + File.separator, "");
				packageName = packageName.trim();
				packageName = packageName.replace(File.separator, ".");
				String className = packageName.replace(".java", "");
				String fullName = className;

				getLog().info("Loading class: " + fullName);

				try {
					// Load Class based on the class name
					Class<?> clazz = loader.loadClass(fullName);
					// Get the Service Annotation from the class file
					Service service = clazz.getAnnotation(Service.class);

					if (service != null) {
						getLog().info("Service Annotations Found");

						// Translate METADATA of a service type for use in a
						// role.
						ServiceType serviceTypeRole = new ServiceType();
						serviceTypeRole.setName(service.name());
						serviceTypeRole.setVersion(service.version());

						if (roleName != null) {
							role.getServices().add(serviceTypeRole);
						}

						// Translate all METADATA information from the
						// Annotations
						ServiceType serviceType = new ServiceType();
						serviceType.setName(service.name());
						serviceType.setVersion(service.version());

						serviceType.setDescription(service.description());
						serviceType.setMaxInstances(Integer.toString(service
								.maxInstances()));

						serviceType.setClassName(fullName);

						serviceType.setRequired(service.required());

						for (com.edifecs.servicemanager.annotations.Property propertyAn : service
								.properties()) {
							Property property = new Property();
							property.setName(propertyAn.name());
							property.setDefaultValue(propertyAn.defaultValue());
							property.setDescription(propertyAn.description());
							property.setRequired(propertyAn.required());
							property.setEditable(propertyAn.editable());

							property.setType(com.edifecs.core.configuration.metadata.Property.PropertyType
									.valueOf(propertyAn.propertyType()
											.getText()));

							property.setRegEx(propertyAn.regEx());
							property.setRegExError(propertyAn.regExError());
							property.setSelectOneValues(Arrays
									.asList(propertyAn.selectValues()));
							serviceType.getProperties().add(property);
						}

						for (com.edifecs.servicemanager.annotations.Bundle bundleAn : service
								.bundles()) {
							Bundle bundle = new Bundle();
							bundle.setName(bundleAn.name());
							bundle.setVersion(bundleAn.version());
							serviceType.getBundles().add(bundle);
						}

						for (ServiceDependency servicesAn : service.services()) {
							ServiceTypeReference services = new ServiceTypeReference();
							services.setName(servicesAn.name());
							services.setVersion(servicesAn.version());
							services.setTypeName(servicesAn.typeName());
							services.setUnique(servicesAn.unique());
							serviceType.getServiceTypes().add(services);
						}

						for (Resource resourcesAn : service.resources()) {
							ResourceTypeReference resources = new ResourceTypeReference();
							resources.setName(resourcesAn.name());
							resources.setType(resourcesAn.type());
							resources.setUnique(resourcesAn.unique());
							serviceType.getResources().add(resources);
						}
						product.getServices().add(serviceType);
					}

					// parse only for apps
					else if (outputDir != null
							&& AbstractCommandHandler.class
									.isAssignableFrom(clazz)) {
						getLog().info(
								"parsing command handler : " + clazz.getName());

						for (Method m : clazz.getDeclaredMethods()) {

							RequiresPermissions permAnn = m
									.getAnnotation(RequiresPermissions.class);
							if (null != permAnn) {
								getLog().info(
										"permission found for command : "
												+ m.getName());
								for (String str : permAnn.value()) {
									Permission p = parsePermission(str);
									permissions.add(p);
								}
							}

							RequiresRoles roleAnn = m
									.getAnnotation(RequiresRoles.class);
							if (null != roleAnn) {
								getLog().info(
										"role found for command : "
												+ m.getName());
								for (String str : roleAnn.value()) {
									// TODO : role ann format?
								}
							}
							
							
							org.apache.shiro.authz.annotation.RequiresPermissions permAnn2 = m
									.getAnnotation(org.apache.shiro.authz.annotation.RequiresPermissions.class);
							if (null != permAnn2) {
								getLog().info(
										"permission found for command : "
												+ m.getName());
								for (String str : permAnn2.value()) {
									Permission p = parsePermission(str);
									permissions.add(p);
								}
							}

							org.apache.shiro.authz.annotation.RequiresRoles roleAnn2 = m
									.getAnnotation(org.apache.shiro.authz.annotation.RequiresRoles.class);
							if (null != roleAnn2) {
								getLog().info(
										"role found for command : "
												+ m.getName());
								for (String str : roleAnn2.value()) {
									// TODO : role ann format?
								}
							}
							
						}
					}

				} catch (HandlerConfigurationException e) {
					getLog().error(e);
					throw new MojoExecutionException(e.getMessage(), e);
				} catch (Exception e) {
					getLog().error(e);
				}
			}
		}

		// Load the 'nav.json' navigation menu file, if it exists.
		final File navFile = new File(mavenProject.getBasedir()
				.getAbsolutePath() + File.separator + NAV_JSON_FILENAME);
		if (navFile.exists() && navFile.isFile()) {

			// copy nav.json to components nav.json
			if (null == outputDir)
				throw new MojoExecutionException(
						"Output Dir not defined for project, "
								+ mavenProject.getArtifactId()
								+ "/n"
								+ "Please configure the 'outputDir' proeprty in the plugin configuration");

			String destFileName = outputDir + File.separator
					+ navFile.getName();

			final File destFile = new File(destFileName).getAbsoluteFile();
			try {
				FileUtils.copyFile(navFile, destFile, true);
				getLog().info(
						"copied nav.json to " + destFile.getAbsolutePath());
			} catch (IOException e) {
				getLog().error(e);
			}
		}
		if (null != outputFile) {
			try {
				Marshaller marshaller = JAXBUtility.METADATA.getContext()
						.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);

				File outputPath = new File(outputFile).getAbsoluteFile();
				outputPath.getParentFile().mkdirs();
				marshaller.marshal(configuration, outputPath);

			} catch (JAXBException e) {
				getLog().error(e);
				throw new MojoExecutionException(e.toString());
			}
		}
		if (null != outputDir) {
			try {
				serializeRolesAndPermissions(permissions, roles);
			} catch (Exception e) {
				getLog().error(e);
				throw new MojoExecutionException(e.toString());
			}
		}
	}

	private void serializeRolesAndPermissions(List<Permission> permissions,
			List<com.edifecs.security.data.Role> roles) throws Exception {

		// write to json
		String fileName = ".security-defaults";
		String securityOutputFile = outputDir + File.separator + "conf"
				+ File.separator + fileName;
		if (mavenProject.getArtifactId().contains("node")) {
			fileName = ".node-defaults";
			securityOutputFile = outputDir + File.separator + File.separator
					+ fileName;
		}
		if (mavenProject.getArtifactId().contains("agent")) {
			fileName = ".agent-defaults";
			securityOutputFile = outputDir + File.separator + File.separator
					+ fileName;
		}

		File outputPath = new File(securityOutputFile).getAbsoluteFile();
		getLog().info("parent file : " + outputPath.getParentFile().getName());
		outputPath.getParentFile().mkdirs();
		JsonWriter jsonWriter = new JsonWriter(new FileWriter(outputPath));
		getLog().info(
				"writing " + permissions.size() + " permissions to file : "
						+ outputPath.getAbsolutePath());

		jsonWriter.setIndent("\t");

		jsonWriter.beginObject().name("permissions");
		jsonWriter.beginArray();
		for (Permission p : permissions) {
			jsonWriter.beginObject();
			jsonWriter.name("permission").value(parsePermission(p));
			jsonWriter.name("name").value(p.getCanonicalName());
			if (p.getDescription() != null) {
				jsonWriter.name("description").value(p.getDescription());
			}
			jsonWriter.endObject();
		}
		jsonWriter.endArray();

		jsonWriter.name("roles");
		jsonWriter.beginArray();
		for (com.edifecs.security.data.Role r : roles) {
			jsonWriter.beginObject();
			jsonWriter.name("name").value(r.getCanonicalName());
			jsonWriter.name("description").value(r.getDescription());
			// TODO : permissions?
			jsonWriter.endObject();
		}
		jsonWriter.endArray();

		jsonWriter.endObject();
		jsonWriter.close();
	}

	private String parsePermission(Permission permission) {

		StringBuilder sb = new StringBuilder();

		sb.append(permission.getProductCanonicalName());

		if (null != permission.getCategoryCanonicalName())
			sb.append(":").append(permission.getCategoryCanonicalName());

		if (null != permission.getTypeCanonicalName())
			sb.append(":").append(permission.getTypeCanonicalName());

		if (null != permission.getSubTypeCanonicalName())
			sb.append(":").append(permission.getSubTypeCanonicalName());

		if (null != permission.getCanonicalName())
			sb.append(":").append(permission.getCanonicalName());

		return sb.toString();
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<com.edifecs.servicemanager.metadata.tool.maven.Product> getProducts() {
		return products;
	}

	public void setProducts(
			List<com.edifecs.servicemanager.metadata.tool.maven.Product> products) {
		this.products = products;
	}

	public MavenProject getMavenProject() {
		return mavenProject;
	}

	public void setMavenProject(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
	}

	public PluginDescriptor getPlugin() {
		return plugin;
	}

	public void setPlugin(PluginDescriptor plugin) {
		this.plugin = plugin;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public RepositorySystem getRepoSystem() {
		return repoSystem;
	}

	public void setRepoSystem(RepositorySystem repoSystem) {
		this.repoSystem = repoSystem;
	}

	public RepositorySystemSession getRepoSession() {
		return repoSession;
	}

	public void setRepoSession(RepositorySystemSession repoSession) {
		this.repoSession = repoSession;
	}

	public List<RemoteRepository> getRemoteRepos() {
		return remoteRepos;
	}

	public void setRemoteRepos(List<RemoteRepository> remoteRepos) {
		this.remoteRepos = remoteRepos;
	}

	public String getAppDir() {
		return outputDir;
	}

	public void setAppDir(String appDir) {
		this.outputDir = appDir;
	}

	private Permission parsePermission(String permStr)
			throws HandlerConfigurationException {
		final Permission permission = new Permission();
		final String[] permissionArray = permStr.split(":");
		if (permissionArray.length != 5) {
			throw new HandlerConfigurationException(
					"The permission definitions must follow the following format: "
							+ "<Product>:<Category>:<Type>:<SubType>:<Name> The permission "
							+ permStr + " is invalid");
		}
		permission.setCanonicalName(permissionArray[4]);
		permission.setSubTypeCanonicalName(permissionArray[3]);
		permission.setTypeCanonicalName(permissionArray[2]);
		permission.setCategoryCanonicalName(permissionArray[1]);
		permission.setProductCanonicalName(permissionArray[0]);
		return permission;
	}
}
