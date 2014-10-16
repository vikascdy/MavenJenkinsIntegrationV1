package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class RegionEntityTest {

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

        RegionEntity region = new RegionEntity();
        //region.setCode("1");
        region.setCanonicalName("India");

        em.persist(region);
        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<RegionEntity> regions = em.createNamedQuery(RegionEntity.FIND_ALL_REGIONS).getResultList();

        if (regions.size() > 0) {
            for (RegionEntity region1 : regions) {
                System.out.println("Region Details ...");
                System.out.println("Id " + region1.getCode());
                System.out.println("Name " + region1.getCanonicalName());
            }
        }

        assertTrue(regions.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
