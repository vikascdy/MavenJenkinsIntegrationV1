package com.edifecs.epp.security.jpa.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.data.RealmType;
import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class SecurityRealmEntityTest {

	private static EntityManager em;
	private static EntityTransaction tx;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		em = JPAUtility.getEntityManager();
		if (em != null) {
			System.out.println("Entity Manager Created Successfully.");
		}
		System.out.println("Starting Transaction...");
		tx = em.getTransaction();
		tx.begin();
		System.out.println("Transaction Started...");
	}

	@Test
	public void test() {

        SiteEntity site = new SiteEntity();

        TenantEntity tenant = new TenantEntity();
        tenant.setSite(site);

		OrganizationEntity org = new OrganizationEntity();
		org.setCanonicalName("TEST_ORG");
        org.setTenant(tenant);

		SecurityRealmEntity seEntity = new SecurityRealmEntity();
		seEntity.setEnabled(true);
		seEntity.setName("TEST.ORG.LDAP");
		seEntity.setRealmType(RealmType.LDAP);

		CustomPropertyEntity prop = new CustomPropertyEntity();
		prop.setName("userProvider");
		prop.setDescription("User DN formats are unique to the LDAP directory's schema, and each environment differs - you will need to specify the format corresponding to your directory. You do this by specifying the full User DN as normal, but but you use a {0}} placeholder token in the string representing the location where the user's submitted principal (usually a username or uid) will be substituted at runtime. "
				+ "\n\nFor example, if your directory uses an LDAP uid attribute to represent usernames, the User DN for the jsmith user may look like this:\n\n uid=jsmith,ou=users,dc=mycompany,dc=com"
				+ "in which case you would set this property with the following template value: \n\n uid={0},ou=users,dc=mycompany,dc=com"
				+ "uid={0},ou=users,ou=system User DN formats are unique to the LDAP directory's schema, and each environment differs - you will need to specify the format corresponding to your directory. You do this by specifying the full User DN as normal, but but you use a {0}} placeholder token in the string representing the location where the user's submitted principal (usually a username or uid) will be substituted at runtime. "
				+ "\n\nFor example, if your directory uses an LDAP uid attribute to represent usernames, the User DN for the jsmith user may look like this:\n\n uid=jsmith,ou=users,dc=mycompany,dc=com"
				+ "in which case you would set this property with the following template value: \n\n uid={0},ou=users,dc=mycompany,dc=com");
		prop.setRequired(true);
		prop.setDefaultVal("ldap://192.168.1.1:10389");
		seEntity.getProperties().add(prop);

        em.persist(site);
        em.persist(tenant);
		em.persist(seEntity);
		em.persist(org);

		org.getSecurityRealms().add(seEntity);
		seEntity.setOrganization(org);

		tx.commit();

		Assert.assertNotNull(em.find(OrganizationEntity.class, org.getId())
				.getSecurityRealms().get(0).getProperties());
		System.out.println("Transaction Committed...");

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		JPAUtility.shutdown();
		System.out.println("Entity Manager closed...");
	}
}
