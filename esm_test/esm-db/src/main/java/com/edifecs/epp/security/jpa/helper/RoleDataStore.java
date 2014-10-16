package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.datastore.IRoleDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemCannotBeNullException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.entity.*;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.RolesValidator;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.*;

class RoleDataStore implements IRoleDataStore {

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Role> getAll() throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Role> roles = new ArrayList<>();
            List<RoleEntity> rolesDB = entityManager.createNamedQuery(
                    RoleEntity.FIND_ALL_ROLES).getResultList();

            for (RoleEntity r : rolesDB) {
                roles.add((Role) ObjectConverter.jpaToApi(r));
            }
            return roles;
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PaginatedList<Role> getPaginatedRange(long startRecord,
                                                 long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Role> roles = new ArrayList<>();
            int total = entityManager
                    .createNamedQuery(RoleEntity.FIND_ALL_ROLES)
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<RoleEntity> rolesDB = entityManager
                    .createNamedQuery(RoleEntity.FIND_ALL_ROLES)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (RoleEntity r : rolesDB) {
                roles.add((Role) ObjectConverter.jpaToApi(r));
            }
            return new PaginatedList<Role>(roles, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Role getById(long id) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            RoleEntity roleDB = entityManager.find(RoleEntity.class, id);

            if (null != roleDB) {
                return (Role) ObjectConverter.jpaToApi(roleDB);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Role> queryRoles(String seed, long startRecord, long maxRecords)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<RoleEntity> criteriaQuery = builder
                    .createQuery(RoleEntity.class);
            Root<RoleEntity> root = criteriaQuery.from(RoleEntity.class);
            criteriaQuery.where(builder.like(
                    builder.lower(root.get("canonicalName").as(String.class)),
                    "%" + seed.toLowerCase() + "%"));
            criteriaQuery.distinct(true);

            List<Role> roles = new ArrayList<>();

            List<RoleEntity> roleDBList = entityManager
                    .createQuery(criteriaQuery)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) maxRecords).getResultList();

            for (RoleEntity roleDB : roleDBList) {
                roles.add((Role) ObjectConverter.jpaToApi(roleDB));
            }

            return roles;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Role create(Long tenantId, Role role, User auditor) throws ItemAlreadyExistsException,
            SecurityDataException {
        if (role.getId() != null) {
		    throw new ItemAlreadyExistsException("Role", role.getCanonicalName());
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            //check for duplicate orgs in same tenant
            try { 
                RoleEntity rolezDB = entityManager.createNamedQuery(RoleEntity.FIND_ROLE_BY_NAME_TENANT_ID,
                        RoleEntity.class)
                        .setParameter("roleName", role.getCanonicalName().toLowerCase())
                        .setParameter("tenantId", tenantId)
                        .getSingleResult();
                if (null != rolezDB) {
                	String tenantName = String.valueOf(tenantId);
                	if (rolezDB.getTenant() != null){
                		tenantName = rolezDB.getTenant().getCanonicalName();
                	}
                    throw new ItemAlreadyExistsException("Role", role.getCanonicalName());
                }
            } catch (NoResultException e) {
                // do nothing
            }
            RoleEntity roleDB = (RoleEntity) ObjectConverter.apiToJpa(role);
            roleDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
            roleDB.setCreationDate(new Date());
            TenantEntity tenant = new TenantEntity();
            tenant.setId(tenantId);
            roleDB.setTenant(tenant);

            entityManager.persist(roleDB);

            role.setId(roleDB.getId());
            return role;
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
    public Role update(Long tenantId, Role role, User auditor) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            RoleEntity roleDB = entityManager.find(RoleEntity.class, role.getId());
            if (!roleDB.getCanonicalName().equalsIgnoreCase(role.getCanonicalName())) {
                //check for duplicate orgs in same tenant
                try {
                	RoleEntity rolezDB = entityManager.createNamedQuery(RoleEntity.FIND_ROLE_BY_NAME_TENANT_ID,
                            RoleEntity.class)
                            .setParameter("tenantId", tenantId)
                            .setParameter("roleName", role.getCanonicalName().toLowerCase())
                            .getSingleResult();
                	if (null != rolezDB) {
                        throw new ItemAlreadyExistsException("Role", role.getCanonicalName());
                    }
                } catch (NoResultException e) {
                    // do nothing
                }
            }
            if (null != roleDB) {
                ObjectConverter.copyCommonBeanProperties(role, roleDB);
                roleDB.setLastUpdatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                roleDB.setLastUpdatedDate(new Date());
                return role;
            } else {
    		    throw new ItemNotFoundException("Role", role.getCanonicalName());
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
    public void delete(Role role) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    role.getId());

            List<UserEntity> users = entityManager
                    .createNamedQuery(UserEntity.FIND_USERS_BY_ROLE,
                            UserEntity.class)
                    .setParameter("roleId", role.getId()).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList();

            List<UserGroupEntity> groups = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_GROUPS_BY_ROLE,
                            UserGroupEntity.class)
                    .setParameter("roleId", role.getId()).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList();

            if (!users.isEmpty())
                for (UserEntity u : users) {
                    for (Iterator<RoleEntity> it = u.getRoles().iterator(); it
                            .hasNext(); ) {
                        RoleEntity r = it.next();
                        if (r.getId() == role.getId()) {
                            it.remove();
                        }
                    }
                }

            if (!groups.isEmpty())
                for (UserGroupEntity g : groups) {
                    for (Iterator<RoleEntity> it = g.getRoles().iterator(); it
                            .hasNext(); ) {
                        RoleEntity r = it.next();
                        if (r.getId() == role.getId()) {
                            it.remove();
                        }
                    }
                }

            if (!roleDB.getPermissions().isEmpty())
                roleDB.getPermissions().clear();

            entityManager.remove(roleDB);

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
    public Role getRoleByRoleName(String roleName) {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            RoleEntity role = (RoleEntity) entityManager
                    .createNamedQuery(RoleEntity.FIND_ROLE_BY_ROLE_NAME)
                    .setParameter("roleName", roleName).getSingleResult();

            return (Role) ObjectConverter.jpaToApi(role);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Role> getRolesForUser(User user, long startRecord,
                                               long recordCount) throws SecurityDataException {
        if (user == null) {
		    throw new ItemCannotBeNullException("User");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            UserEntity u = entityManager.find(UserEntity.class, user.getId());
            final Collection<Role> roles = new ArrayList<Role>();
            for (RoleEntity r : u.getRoles()) {
                roles.add((Role) ObjectConverter.jpaToApi(r));
            }
            return new PaginatedList<Role>(roles, roles.size());
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Role> getTransitiveRolesForUser(User user,
                                                         long startRecord, long recordCount) throws SecurityDataException {
        if (user == null) {
            throw new ItemCannotBeNullException("User");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            UserEntity u = entityManager.find(UserEntity.class, user.getId());
            Set<RoleEntity> rolesDB = new HashSet<>();
            Set<RoleEntity> transRolesDB = new HashSet<>();
            Set<RoleEntity> directRolesDB = new HashSet<>();
            Set<Long> groupsTraversed = new HashSet<>();
            Set<Long> rolesTraversed = new HashSet<>();

            directRolesDB.addAll(u.getRoles());
            for (UserGroupEntity g : u.getGroups()) {
                transRolesDB.addAll(getRolesFromUserGroup(g, groupsTraversed));
            }

//            transRolesDB.addAll(getRolesFromOrganization(u.getOrganization(), orgsTraversed));

            for (UserGroupEntity og : u.getOrganization().getGroups()) {
                transRolesDB.addAll(getRolesFromUserGroup(og, groupsTraversed));
            }

            rolesDB.addAll(directRolesDB);
            rolesDB.addAll(transRolesDB);
            Set<RoleEntity> childRoles = new HashSet<>();
            for (RoleEntity r : rolesDB) {
                childRoles.addAll(getRolesFromRole(r, rolesTraversed));
            }

            transRolesDB.addAll(childRoles);
            final Collection<Role> roles = new ArrayList<Role>();
            for (RoleEntity r : directRolesDB) {
                Role role = (Role) ObjectConverter.jpaToApi(r);
                role.setRoleType(RolePermissionType.DIRECT);
                roles.add(role);

                // remove duplicates from transitive if any
                transRolesDB.remove(r);
            }
            for (RoleEntity r : transRolesDB) {
                Role role = (Role) ObjectConverter.jpaToApi(r);
                role.setRoleType(RolePermissionType.TRANSITIVE);
                roles.add(role);
            }

            return new PaginatedList<Role>(roles, roles.size());
        } finally {
            entityManager.close();
        }
    }

    private Set<? extends RoleEntity> getRolesFromUserGroup(
            UserGroupEntity group, Set<Long> groupsTraversed) {
        groupsTraversed.add(group.getId());
        Set<RoleEntity> roles = new HashSet<>();
        roles.addAll(group.getRoles());
        for (UserGroupEntity g : group.getChildGroups())
            if (!groupsTraversed.contains(g.getId()))
                roles.addAll(getRolesFromUserGroup(g, groupsTraversed));
        return roles;
    }

    private Set<? extends RoleEntity> getRolesFromOrganization(
            OrganizationEntity org, Set<Long> orgsTraversed) {
        orgsTraversed.add(org.getId());
        Set<RoleEntity> roles = new HashSet<>();
        roles.addAll(org.getRoles());
        for (OrganizationEntity o : org.getChildOrganizations())
            if (!orgsTraversed.contains(o.getId()))
                roles.addAll(getRolesFromOrganization(o, orgsTraversed));
        return roles;
    }

    private Set<? extends RoleEntity> getRolesFromRole(RoleEntity role,
                                                       Set<Long> rolesTraversed) {
        rolesTraversed.add(role.getId());
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        for (RoleEntity r : role.getRoles())
            if (!rolesTraversed.contains(r.getId())) {
                roles.addAll(getRolesFromRole(r, rolesTraversed));
            }
        return roles;
    }

    @Override
    public PaginatedList<Role> getChildRolesForRole(Role role,
                                                    long startRecord, long recordCount) throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {

            int total = entityManager
                    .createNamedQuery(RoleEntity.FIND_CHILD_ROLES,
                            RoleEntity.class).setParameter("id", role.getId())
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<RoleEntity> rolesDB = entityManager
                    .createNamedQuery(RoleEntity.FIND_CHILD_ROLES,
                            RoleEntity.class).setParameter("id", role.getId())
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            final Collection<Role> roles = new ArrayList<Role>();
            for (RoleEntity r : rolesDB) {
                roles.add((Role) ObjectConverter.jpaToApi(r));
            }
            return new PaginatedList<Role>(roles, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Role> getRolesForGroup(UserGroup group,
                                                long startRecord, long recordCount) throws SecurityDataException {
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            int total = entityManager
                    .createNamedQuery(RoleEntity.FIND_ROLE_BY_GROUP_ID,
                            RoleEntity.class).setParameter("id", group.getId())
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<RoleEntity> rolesDB = entityManager
                    .createNamedQuery(RoleEntity.FIND_ROLE_BY_GROUP_ID,
                            RoleEntity.class).setParameter("id", group.getId())
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            final Collection<Role> roles = new ArrayList<Role>();
            for (RoleEntity r : rolesDB) {
                roles.add((Role) ObjectConverter.jpaToApi(r));
            }
            return new PaginatedList<Role>(roles, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void addRoleToUser(User user, Role role)
            throws SecurityDataException {
        if (user == null) {
            throw new ItemCannotBeNullException("User");
        }
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            UserEntity userDB = entityManager.find(UserEntity.class,
                    user.getId());

            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    role.getId());

            if (null != userDB && null != roleDB) {

                if (!userDB.getRoles().contains(roleDB)) {
                    userDB.getRoles().add(roleDB);
                    roleDB.getUsers().add(userDB);
                    userDB.setModifiedDateTime(new Date());
                }
            }

            entityManager.flush();

        } catch (Exception e) {
            e.printStackTrace();
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
    public void addRoleToGroup(UserGroup group, Role role)
            throws SecurityDataException {
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    role.getId());

            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class,
                    group.getId());

            if (null != roleDB && null != groupDB) {

                if (!groupDB.getRoles().contains(roleDB)) {
                    groupDB.getRoles().add(roleDB);
                    roleDB.getGroups().add(groupDB);
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
    public void addRolesToUser(User user, Collection<Role> roles)
            throws SecurityDataException {
        if (user == null) {
            throw new ItemCannotBeNullException("User");
        }
        if (roles == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            for (Role role : roles) {
                UserEntity userDB = entityManager.find(UserEntity.class,
                        user.getId());

                RoleEntity roleDB = entityManager.find(RoleEntity.class,
                        role.getId());

                if (null != userDB && null != roleDB) {

                    if (!userDB.getRoles().contains(roleDB)) {
                        userDB.getRoles().add(roleDB);
                        roleDB.getUsers().add(userDB);
                    }
                }
                userDB.setModifiedDateTime(new Date());
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
    public void addRolesToGroup(UserGroup group, Collection<Role> roles)
            throws SecurityDataException {
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        if (roles == null) {
            throw new ItemCannotBeNullException("Roles");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            for (Role role : roles) {
                RoleEntity roleDB = entityManager.find(RoleEntity.class,
                        role.getId());

                UserGroupEntity groupDB = entityManager.find(
                        UserGroupEntity.class, group.getId());

                if (null != roleDB && null != groupDB) {

                    if (!groupDB.getRoles().contains(roleDB)) {
                        groupDB.getRoles().add(roleDB);
                        roleDB.getGroups().add(groupDB);
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
    public void removeRoleFromUser(User user, Role role)
            throws SecurityDataException {
        if (user == null) {
            throw new ItemCannotBeNullException("User");
        }
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            UserEntity userDB = entityManager.find(UserEntity.class,
                    user.getId());

            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    role.getId());

            if (null != userDB && null != roleDB) {
                if (userDB.getRoles().contains(roleDB)) {
                    userDB.getRoles().remove(roleDB);
                    userDB.setModifiedDateTime(new Date());
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
    public void removeRoleFromGroup(UserGroup group, Role role)
            throws SecurityDataException {
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    role.getId());

            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class,
                    group.getId());

            if (null != roleDB && null != groupDB) {

                if (groupDB.getRoles().contains(roleDB)) {
                    groupDB.getRoles().remove(roleDB);
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
    public void addChildRoleToRole(Role role, Role parentRole)
            throws SecurityDataException {

        if (role == null || parentRole == null) {
            return;
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    parentRole.getId());

            RoleEntity childRole = entityManager.find(RoleEntity.class,
                    role.getId());

            if (null != roleDB && null != childRole) {
                roleDB.getRoles().add(childRole);
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
    public void removeChildRoleFromRole(Role role, Role parentRole)
            throws SecurityDataException {
        if (parentRole == null) {
            throw new ItemCannotBeNullException("Parent Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    parentRole.getId());

            RoleEntity childRole = entityManager.find(RoleEntity.class,
                    role.getId());

            if (null != roleDB && null != childRole) {
                roleDB.getRoles().remove(childRole);
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

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Role> getRange(long startRecord, long recordCount)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Role> roles = new ArrayList<>();
            List<RoleEntity> rolesDB = entityManager
                    .createNamedQuery(RoleEntity.FIND_ALL_ROLES)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (RoleEntity r : rolesDB) {
                roles.add((Role) ObjectConverter.jpaToApi(r));
            }
            return roles;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Role> getRolesForUser(User user)
            throws SecurityDataException {
        if (user == null) {
            throw new ItemCannotBeNullException("User");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            UserEntity u = entityManager.find(UserEntity.class, user.getId());
            Set<RoleEntity> rolesDB = new java.util.HashSet<>();
            rolesDB.addAll(u.getRoles());

            for (UserGroupEntity g : u.getGroups()) {
                rolesDB.addAll(g.getRoles());
                for (UserGroupEntity cg : g.getChildGroups()) {
                    rolesDB.addAll(cg.getRoles());
                }
            }

            List<RoleEntity> childRoles = new ArrayList<>();
            for (RoleEntity r : rolesDB) {
                if (!r.getRoles().isEmpty())
                    childRoles.addAll(r.getRoles());
            }

            rolesDB.addAll(childRoles);
            ListIterator<RoleEntity> it = new ArrayList<RoleEntity>(rolesDB)
                    .listIterator();

            final Collection<Role> roles = new ArrayList<Role>();
            while (it.hasNext()) {
                roles.add((Role) ObjectConverter.jpaToApi(it.next()));
            }
            return roles;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Role> getChildRolesForRole(Role role)
            throws SecurityDataException {
        if (role == null) {
            throw new ItemCannotBeNullException("Role");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            RoleEntity roleDB = entityManager.find(RoleEntity.class,
                    role.getId());

            if (null != roleDB) {
                final Collection<Role> roles = new ArrayList<Role>();
                for (RoleEntity r : roleDB.getRoles()) {
                    roles.add((Role) ObjectConverter.jpaToApi(r));
                }
                return roles;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Role> getRolesForGroup(UserGroup group)
            throws SecurityDataException {
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            // FIXME: This executes two queries instead of one
            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class,
                    group.getId());
            if (null != groupDB) {
                final Collection<Role> roles = new ArrayList<Role>();
                for (RoleEntity r : groupDB.getRoles()) {
                    roles.add((Role) ObjectConverter.jpaToApi(r));
                }
                return roles;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Role> getTransitiveRolesForGroup(UserGroup group)
            throws SecurityDataException {
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class,
                    group.getId());
            if (null != groupDB) {
                Set<RoleEntity> rolesDB = new HashSet<>();
                Set<RoleEntity> transRolesDB = new HashSet<>();
                Set<RoleEntity> directRolesDB = new HashSet<>();
                Set<Long> groupsTraversed = new HashSet<>();
                Set<Long> rolesTraversed = new HashSet<>();
                Set<Long> orgsTraversed = new HashSet<>();

                // direct group roles
                directRolesDB.addAll(groupDB.getRoles());

                // Get all Transitive Roles
                transRolesDB.addAll(getRolesFromUserGroup(groupDB, groupsTraversed));

//                for (OrganizationEntity o : groupDB.getOrganizations()) {
//                    transRolesDB.addAll(getRolesFromOrganization(o, orgsTraversed));
//                }

                rolesDB.addAll(directRolesDB);
                rolesDB.addAll(transRolesDB);
                for (RoleEntity r : rolesDB)
                    transRolesDB.addAll(getRolesFromRole(r, rolesTraversed));

                final Collection<Role> roles = new ArrayList<Role>();

                for (RoleEntity r : directRolesDB) {
                    Role role = (Role) ObjectConverter.jpaToApi(r);
                    role.setRoleType(RolePermissionType.DIRECT);
                    roles.add(role);
                    // remove duplicates from transitive if any
                    transRolesDB.remove(r);
                }
                for (RoleEntity r : transRolesDB) {
                    Role role = (Role) ObjectConverter.jpaToApi(r);
                    role.setRoleType(RolePermissionType.TRANSITIVE);
                    roles.add(role);
                }

                return roles;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    // TODO: Optimize Method
    @Override
    public Map<Class<?>, Collection<?>> getTransitiveRolesAndPermissionsForGroups(
            List<UserGroup> sMGroups) throws SecurityDataException {
        Map<Class<?>, Collection<?>> map = new HashMap<Class<?>, Collection<?>>();

        HashSet<Permission> permissions = new HashSet<>();
        HashSet<Role> roles = new HashSet<>();

        map.put(Permission.class, permissions);
        map.put(Role.class, roles);

        Set<Long> rolesTraversed = new HashSet<>();

        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            for (UserGroup group : sMGroups) {
                UserGroupEntity groupDB = entityManager.find(
                        UserGroupEntity.class, group.getId());

                // Get all Transitive Roles
                Set<RoleEntity> rolesDB = new HashSet<>();
                rolesDB.addAll(groupDB.getRoles());
                for (RoleEntity r : rolesDB) {
                    rolesDB.addAll(getRolesFromRole(r, rolesTraversed));
                }

                for (RoleEntity r : rolesDB) {
                    roles.add((Role) ObjectConverter.jpaToApi(r));
                    for (PermissionEntity p : r.getPermissions()) {
                        permissions.add((Permission) ObjectConverter
                                .jpaToApi(p));
                    }
                }
            }
        } finally {
            entityManager.close();
        }

        return map;
    }

    @Override
    public PaginatedList<Role> getRolesForTenant(Long id, long startRecord,
                                                 long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Role> roles = new ArrayList<Role>();
            int total = entityManager
                    .createNamedQuery(RoleEntity.FIND_ROLE_BY_TENANT_ID,
                            RoleEntity.class).setParameter("id", id)
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<RoleEntity> rolesDB = entityManager
                    .createNamedQuery(RoleEntity.FIND_ROLE_BY_TENANT_ID,
                            RoleEntity.class).setParameter("id", id)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (RoleEntity r : rolesDB) {
                roles.add((Role) ObjectConverter.jpaToApi(r));
            }
            return new PaginatedList<Role>(roles, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Role> getRolesForOrganization(Long organizationId,
                                                       long startRecord, long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Role> roles = new ArrayList<Role>();
            int total = entityManager
                    .createNamedQuery(RoleEntity.FIND_ROLE_BY_ORGANIZATION_ID,
                            RoleEntity.class)
                    .setParameter("id", organizationId).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList().size();

            List<RoleEntity> rListDB = entityManager
                    .createNamedQuery(RoleEntity.FIND_ROLE_BY_ORGANIZATION_ID,
                            RoleEntity.class)
                    .setParameter("id", organizationId)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (RoleEntity o : rListDB) {
                roles.add((Role) ObjectConverter.jpaToApi(o));
            }
            return new PaginatedList<Role>(roles, total);

        } finally {
            entityManager.close();
        }
    }
    
    @Override
    public Role importRoles(Long tenantId, Role role) throws ItemAlreadyExistsException,
            SecurityDataException {
        if (role.getId() != null) {
		    throw new ItemAlreadyExistsException("Role : "+role.getCanonicalName(), "tenant : ");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            //check for duplicate orgs in same tenant
            try { 
                RoleEntity rolezDB = entityManager.createNamedQuery(RoleEntity.FIND_ROLE_BY_NAME_TENANT_ID,
                        RoleEntity.class)
                        .setParameter("roleName", role.getCanonicalName().toLowerCase())
                        .setParameter("tenantId", tenantId)
                        .getSingleResult();
                if (null != rolezDB) {
                	String tenantName = String.valueOf(tenantId);
                	if (rolezDB.getTenant() != null){
                		tenantName = rolezDB.getTenant().getCanonicalName();
                	}
        		    throw new ItemAlreadyExistsException("Role : "+role.getCanonicalName(), "tenant : "+tenantName);
                }
            } catch (NoResultException e) {
                // do nothing
            }
            
            RoleEntity roleDB = (RoleEntity) ObjectConverter.apiToJpa(role);
            // TODO : Checking the permission exit in the database ....
            List<PermissionEntity> permEntityLst = roleDB.getPermissions();
            for (PermissionEntity permEntity : permEntityLst){
            	new PermissionDataStore().importPermissions(permEntity);
            }
            roleDB.setCreationDate(new Date());
            TenantEntity tenant = new TenantEntity();
            tenant.setId(tenantId);
            roleDB.setTenant(tenant);

            entityManager.persist(roleDB);

            role.setId(roleDB.getId());
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
        return role;
    }

    
	@Override
	public RolesValidator ValidateRolesImport(Long tenantId, Role role) {
		RolesValidator myValidator = (new TenantValidationHelper()).new RolesValidator();
		 myValidator.setName(role.getCanonicalName());
		 if (role.getId() != null) {
			 myValidator.setDescription(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
			 return myValidator;
	        }
	        EntityManager entityManager = DatabaseDataStore.createEntityManager();
	        EntityTransaction tx = entityManager.getTransaction();
	        try {
	            tx.begin();

	            //check for duplicate orgs in same tenant
	            try { 
	                RoleEntity rolezDB = entityManager.createNamedQuery(RoleEntity.FIND_ROLE_BY_NAME_TENANT_ID,
	                        RoleEntity.class)
	                        .setParameter("roleName", role.getCanonicalName().toLowerCase())
	                        .setParameter("tenantId", tenantId)
	                        .getSingleResult();
	                if (null != rolezDB) {
	       			 	myValidator.setDescription(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
	       			 	return myValidator;
	                }
	            } catch (NoResultException e) {
       			 	myValidator.setDescription(ImportValidatorErrorCodes._VALID_ENTITY);
	            }
	        } catch (Exception e) {
	        	tx.rollback();
	        	myValidator.setDescription(ImportValidatorErrorCodes._INVALID_ENTITY);
	        } finally {
	            if (tx.isActive()) {
	                tx.commit();
	            }
	            entityManager.close();
	        }
			return myValidator;
	}
}
