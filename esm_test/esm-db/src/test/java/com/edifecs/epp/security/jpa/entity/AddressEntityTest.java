package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class AddressEntityTest {

    private static EntityManager     em;
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

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }

    @Test
    public void test() {

        RegionEntity region = new RegionEntity();
        // region.setCode("1");
        region.setCanonicalName("India");
        em.persist(region);

        CountryEntity country = new CountryEntity();
        // country.setCode("1");
        country.setCanonicalName("India");
        em.persist(country);

        AddressEntity add1 = new AddressEntity();
        add1.setCity("Mohali");
        // add1.setCode("1");
        add1.setPhone("12345");
        add1.setStreetAddress1("#4565");
        add1.setFax("123");
        add1.setCountry(country);
        add1.setRegion(region);

        em.persist(add1);

        tx.commit();

        List<AddressEntity> addresses = em.createNamedQuery(AddressEntity.FIND_ALL_ADDRESSES).getResultList();

        if (addresses.size() > 0) {
            for (AddressEntity addresse1 : addresses) {
                System.out.println("Address Details ...");
                System.out.println("Phone " + addresse1.getPhone());
                System.out.println("City " + addresse1.getCity());
                System.out.println("Street " + addresse1.getStreetAddress1());
                System.out.println("Fax " + addresse1.getFax());
                System.out.println("Country " + addresse1.getCountry().getCanonicalName());
                System.out.println("Region " + addresse1.getRegion().getCanonicalName());
            }
        }

        assertTrue(addresses.size() > 0);
    }

}
