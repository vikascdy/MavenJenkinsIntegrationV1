package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.data.Role;
import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;
import com.edifecs.epp.security.jpa.helper.ObjectConverter;

public class RoleEntityTest {

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
		tenant.setCanonicalName("ABC");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTenant(tenant);

		ContactEntity contact = new ContactEntity();
		contact.setEmailAddress("a@a.com");
		contact.setFirstName("Abhijit");
		contact.setLastName("Singh");
		contact.setMiddleName("NA");
		contact.setSalutation("Mr.");

		ContactEntity contact2 = new ContactEntity();
		contact2.setEmailAddress("b@b.com");
		contact2.setFirstName("Abhijit");
		contact2.setLastName("Singh");
		contact2.setMiddleName("NA");
		contact2.setSalutation("Mr.");

		List<UserEntity> users = new ArrayList<UserEntity>();

		UserEntity user = new UserEntity();
		user.setActive(true);
		user.setDeleted(false);
		user.setContact(contact);
        user.setOrganization(organization);

		UserEntity user2 = new UserEntity();
		user2.setActive(true);
		user2.setDeleted(false);
		user2.setContact(contact2);
        user2.setOrganization(organization);


		users.add(user);
		users.add(user2);

		List<UserGroupEntity> groups = new ArrayList<UserGroupEntity>();
		UserGroupEntity group = new UserGroupEntity();
		group.setTenant(tenant);

		UserGroupEntity group2 = new UserGroupEntity();
		group2.setTenant(tenant);

		groups.add(group);
		groups.add(group2);

		List<PermissionEntity> permissions = new ArrayList<PermissionEntity>();

		PermissionEntity permission = new PermissionEntity();
		permission.setCanonicalName("perm 1");
		permission.setCategoryCanonicalName("Category #1");
		permission.setProductCanonicalName("Product #1");
		permission.setTypeCanonicalName("Type #1");
		permission.setSubTypeCanonicalName("SubType #1");
		permission.setSortOrder(1L);

		PermissionEntity permission2 = new PermissionEntity();
		permission2.setCanonicalName("perm 2");
		permission2.setCategoryCanonicalName("Category #2");
		permission2.setProductCanonicalName("Product #2");
		permission2.setTypeCanonicalName("Type #2");
		permission2.setSubTypeCanonicalName("SubType #2");
		permission2.setSortOrder(2L);

		permissions.add(permission);
		permissions.add(permission2);

		RoleEntity role = new RoleEntity();
		role.setCanonicalName("ADMIN3");
		role.setGroups(groups);
		role.setUsers(users);
		role.setPermissions(permissions);
		role.setReadOnly(true);
        role.setTenant(tenant);

        em.persist(site);
		em.persist(tenant);
        em.persist(organization);
		em.persist(permission);
		em.persist(permission2);
		em.persist(contact);
		em.persist(contact2);
		em.persist(user);
		em.persist(user2);
		em.persist(group);
		em.persist(group2);
		em.persist(role);
		tx.commit();

		System.out.println("Transaction Committed...");

		// check named query

		List<RoleEntity> roles = em.createNamedQuery(RoleEntity.FIND_ALL_ROLES)
				.getResultList();

		if (roles.size() > 0) {
			for (RoleEntity role1 : roles) {
				System.out.println("Role Details ...");
				System.out.println("Id " + role1.getId());
				System.out.println("Role Name " + role1.getCanonicalName());
				System.out.println("Users  " + role1.getUsers().get(0).getId());
				System.out
						.println("Groups " + role1.getGroups().get(0).getId());
				System.out.println("Permissions "
						+ role1.getPermissions().get(0).getCanonicalName());

				System.out
						.println(" editible "
								+ ((Role) ObjectConverter.jpaToApi(role1))
										.isReadOnly());
			}
		}

		assertTrue(roles.size() > 0);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		JPAUtility.shutdown();
		System.out.println("Entity Manager closed...");
	}
}
