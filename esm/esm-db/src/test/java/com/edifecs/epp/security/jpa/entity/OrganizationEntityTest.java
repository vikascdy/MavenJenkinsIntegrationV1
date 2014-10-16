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

public class OrganizationEntityTest {

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

        List<UserGroupEntity> groups = new ArrayList<UserGroupEntity>();
        UserGroupEntity group = new UserGroupEntity();
        group.setTenant(tenant);

        UserGroupEntity group2 = new UserGroupEntity();
        group2.setTenant(tenant);

        groups.add(group);
        groups.add(group2);

        OrganizationEntity org = new OrganizationEntity();
        org.setTenant(tenant);
        org.setCanonicalName("ABC");
        org.setGroups(groups);

        em.persist(site);
        em.persist(tenant);
        em.persist(group);
        em.persist(group2);
        em.persist(org);

        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<OrganizationEntity> orgs = em.createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS)
                .getResultList();

        if (orgs.size() > 0) {
            for (OrganizationEntity org1 : orgs) {
                System.out.println("Organization Details ...");
                System.out.println("Id " + org1.getId());
                System.out.println("Members " + org1.getGroups().get(0).getId());
                System.out.println("Member NUM " + org1.getGroups().size());
            }
        }

        assertTrue(orgs.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
