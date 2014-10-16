package com.edifecs.servicemanager.contentrepository.uploadtool.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Empty goal, provided only to set loose the lifecycle.
 * 
 * @goal smbundle
 * @execute lifecycle="smbundle" phase="smbundle-upload"
 * @aggregate
 */
public class ContentRepositoryLifecycleMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		// nothing to do.
	}

}