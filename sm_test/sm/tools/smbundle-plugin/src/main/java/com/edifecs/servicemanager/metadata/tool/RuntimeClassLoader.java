package com.edifecs.servicemanager.metadata.tool;

import java.net.URL;
import java.net.URLClassLoader;

public class RuntimeClassLoader extends URLClassLoader {
	
	public RuntimeClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
}
