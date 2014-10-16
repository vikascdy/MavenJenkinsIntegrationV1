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

public class CredentialTypeEntityTest {

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

        List<CredentialTypeEntity> child = new ArrayList<CredentialTypeEntity>();

        CredentialTypeEntity parentCR = new CredentialTypeEntity();
        parentCR.setAuthentication(true);
        parentCR.setIdentification(true);
        parentCR.setCanonicalName("Parent");

        CredentialTypeEntity crType = new CredentialTypeEntity();
        crType.setAuthentication(true);
        crType.setIdentification(true);
        crType.setCanonicalName("Default 1");
        crType.setParentCredentialType(parentCR);

        CredentialTypeEntity crType2 = new CredentialTypeEntity();
        crType2.setAuthentication(true);
        crType2.setIdentification(true);
        crType2.setCanonicalName("Default 2");
        crType2.setParentCredentialType(parentCR);

        /* child.add(crType);
         child.add(crType2);
         parentCR.setChildCredentialTypes(child);
        */
        // em.persist(parentCR);
        em.persist(crType);
        em.persist(crType2);

        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<CredentialTypeEntity> crTypes = em
                .createNamedQuery(CredentialTypeEntity.FIND_ALL_CREDENTIAL_TYPES).getResultList();

        if (crTypes.size() > 0) {
            for (CredentialTypeEntity crType1 : crTypes) {
                System.out.println("CredentialType Details ...");
                System.out.println("Id " + crType1.getId());
                System.out.println("Name " + crType1.getCanonicalName());
                if (crType1.getParentCredentialType() != null) {
                    System.out.println("Parent Crendentials :");
                    System.out.println("Parent Cr ID " + crType1.getParentCredentialType().getId());
                }

            }
        }

        assertTrue(crTypes.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
