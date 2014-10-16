package com.edifecs.epp.security.jpa.entity;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.edifecs.core.configuration.configuration.Scope;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.security.jpa.entity.utility.JPAUtility;

public class PropertyEntityTest {

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

        SiteEntity site = new SiteEntity();

        TenantEntity tenant = new TenantEntity();
        tenant.setSite(site);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTenant(tenant);

    	ContactEntity contact = new ContactEntity();
        contact.setFirstName("aaa");
        contact.setLastName("bbb");
        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setContact(contact);
        user.setOrganization(organization);
        tx.begin();
        em.persist(site);
        em.persist(tenant);
        em.persist(organization);
        em.persist(user);
        tx.commit();
        
        PropertyEntity property = new PropertyEntity();
		property.setName("property1");
		property.setValue("value1");
		property.setOwnerId(user.getId());
		property.setOwnerScope(Scope.USER);
        tx.begin();
        em.persist(property);
        tx.commit();

        List<PropertyEntity> properties = em.createNamedQuery(PropertyEntity.FIND_ALL_PROPERTIES).getResultList();
        assertTrue(properties.size() == 1);
        assertNotNull(properties.get(0).getId());
    }
    
    
    @Test
    public void testDelete() {
    	PropertyEntity property = null;
    	
        if (em.createNamedQuery(PropertyEntity.FIND_ALL_PROPERTIES).getResultList().size() == 0) {
        	ContactEntity contact = new ContactEntity();
            contact.setFirstName("aaa");
            contact.setLastName("bbb");
            UserEntity user = new UserEntity();
            user.setActive(true);
            user.setContact(contact);
            tx.begin();
            em.persist(user);
            tx.commit();
            
            PropertyEntity p = new PropertyEntity();
    		p.setName("property1");
    		p.setValue("value1");
    		p.setOwnerId(user.getId());
    		p.setOwnerScope(Scope.USER);
    		
            tx.begin();
            em.persist(p);
            tx.commit();
        }

        List<PropertyEntity> properties = em.createNamedQuery(PropertyEntity.FIND_ALL_PROPERTIES).getResultList();
        assertTrue(properties.size() == 1);
        
        property = properties.get(0);
        tx.begin();
        em.remove(property);
        tx.commit();
        
        assertTrue(em.createNamedQuery(PropertyEntity.FIND_ALL_PROPERTIES).getResultList().size() == 0);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
