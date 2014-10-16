package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class CountryEntityTest {

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

        CountryEntity country = new CountryEntity();
        country.setCanonicalName("India");

        em.persist(country);
        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<CountryEntity> countries = em.createNamedQuery(CountryEntity.FIND_ALL_COUNTRIES).getResultList();

        if (countries.size() > 0) {
            for (CountryEntity countrie1 : countries) {
                System.out.println("Country Details ...");
                System.out.println("Id " + countrie1.getCode());
                System.out.println("Name " + countrie1.getCanonicalName());
            }
        }

        assertTrue(countries.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
