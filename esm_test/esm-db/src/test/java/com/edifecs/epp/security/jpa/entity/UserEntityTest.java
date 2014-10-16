package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class UserEntityTest {

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
		tenant.setCanonicalName("ABC");
		tenant.setSite(site);

		OrganizationEntity org = new OrganizationEntity();
		org.setTenant(tenant);

		ContactEntity contact = new ContactEntity();
		contact.setEmailAddress("a@a.com");
		contact.setFirstName("Abhijit");
		contact.setLastName("Singh");
		contact.setMiddleName("NA");
		contact.setSalutation("Mr.");

		List<UserGroupEntity> groups = new ArrayList<UserGroupEntity>();
		UserGroupEntity group = new UserGroupEntity();
		group.setTenant(tenant);

		UserGroupEntity group2 = new UserGroupEntity();
		group2.setTenant(tenant);

		UserGroupEntity group3 = new UserGroupEntity();
		group3.setTenant(tenant);

		groups.add(group);
		groups.add(group2);
		groups.add(group3);

		// Roles

		List<RoleEntity> roles = new ArrayList<RoleEntity>();

		RoleEntity role = new RoleEntity();
		role.setCanonicalName("ADMIN");

		RoleEntity role2 = new RoleEntity();
		role2.setCanonicalName("USER");

		roles.add(role);
		roles.add(role2);

		UserEntity user = new UserEntity();
		user.setActive(true);
		user.setDeleted(false);
		user.setContact(contact);
		user.setGroups(groups);
		user.setRoles(roles);
		user.setOrganization(org);

		em.persist(role);
		em.persist(role2);

		em.persist(site);
		em.persist(tenant);
		em.persist(org);
		em.persist(group);
		em.persist(group2);
		em.persist(group3);

		em.persist(contact);
		em.persist(user);
		tx.commit();

		System.out.println("Transaction Committed...");

		// check named query

		List<UserEntity> users = em.createNamedQuery(UserEntity.FIND_ALL_USERS)
				.getResultList();

		if (users.size() > 0) {
			for (UserEntity user1 : users) {
				System.out.println("User Details ...");
				System.out.println("Id " + user1.getId());
				System.out.println("First Name "
						+ user1.getContact().getFirstName());
				System.out.println("Email "
						+ user1.getContact().getEmailAddress());
				System.out.println("Group " + user1.getGroups().get(0).getId());
				System.out.println("Group NUM " + user1.getGroups().size());
				System.out.println("Role "
						+ user1.getRoles().get(0).getCanonicalName());
				System.out.println("Role NUM " + user1.getRoles().size());
			}
		}

		assertTrue(users.size() > 0);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		JPAUtility.shutdown();
		System.out.println("Entity Manager closed...");
	}
}
