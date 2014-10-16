package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class GroupEntityTest {

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

        ContactEntity contact = new ContactEntity();
        contact.setEmailAddress("a@a.com");
        contact.setFirstName("Abhijit");
        contact.setLastName("Singh");
        contact.setMiddleName("NA");

        OrganizationEntity org = new OrganizationEntity();
        org.setCanonicalName("ABC");
        org.setTenant(tenant);

        OrganizationEntity org2 = new OrganizationEntity();
        org2.setCanonicalName("ABC");
        org2.setTenant(tenant);

        List<UserEntity> users = new ArrayList<UserEntity>();

        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setDeleted(false);
        user.setContact(contact);
        user.setOrganization(org);
        user.setLastLoginDateTime(new Date());

        users.add(user);

        List<RoleEntity> roles = new ArrayList<RoleEntity>();

        RoleEntity role = new RoleEntity();
        role.setCanonicalName("ADMIN");
        role.setTenant(tenant);

        RoleEntity role2 = new RoleEntity();
        role2.setCanonicalName("USER");
        role2.setTenant(tenant);

        roles.add(role);
        roles.add(role2);

        UserGroupEntity group = new UserGroupEntity();
        
        group.setTenant(tenant);
        group.setCanonicalName("Parent Group");
        group.setUsers(users);
        group.setRoles(roles);
        group.getOrganizations().add(org);
        group.getOrganizations().add(org2);
        
        UserGroupEntity childGroup = new UserGroupEntity();
        childGroup.setCanonicalName("Child Group");
        childGroup.setTenant(tenant);
        childGroup.setUsers(users);
        childGroup.setRoles(roles);
        childGroup.getOrganizations().add(org);
        childGroup.getOrganizations().add(org2);
        
        List<UserGroupEntity> childGroups = new ArrayList<>();
        childGroups.add(childGroup);
        group.setChildGroups(childGroups);

        em.persist(site);
        em.persist(tenant);
        em.persist(role);
        em.persist(role2);
        em.persist(org);
        em.persist(org2);
        em.persist(contact);
        em.persist(user);
        em.persist(childGroup);
        em.persist(group);
        

        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<UserGroupEntity> groups = em.createNamedQuery(UserGroupEntity.FIND_ALL_GROUPS).getResultList();

        if (groups.size() > 0) {
            for (UserGroupEntity group1 : groups) {
                System.out.println("Group Details ...");
                System.out.println("Id " + group1.getId());
                System.out.println("Name " + group1.getCanonicalName());
                System.out.println("Role NUM " + group1.getRoles().size());
                System.out.println("Orgs NUM " + group1.getOrganizations().size());
                System.out.println("Tenant  " + group1.getTenant().getCanonicalName());
                System.out.println("Users  " + group1.getUsers().get(0).getId());
                System.out.println("Role " + group1.getRoles().get(0).getCanonicalName());
                System.out.println("Orgs " + group1.getOrganizations().get(0).getCanonicalName());
            }
        }

        assertTrue(groups.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
