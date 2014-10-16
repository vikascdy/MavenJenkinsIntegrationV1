package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class TimeZoneEntityTest {

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

        TimeZoneEntity time = new TimeZoneEntity();
        time.setCanonicalName("India");
        //	time.setCode("1");

        em.persist(time);
        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<TimeZoneEntity> timeZones = em.createNamedQuery(TimeZoneEntity.FIND_ALL_TIMEZONES).getResultList();

        if (timeZones.size() > 0) {
            for (TimeZoneEntity time1 : timeZones) {
                System.out.println("TimeZone Details ...");
                System.out.println("Id " + time1.getCode());
                System.out.println("Name " + time1.getCanonicalName());
            }
        }

        assertTrue(timeZones.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
