package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class LanguageEntityTest {

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

        LanguageEntity lang = new LanguageEntity();
        //	lang.setCode("1");
        lang.setCanonicalName("English");

        em.persist(lang);
        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<LanguageEntity> languages = em.createNamedQuery(LanguageEntity.FIND_ALL_LANGUAGES).getResultList();

        if (languages.size() > 0) {
            for (LanguageEntity language1 : languages) {
                System.out.println("Language Details ...");
                System.out.println("Id " + language1.getCode());
                System.out.println("Name " + language1.getCanonicalName());
            }
        }

        assertTrue(languages.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
