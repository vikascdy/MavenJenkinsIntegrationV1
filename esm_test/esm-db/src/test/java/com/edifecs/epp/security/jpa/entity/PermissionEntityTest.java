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

public class PermissionEntityTest {

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

        List<RoleEntity> roles = new ArrayList<RoleEntity>();

        RoleEntity role = new RoleEntity();
        role.setCanonicalName("ADMIN");

        RoleEntity role2 = new RoleEntity();
        role2.setCanonicalName("ADMIN2");

        roles.add(role);
        roles.add(role2);

        PermissionEntity permission = new PermissionEntity();
        permission.setCanonicalName("perm 1 new");
        permission.setCategoryCanonicalName("Category #2");
        permission.setProductCanonicalName("Product #2");
        permission.setTypeCanonicalName("Type #2");
        permission.setSubTypeCanonicalName("SubType #2");
        permission.setSortOrder(1L);
        permission.setRoles(roles);

        em.persist(role);
        em.persist(role2);
        em.persist(permission);

        tx.commit();

        System.out.println("Transaction Committed...");

        // check named query

        List<PermissionEntity> permissions = em.createNamedQuery(PermissionEntity.FIND_ALL_PERMISSIONS)
                .getResultList();

        if (permissions.size() > 0) {
            for (PermissionEntity permission1 : permissions) {
                System.out.println("permission Details ...");
                System.out.println("Id " + permission1.getId());
                System.out.println("permission Name " + permission1.getCanonicalName());
                System.out.println("Roles  " + permission1.getRoles().get(0).getCanonicalName());
            }
        }

        assertTrue(permissions.size() > 0);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        JPAUtility.shutdown();
        System.out.println("Entity Manager closed...");
    }
}
