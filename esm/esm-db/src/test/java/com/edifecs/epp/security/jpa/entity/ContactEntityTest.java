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

public class ContactEntityTest {

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

        SiteEntity site = new SiteEntity();

        TenantEntity tenant = new TenantEntity();
        tenant.setSite(site);
        tenant.setCanonicalName("ABC");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTenant(tenant);


        List<AddressEntity> addSet = new ArrayList<AddressEntity>();

        AddressEntity add1 = new AddressEntity();
        add1.setCity("Mohali");
        // add1.setCode("1");
        add1.setPhone("12345");
        add1.setStreetAddress1("#4565");

        AddressEntity add2 = new AddressEntity();
        add2.setCity("sdsadsa");
        // add2.setCode("2");
        add2.setPhone("12666345");
        add2.setStreetAddress1("#45sfds65");

        addSet.add(add1);
        addSet.add(add2);

        em.persist(add1);
        em.persist(add2);

        LanguageEntity lang = new LanguageEntity();
        // lang.setCode("1");
        lang.setCanonicalName("English");
        em.persist(lang);

        TimeZoneEntity time = new TimeZoneEntity();
        time.setCanonicalName("India");
        // time.setCode("1");
        em.persist(time);

        ContactEntity contact = new ContactEntity();
        // contact.setId("1");
        contact.setEmailAddress("a@a.com");
        contact.setFirstName("Abhijit");
        contact.setLastName("Singh");
        contact.setMiddleName("NA");
        contact.setSalutation("Mr.");
        contact.setAddresses(addSet);
        contact.setPreferredLanguage(lang);
        contact.setPreferredTimezone(time);
        
        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setDeleted(false);
        user.setContact(contact);
        user.setOrganization(organization);
        
        contact.setUser(user);

        em.persist(site);
        em.persist(tenant);
        em.persist(organization);
        em.persist(user);
        em.persist(contact);

        tx.commit();
        List<ContactEntity> contacts = em.createNamedQuery(ContactEntity.FIND_ALL_CONTACTS).getResultList();

        if (contacts.size() > 0) {
            for (ContactEntity contact1 : contacts) {
                System.out.println("Contact Details ...");
                System.out.println("First Name " + contact1.getFirstName());
                System.out.println("Email " + contact1.getEmailAddress());
                System.out.println("Phone " + contact1.getAddresses().get(0).getPhone());
                System.out.println("City " + contact1.getAddresses().get(0).getCity());
                System.out.println("Language " + contact1.getPreferredLanguage().getCanonicalName());
                System.out.println("TimeZone " + contact1.getPreferredTimezone().getCanonicalName());
            }
        }

        assertTrue(contacts.size() > 0);
    }

}
