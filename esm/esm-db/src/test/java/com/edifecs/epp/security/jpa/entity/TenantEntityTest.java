package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class TenantEntityTest {

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
    }

    @Test
    public void testPersist() {
        tx.begin();
        SiteEntity site = new SiteEntity();

        PasswordPolicyEntity pp = new PasswordPolicyEntity();
        pp.setChangePasswdAtFirstLogin(true);
        pp.setPasswdRegex("someRegex");

        TenantEntity com1 = new TenantEntity();
        com1.setCanonicalName("ABC");
        com1.setPasswordPolicy(pp);
        com1.setSite(site);

        OrganizationEntity org = new OrganizationEntity();
        org.setCanonicalName("org 1");
        org.setTenant(com1);

        OrganizationEntity org2 = new OrganizationEntity();
        org2.setCanonicalName("org 2");
        org2.setTenant(com1);

        List<TenantEntity> communities = em.createNamedQuery(TenantEntity.FIND_ALL_TENANTS).getResultList();
        assertTrue(communities.size() == 0);

        List<OrganizationEntity> orgs = em.createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS).getResultList();
        assertTrue(orgs.size() == 0);

        em.persist(site);
        em.persist(com1);
        em.persist(org);
        em.persist(org2);

        em.refresh(com1);

        tx.commit();

        communities = em.createNamedQuery(TenantEntity.FIND_ALL_TENANTS).getResultList();
        assertTrue(communities.size() == 1);
        orgs = em.createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS).getResultList();
        assertTrue(orgs.size() == 2);

        TenantEntity comm1 = communities.get(0);
        assertNotNull(comm1.getId());
        assertNotNull(comm1.getPasswordPolicy());
        assertEquals("ABC", comm1.getCanonicalName());
        assertTrue(comm1.getOrganizations().size() == 2);
        assertNotNull(comm1.getOrganizations().get(0).getId());
        assertEquals("org 1", comm1.getOrganizations().get(0).getCanonicalName());
        assertNotNull(comm1.getOrganizations().get(1).getId());
        assertEquals("org 2", comm1.getOrganizations().get(1).getCanonicalName());
    }

    @Test
    public void testUpdate() {
        SiteEntity site = new SiteEntity();

        TenantEntity tenant = new TenantEntity();
        tenant.setSite(site);

        if (em.createNamedQuery(TenantEntity.FIND_ALL_TENANTS).getResultList().size() == 0) {
            PasswordPolicyEntity pp = new PasswordPolicyEntity();
            pp.setChangePasswdAtFirstLogin(true);
            pp.setPasswdRegex("someRegex");

            tenant = new TenantEntity();
            tenant.setCanonicalName("ABC");
            tenant.setPasswordPolicy(pp);
            tenant.setSite(site);

            OrganizationEntity org = new OrganizationEntity();
            org.setCanonicalName("org 1");
            org.setTenant(tenant);

            OrganizationEntity org2 = new OrganizationEntity();
            org2.setCanonicalName("org 2");
            org2.setTenant(tenant);

            tx.begin();
            em.persist(site);
            em.persist(tenant);
            em.persist(org);
            em.persist(org2);

            em.refresh(tenant);
            tx.commit();
        }

        List<TenantEntity> communities = em.createNamedQuery(
                TenantEntity.FIND_ALL_TENANTS).getResultList();
        assertTrue(communities.size() == 1);
        assertTrue(em
                .createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS)
                .getResultList().size() == 2);

        tenant = communities.get(0);
        tx.begin();
        tenant.setCanonicalName("ABC Updated");
        tenant.getOrganizations().get(0).setCanonicalName("org 1 updated");
        tenant.getPasswordPolicy().setPasswdRegex("changed");
        tx.commit();

        assertTrue(em.createNamedQuery(TenantEntity.FIND_ALL_TENANTS)
                .getResultList().size() == 1);
        assertTrue(em
                .createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS)
                .getResultList().size() == 2);
        assertEquals(
                "ABC Updated",
                ((TenantEntity) em
                        .createNamedQuery(TenantEntity.FIND_ALL_TENANTS)
                        .getResultList().get(0)).getCanonicalName());
        assertEquals(
                "changed",
                ((TenantEntity) em
                        .createNamedQuery(TenantEntity.FIND_ALL_TENANTS)
                        .getResultList().get(0)).getPasswordPolicy()
                        .getPasswdRegex());
        assertEquals(
                "org 1 updated",
                ((TenantEntity) em
                        .createNamedQuery(TenantEntity.FIND_ALL_TENANTS)
                        .getResultList().get(0)).getOrganizations().get(0)
                        .getCanonicalName());
    }

    @Test
    public void testRemove() {
        OrganizationEntity org = null;
        List<OrganizationEntity> orgs = null;
        TenantEntity com1 = null;

        if (em.createNamedQuery(TenantEntity.FIND_ALL_TENANTS).getResultList()
                .size() == 0) {
            org = new OrganizationEntity();
            org.setCanonicalName("org 1");
            orgs = new ArrayList<>();
            orgs.add(org);
            org = new OrganizationEntity();
            org.setCanonicalName("org 2");
            orgs.add(org);

            PasswordPolicyEntity pp = new PasswordPolicyEntity();
            pp.setChangePasswdAtFirstLogin(true);
            pp.setPasswdRegex("someRegex");

            com1 = new TenantEntity();
            com1.setCanonicalName("ABC");
            com1.setOrganizations(orgs);
            com1.setPasswordPolicy(pp);

            tx.begin();
            em.persist(com1);
            tx.commit();
        }

        List<TenantEntity> communities = em.createNamedQuery(
                TenantEntity.FIND_ALL_TENANTS).getResultList();
        assertTrue(communities.size() == 1);
        assertTrue(em
                .createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS)
                .getResultList().size() == 2);

        com1 = communities.get(0);
        Long ppId = com1.getPasswordPolicy().getId();
        tx.begin();
        em.remove(com1);
        tx.commit();

        assertTrue(em.createNamedQuery(TenantEntity.FIND_ALL_TENANTS)
                .getResultList().size() == 0);
        assertTrue(em
                .createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS)
                .getResultList().size() == 0);
        assertNull(em.find(PasswordPolicyEntity.class, ppId));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
