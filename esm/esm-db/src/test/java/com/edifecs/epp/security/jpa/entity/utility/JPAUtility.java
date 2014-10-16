package com.edifecs.epp.security.jpa.entity.utility;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtility {

	private final static String PERSISTENCE_UNIT = "security-db";
	private static EntityManagerFactory emf;
	private static EntityManager em;

    //Used to randomly generate new DB connections
    private static final Random random = new Random(new Date().getTime());

	public static EntityManager getEntityManager() {
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
		properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
		properties.put("hibernate.connection.username", "sa");
		properties.put("hibernate.connection.password", "");
		properties.put("hibernate.connection.driver_class", "org.h2.Driver");
		properties.put("hibernate.connection.url", "jdbc:h2:mem:TestDatabase" + random.nextLong());
		properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.format_sql", "true");
		properties.put("hibernate.archive.autodetection", "class ,hbm");
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
		em = emf.createEntityManager();
		return em;
	}

	public static void shutdown() {
//		em.close();
//		emf.close();
	}
}

