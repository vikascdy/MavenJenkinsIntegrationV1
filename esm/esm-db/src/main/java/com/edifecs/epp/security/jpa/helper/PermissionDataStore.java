package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.datastore.IPermissionDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemCannotBeNullException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.entity.PermissionEntity;
import com.edifecs.epp.security.jpa.entity.RoleEntity;
import com.edifecs.epp.security.jpa.entity.UserEntity;
import com.edifecs.epp.security.jpa.entity.UserGroupEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import java.util.*;

class PermissionDataStore implements IPermissionDataStore {

    @Override
    public Collection<Permission> getAll() throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Permission> permissions = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<PermissionEntity> permissionsDB = entityManager
                    .createNamedQuery(PermissionEntity.FIND_ALL_PERMISSIONS)
                    .getResultList();

            for (PermissionEntity p : permissionsDB) {
                permissions.add((Permission) ObjectConverter.jpaToApi(p));
            }
            return permissions;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Permission> getPaginatedRange(long startRecord,
                                                       long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Permission> permissions = new ArrayList<>();
            int total = entityManager
                    .createNamedQuery(PermissionEntity.FIND_ALL_PERMISSIONS,
                            PermissionEntity.class).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList().size();

            List<PermissionEntity> permissionsDB = entityManager
                    .createNamedQuery(PermissionEntity.FIND_ALL_PERMISSIONS,
                            PermissionEntity.class)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (PermissionEntity p : permissionsDB) {
                permissions.add((Permission) ObjectConverter.jpaToApi(p));
            }
            return new PaginatedList<Permission>(permissions, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Permission getById(long id) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            PermissionEntity permissionDB = entityManager.find(
                    PermissionEntity.class, id);

            if (null != permissionDB) {
                return (Permission) ObjectConverter.jpaToApi(permissionDB);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Permission create(Permission permission, User auditor)
            throws ItemAlreadyExistsException, SecurityDataException {
        if (permission.getId() != null) {
             throw new ItemAlreadyExistsException("Permission ", permission.getId());
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            PermissionEntity perm = (PermissionEntity) ObjectConverter
                    .apiToJpa(permission);

            // Check to see if the permission already exists
            try {
                PermissionEntity pe = entityManager
                        .createNamedQuery(
                                PermissionEntity.FIND_PERMISSION_BY_NAMES,
                                PermissionEntity.class)
                        .setParameter("productCanonicalName",
                                perm.getProductCanonicalName())
                        .setParameter("categoryCanonicalName",
                                perm.getCategoryCanonicalName())
                        .setParameter("typeCanonicalName",
                                perm.getTypeCanonicalName())
                        .setParameter("subTypeCanonicalName",
                                perm.getSubTypeCanonicalName())
                        .setParameter("canonicalName", perm.getCanonicalName())
                        .getSingleResult();
                throw new ItemAlreadyExistsException("Permission ", pe.getId());
            } catch (NoResultException e) {
                perm.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                perm.setCreationDate(new Date());
                entityManager.persist(perm);
                permission.setId(perm.getId());
                return permission;
            }
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public Permission update(Permission permission, User auditor)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            com.edifecs.epp.security.jpa.entity.PermissionEntity permissionDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.PermissionEntity.class,
                            permission.getId());
            if (null != permissionDB) {
                ObjectConverter.copyCommonBeanProperties(permission,
                        permissionDB);
                permissionDB.setLastUpdatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                permissionDB.setLastUpdatedDate(new Date());
                return permission;
            } else {
                 throw new ItemNotFoundException("Permission ", permission.getId());
            }
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public void delete(Permission permission) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.remove(entityManager.find(PermissionEntity.class,
                    permission.getId()));
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Permission> getGroupedPermissions()
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            return entityManager
                    .createNamedQuery(
                            com.edifecs.epp.security.jpa.entity.PermissionEntity.FIND_ALL_PERMISSIONS)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Permission> getTransitivePermissionsForUser(Long userId,
                                                                     long startRecord, long recordCount) throws SecurityDataException {
        if (userId == null) {
             throw new ItemCannotBeNullException("UserId");
        }
        Collection<Permission> permissions = getTransitivePermissionsForUser(userId);
        int total = permissions.size();

        return new PaginatedList<Permission>(permissions, total);
    }

    @Override
    public PaginatedList<Permission> getPermissionsForRole(Role role,
                                                           long startRecord, long recordCount) throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            // FIXME: Create a single Named Query
            int total = entityManager
                    .createNamedQuery(
                            PermissionEntity.FIND_ALL_PERMISSIONS_FOR_ROLE,
                            PermissionEntity.class)
                    .setParameter("id", role.getId()).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList().size();

            List<com.edifecs.epp.security.jpa.entity.PermissionEntity> permissionsDB = entityManager
                    .createNamedQuery(
                            PermissionEntity.FIND_ALL_PERMISSIONS_FOR_ROLE,
                            PermissionEntity.class)
                    .setParameter("id", role.getId())
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            final Collection<Permission> permissions = new ArrayList<Permission>();
            for (com.edifecs.epp.security.jpa.entity.PermissionEntity p : permissionsDB) {
                permissions.add((Permission) ObjectConverter.jpaToApi(p));
            }
            return new PaginatedList<Permission>(permissions, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void addPermissionToRole(Role role, Permission permission)
            throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        if (permission == null) {
            throw new ItemCannotBeNullException("Permission");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            com.edifecs.epp.security.jpa.entity.RoleEntity roleDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.RoleEntity.class,
                            role.getId());
            com.edifecs.epp.security.jpa.entity.PermissionEntity permissionDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.PermissionEntity.class,
                            permission.getId());

            if (null != permissionDB && null != roleDB) {
                if (!roleDB.getPermissions().contains(permissionDB)) {
                    roleDB.getPermissions().add(permissionDB);
                    entityManager.flush();
                }
            }
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public void addPermissionsToRole(Role role, List<Permission> permissions)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            for (Permission permission : permissions) {
                com.edifecs.epp.security.jpa.entity.RoleEntity roleDB = entityManager
                        .find(com.edifecs.epp.security.jpa.entity.RoleEntity.class,
                                role.getId());
                com.edifecs.epp.security.jpa.entity.PermissionEntity permissionDB = entityManager
                        .find(com.edifecs.epp.security.jpa.entity.PermissionEntity.class,
                                permission.getId());

                if (null != permissionDB && null != roleDB) {
                    if (!roleDB.getPermissions().contains(permissionDB)) {
                        roleDB.getPermissions().add(permissionDB);
                        entityManager.flush();
                    }
                }
            }
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public void removePermissionFromRole(Role role, Permission permission)
            throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        if (permission == null) {
            return;
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            com.edifecs.epp.security.jpa.entity.PermissionEntity permissionDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.PermissionEntity.class,
                            permission.getId());

            com.edifecs.epp.security.jpa.entity.RoleEntity roleDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.RoleEntity.class,
                            role.getId());

            if (null != permissionDB && null != roleDB) {
                if (roleDB.getPermissions().contains(permissionDB)) {
                    roleDB.getPermissions().remove(permissionDB);
                    entityManager.flush();
                }
            }
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public void removePermissionsFromRole(Role role,
                                          Collection<Permission> permissions) throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Permission");
        }
        if (permissions == null) {
            return;
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            for (Permission permission : permissions) {
                com.edifecs.epp.security.jpa.entity.PermissionEntity permissionDB = entityManager
                        .find(com.edifecs.epp.security.jpa.entity.PermissionEntity.class,
                                permission.getId());

                com.edifecs.epp.security.jpa.entity.RoleEntity roleDB = entityManager
                        .find(com.edifecs.epp.security.jpa.entity.RoleEntity.class,
                                role.getId());

                if (null != permissionDB && null != roleDB) {
                    if (roleDB.getPermissions().contains(permissionDB)) {
                        roleDB.getPermissions().remove(permissionDB);
                        entityManager.flush();
                    }
                }
            }
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public Collection<Permission> getRange(long startRecord, long recordCount)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Permission> permissions = new ArrayList<Permission>();
            @SuppressWarnings("unchecked")
            List<PermissionEntity> permissionsDB = entityManager
                    .createNamedQuery(PermissionEntity.FIND_ALL_PERMISSIONS)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (PermissionEntity p : permissionsDB) {
                permissions.add((Permission) ObjectConverter.jpaToApi(p));
            }
            return permissions;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Permission> getTransitivePermissionsForUser(Long userId)
            throws SecurityDataException {
        if (userId == null) {
            throw new ItemCannotBeNullException("Permission");
        }
        // FIXME: Create a single Named Query
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {

            UserEntity u = entityManager.find(UserEntity.class, userId);

            Set<Long> roles = new HashSet<>();
            Set<Long> groups = new HashSet<>();
            Set<PermissionEntity> directPermissionsDB = new HashSet<>();
            Set<PermissionEntity> transPermissionsDB = new HashSet<>();

            for (RoleEntity r : u.getRoles()) {
                directPermissionsDB.addAll(r.getPermissions());
                transPermissionsDB.addAll(getTransitivePermissions(r, groups, roles));
            }
            for (UserGroupEntity g : u.getGroups()) {
                transPermissionsDB.addAll(getTransitivePermissions(g, groups, roles));
            }

            final Collection<Permission> permissions = new ArrayList<Permission>();
            for (com.edifecs.epp.security.jpa.entity.PermissionEntity p : directPermissionsDB) {
                Permission perm = (Permission) ObjectConverter.jpaToApi(p);
                perm.setPermissionType(RolePermissionType.DIRECT);
                permissions.add(perm);

                transPermissionsDB.remove(p);
            }

            for (com.edifecs.epp.security.jpa.entity.PermissionEntity p : transPermissionsDB) {
                Permission perm = (Permission) ObjectConverter.jpaToApi(p);
                perm.setPermissionType(RolePermissionType.TRANSITIVE);
                permissions.add(perm);
            }

            return permissions;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Permission> getPermissionsForRole(Role role)
            throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            com.edifecs.epp.security.jpa.entity.RoleEntity roleDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.RoleEntity.class,
                            role.getId());

            if (null != roleDB) {
                List<com.edifecs.epp.security.jpa.entity.PermissionEntity> permissionsDB = roleDB
                        .getPermissions();

                final Collection<Permission> permissions = new ArrayList<Permission>();
                for (com.edifecs.epp.security.jpa.entity.PermissionEntity p : permissionsDB) {
                    permissions.add((Permission) ObjectConverter.jpaToApi(p));
                }
                return permissions;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Permission> getTransitivePermissionsForRole(Role role)
            throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Permission");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            RoleEntity roleDB = entityManager.find(RoleEntity.class, role.getId());

            Set<Long> roles = new HashSet<>();
            Set<Long> groups = new HashSet<>();
//            Set<PermissionEntity> permissionsDB = new HashSet<>();
            Set<PermissionEntity> directPermissionsDB = new HashSet<>();
            Set<PermissionEntity> transPermissionsDB = new HashSet<>();
            directPermissionsDB.addAll(roleDB.getPermissions());

            for (RoleEntity r : roleDB.getRoles()) {
                transPermissionsDB.addAll(getTransitivePermissions(r, groups, roles));
            }

            final Collection<Permission> permissions = new ArrayList<Permission>();
            for (com.edifecs.epp.security.jpa.entity.PermissionEntity p : directPermissionsDB) {
                Permission perm = (Permission) ObjectConverter.jpaToApi(p);
                perm.setPermissionType(RolePermissionType.DIRECT);
                permissions.add(perm);

                transPermissionsDB.remove(p);
            }

            for (com.edifecs.epp.security.jpa.entity.PermissionEntity p : transPermissionsDB) {
                Permission perm = (Permission) ObjectConverter.jpaToApi(p);
                perm.setPermissionType(RolePermissionType.DIRECT);
                permissions.add(perm);
            }
            return permissions;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Permission getPermission(Permission permission) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Permission> permissions = new ArrayList<>();
            @SuppressWarnings("unchecked")
            PermissionEntity p = (PermissionEntity) entityManager
                    .createNamedQuery(PermissionEntity.FIND_PERMISSION_BY_NAMES)
                    .setParameter("productCanonicalName", permission.getProductCanonicalName())
                    .setParameter("categoryCanonicalName", permission.getCategoryCanonicalName())
                    .setParameter("typeCanonicalName", permission.getTypeCanonicalName())
                    .setParameter("subTypeCanonicalName", permission.getSubTypeCanonicalName())
                    .setParameter("canonicalName", permission.getCanonicalName())
                    .getSingleResult();

            return (Permission) ObjectConverter.jpaToApi(p);
        } finally {
            entityManager.close();
        }
    }

    private Set<PermissionEntity> getTransitivePermissions(RoleEntity role, Set<Long> groups, Set<Long> roles) {
        Set<PermissionEntity> permissions = new HashSet<>();
        permissions.addAll(role.getPermissions());
        roles.add(role.getId());
        for (RoleEntity r : role.getRoles()) {
            if (!roles.contains(r.getId())) {
                permissions.addAll(getTransitivePermissions(r, groups, roles));
            }
        }
        for (UserGroupEntity g : role.getGroups()) {
            if (!groups.contains(g.getId())) {
                permissions.addAll(getTransitivePermissions(g, groups, roles));
            }
        }
        return permissions;
    }

    private Set<PermissionEntity> getTransitivePermissions(UserGroupEntity group, Set<Long> groups, Set<Long> roles) {
        Set<PermissionEntity> permissions = new HashSet<>();
        groups.add(group.getId());
        for (RoleEntity r : group.getRoles()) {
            if (!roles.contains(r.getId())) {
                permissions.addAll(getTransitivePermissions(r, groups, roles));
            }
        }
        for (UserGroupEntity g : group.getChildGroups()) {
            if (!groups.contains(g.getId())) {
                permissions.addAll(getTransitivePermissions(g, groups, roles));
            }
        }
        return permissions;
    }

    @Override
	public PermissionEntity importPermissions(PermissionEntity permEntity) throws ItemAlreadyExistsException, SecurityDataException {
        if (permEntity.getId() != null) {
            throw new ItemAlreadyExistsException("Permission ", permEntity.getId());
       }
       EntityManager entityManager = DatabaseDataStore.createEntityManager();
       EntityTransaction tx = entityManager.getTransaction();
       try {
           tx.begin();
           // Check to see if the permission already exists
           try {
               PermissionEntity pe = entityManager
                       .createNamedQuery(
                               PermissionEntity.FIND_PERMISSION_BY_NAMES,
                               PermissionEntity.class)
                       .setParameter("productCanonicalName",
                    		   permEntity.getProductCanonicalName())
                       .setParameter("categoryCanonicalName",
                    		   permEntity.getCategoryCanonicalName())
                       .setParameter("typeCanonicalName",
                    		   permEntity.getTypeCanonicalName())
                       .setParameter("subTypeCanonicalName",
                    		   permEntity.getSubTypeCanonicalName())
                       .setParameter("canonicalName", permEntity.getCanonicalName())
                       .getSingleResult();
               throw new ItemAlreadyExistsException("Permission ", pe.getId());
           } catch (NoResultException e) {
        	   permEntity.setCreationDate(new Date());
               entityManager.persist(permEntity);
               permEntity.setId(permEntity.getId());
               return permEntity;
           }
       } catch (Exception e) {
           tx.rollback();
           throw e;
       } finally {
           if (tx.isActive()) {
               tx.commit();
           }
           entityManager.close();
       }
   }
}
