package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class CredentialHistoryEntityTest {

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

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTenant(tenant);

        Calendar cal = Calendar.getInstance();
        ContactEntity contact = new ContactEntity();
        // contact.setId("1");
        contact.setEmailAddress("a@a.com");
        contact.setFirstName("Abhijit");
        contact.setLastName("Singh");
        contact.setMiddleName("NA");
        contact.setSalutation("Mr.");

        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setDeleted(false);
        user.setContact(contact);
        user.setOrganization(organization);

        CredentialTypeEntity crType = new CredentialTypeEntity();
        crType.setAuthentication(true);
        crType.setIdentification(true);
        crType.setCanonicalName("Default 1");

        CredentialEntity credential2 = new CredentialEntity();
        //  credential2.setId("2");
        credential2.setCredentialKey("check");
        credential2.setCredentialBinary(null);
        credential2.setCredentialType(crType);
        credential2.setExpiresDateTime(cal.getTime());
        credential2.setLastChangesDateTime(cal.getTime());
        credential2.setUser(user);

        CredentialHistoryEntity credential = new CredentialHistoryEntity();
        //credential.setId("1");
        credential.setCredentialKey("check2");
        credential.setCredentialBinary(null);
        credential.setCredentialType(crType);
        credential.setUser(user);

        CredentialHistoryEntity credentialHistory = new CredentialHistoryEntity();
        credentialHistory.setSecondaryCredentialHistory(credential);
        credentialHistory.setCredentialBinary(null);
        credentialHistory.setCredentialKey("NOT AOO");
        credentialHistory.setHistoryDateTime(cal.getTime());
        credentialHistory.setCredentialType(crType);
        credentialHistory.setUser(user);

        em.persist(site);
        em.persist(tenant);
        em.persist(organization);
        em.persist(user);
        em.persist(contact);
        em.persist(crType);
        em.persist(credential);
        em.persist(credentialHistory);

        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<CredentialHistoryEntity> crhs = em.createNamedQuery(
                CredentialHistoryEntity.FIND_ALL_CREDENTIAL_HISTORIES).getResultList();

        if (crhs.size() > 0) {
            for (CredentialHistoryEntity cr : crhs) {
                System.out.println("CredentialHistory Details ...");
                System.out.println("Id " + cr.getId());
                System.out.println("Name " + cr.getCredentialKey());
                System.out.println("History Date " + cr.getHistoryDateTime());
                System.out.println("Cr Type Name " + cr.getCredentialType().getCanonicalName());
                System.out.println("User Id " + cr.getUser().getId());
                if (cr.getSecondaryCredentialHistory() != null) {
                    System.out.println("Alternative Crendentials :");
                    System.out.println("Alt Cr ID " + cr.getSecondaryCredentialHistory().getId());
                }

            }
        }

        assertTrue(crhs.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
