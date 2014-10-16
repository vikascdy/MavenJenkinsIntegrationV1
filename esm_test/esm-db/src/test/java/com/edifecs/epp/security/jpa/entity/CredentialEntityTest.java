package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.edifecs.core.configuration.helper.SystemVariables;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.data.token.CertificateAuthenticationToken;
import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;
import com.edifecs.epp.security.utils.JKSKeyStoreManager;

public class CredentialEntityTest {

    private static EntityManager em;
    private static EntityTransaction tx;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        em = JPAUtility.getEntityManager();
        if (em != null) {
            System.out.println("Entity Manager Created Successfully.");
        }
    }

    @Test
    public void usernamePasswordTest() {

        SiteEntity site = new SiteEntity();

        TenantEntity tenant = new TenantEntity();
        tenant.setSite(site);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTenant(tenant);

        System.out.println("Starting Transaction...");
        tx = em.getTransaction();
        tx.begin();
        System.out.println("Transaction Started...");

        Calendar cal = Calendar.getInstance();

        ContactEntity contact = new ContactEntity();
        //contact.setId("1");
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
        //credential2.setId("2");
        credential2.setCredentialKey("check");
        credential2.setCredentialBinary(null);
        credential2.setCredentialType(crType);
        credential2.setExpiresDateTime(cal.getTime());
        credential2.setLastChangesDateTime(cal.getTime());
        credential2.setUser(user);

        CredentialEntity credential = new CredentialEntity();
        credential.setCredentialKey("check2");
        credential.setCredentialBinary(null);
        credential.setSecondaryCredential(credential2);
        credential.setCredentialType(crType);
        credential.setExpiresDateTime(cal.getTime());
        credential.setLastChangesDateTime(cal.getTime());
        credential.setUser(user);
        
        user.getCredentials().add(credential);

        em.persist(site);
        em.persist(tenant);
        em.persist(organization);
        em.persist(contact);
        em.persist(user);
        em.persist(crType);
        em.persist(credential);

        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<CredentialEntity> credentials = em.createNamedQuery(CredentialEntity.FIND_ALL_CREDENTIALS)
                .getResultList();

        if (credentials.size() > 0) {
            for (CredentialEntity cr : credentials) {
                System.out.println("Credential Details ...");
                System.out.println("Id " + cr.getId());
                System.out.println("Name " + cr.getCredentialKey());
                System.out.println("Exp Date " + cr.getExpiresDateTime());
                if (cr.getSecondaryCredential() != null) {
                    System.out.println("Alternative Crendentials :");
                    System.out.println("Alt Cr ID " + cr.getSecondaryCredential().getId());
                }

            }
        }

        assertTrue(credentials.size() > 0);

    }
    
    @Test
    public void certificateTest() throws InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchPaddingException, UnrecoverableEntryException, IllegalBlockSizeException, BadPaddingException, IOException {

        SiteEntity site = new SiteEntity();

        TenantEntity tenant = new TenantEntity();
        tenant.setSite(site);
        tenant.setDomain(SystemVariables.DEFAULT_TENANT_NAME);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTenant(tenant);

        System.out.println("Starting Transaction...");
        tx = em.getTransaction();
        tx.begin();
        System.out.println("Transaction Started...");
        
        Calendar cal = Calendar.getInstance();

        ContactEntity contact = new ContactEntity();
        //contact.setId("1");
        contact.setEmailAddress("b@b.com");
        contact.setFirstName("Some");
        contact.setLastName("Account");
        contact.setMiddleName("System");

        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setDeleted(false);
        user.setContact(contact);
        user.setOrganization(organization);

        JKSKeyStoreManager keyStore = new JKSKeyStoreManager(getClass().getResourceAsStream("/keystore.jks"), "changeit");
        
        CertificateAuthenticationToken token = new CertificateAuthenticationToken(SystemVariables.DEFAULT_TENANT_NAME,
                SystemVariables.DEFAULT_ORG_NAME,
                keyStore.getRSAEncodedKey("security-system"), SystemVariables.DEFAULT_SYSTEM_USER);
        
        CredentialEntity credential = new CredentialEntity();
        credential.setCredentialKey(token.getKeyLookup());
        credential.setCredentialBinary(token.getKey());
        credential.setUser(user);
        
        user.getCredentials().add(credential);

        em.persist(site);
        em.persist(tenant);
        em.persist(organization);
        em.persist(credential);
        em.persist(user);
        em.persist(contact);

        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        UserEntity resultUser = (UserEntity) em.createNamedQuery(UserEntity.FIND_USER_BY_CREDENTIAL_KEY)
                .setParameter("domain", token.getDomain())
                .setParameter("credentialKey", credential.getCredentialKey())
                .getSingleResult();

        System.out.println("User Details ...");
        System.out.println("Id " + resultUser.getId());
        System.out.println("Name " + resultUser.getCredentials().get(0).getCredentialKey());
        System.out.println("Exp Date " + resultUser.getCredentials().get(0).getExpiresDateTime());

        Assert.assertEquals(credential.getCredentialBinary(), resultUser.getCredentials().get(0).getCredentialBinary());

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
