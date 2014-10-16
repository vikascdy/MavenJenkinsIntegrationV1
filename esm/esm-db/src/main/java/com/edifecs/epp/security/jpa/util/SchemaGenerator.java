package com.edifecs.epp.security.jpa.util;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SchemaGenerator {
	private Configuration cfg;

	public SchemaGenerator(String packageName) throws Exception {
		cfg = new Configuration();
		cfg.setProperty("hibernate.hbm2ddl.auto", "create");

		for (Class<?> clazz : getClasses(packageName)) {
			cfg.addAnnotatedClass(clazz);
		}
	}

	private void generate(Dialect dialect, String directory) {
		cfg.setProperty("hibernate.dialect", dialect.getDialectClass());
		File scriptDirectory = new File(directory);
		scriptDirectory.mkdirs();
		SchemaExport export = new SchemaExport(cfg);
		export.setDelimiter(";");
		export.setOutputFile(directory + "ddl_" + dialect.name().toLowerCase()
				+ ".sql");
		export.execute(false, false, false, true);
	}

	public static void main(String[] args) throws Exception {
		final String packageName = args[0];
		SchemaGenerator gen = new SchemaGenerator(packageName);
		final String directory = args[1];
		gen.generate(Dialect.MYSQL, directory);
		gen.generate(Dialect.ORACLE, directory);
		gen.generate(Dialect.SQLServer, directory);
	}

	private List<Class<?>> getClasses(String packageName) throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = packageName.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(packageName + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(packageName + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(packageName
					+ " is not a valid package");
		}

		return classes;
	}

	private static enum Dialect {
		ORACLE("org.hibernate.dialect.Oracle10gDialect"), MYSQL(
				"org.hibernate.dialect.MySQL5Dialect"), SQLServer(
				"org.hibernate.dialect.SQLServer2012Dialect");

		private String dialectClass;

		private Dialect(String dialectClass) {
			this.dialectClass = dialectClass;
		}

		public String getDialectClass() {
			return dialectClass;
		}
	}
}