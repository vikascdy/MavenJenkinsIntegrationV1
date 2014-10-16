package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemCannotBeNullException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.data.UserGroup;
import com.edifecs.epp.security.datastore.IUserGroupDataStore;
import com.edifecs.epp.security.jpa.entity.OrganizationEntity;
import com.edifecs.epp.security.jpa.entity.TenantEntity;
import com.edifecs.epp.security.jpa.entity.UserEntity;
import com.edifecs.epp.security.jpa.entity.UserGroupEntity;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.GroupsValidator;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

class UserGroupDataStore implements IUserGroupDataStore {

    @Override
    public Collection<UserGroup> getAll() throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<UserGroup> groups = new ArrayList<UserGroup>();
            @SuppressWarnings("unchecked")
            List<UserGroupEntity> groupsDB = entityManager.createNamedQuery(
                    UserGroupEntity.FIND_ALL_GROUPS).getResultList();

            for (UserGroupEntity g : groupsDB) {
                groups.add((UserGroup) ObjectConverter.jpaToApi(g));
            }
            return groups;
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PaginatedList<UserGroup> getPaginatedRange(long startRecord,
                                                      long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<UserGroup> groups = new ArrayList<UserGroup>();
            int total = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_ALL_GROUPS)
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<UserGroupEntity> groupsDB = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_ALL_GROUPS)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (UserGroupEntity g : groupsDB) {
                groups.add((UserGroup) ObjectConverter.jpaToApi(g));
            }
            return new PaginatedList<UserGroup>(groups, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public UserGroup getById(long id) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class,
                    id);

            if (null != groupDB) {
                return (UserGroup) ObjectConverter.jpaToApi(groupDB);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<UserGroup> queryGroups(String seed, long startRecord,
                                       long maxRecords) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserGroupEntity> criteriaQuery = builder
                    .createQuery(UserGroupEntity.class);
            Root<UserGroupEntity> root = criteriaQuery
                    .from(UserGroupEntity.class);
            criteriaQuery.where(builder.like(
                    builder.lower(root.get("canonicalName").as(String.class)),
                    "%" + seed.toLowerCase() + "%"));
            criteriaQuery.distinct(true);

            List<UserGroup> groups = new ArrayList<>();

            List<UserGroupEntity> grpDBList = entityManager
                    .createQuery(criteriaQuery)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) maxRecords).getResultList();

            for (UserGroupEntity grpDB : grpDBList) {
                groups.add((UserGroup) ObjectConverter.jpaToApi(grpDB));
            }

            return groups;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public UserGroup create(Long tenantId, UserGroup group, User auditor) throws ItemAlreadyExistsException,
            SecurityDataException {
        if (group.getId() != null) {
            throw new ItemAlreadyExistsException("Group", group.getCanonicalName());
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TenantEntity tenant = entityManager.find(TenantEntity.class, tenantId);
            for (UserGroupEntity g : tenant.getGroups()) {
                if (g.getCanonicalName().equalsIgnoreCase(group.getCanonicalName())) {
                	throw new ItemAlreadyExistsException("Group", group.getCanonicalName());
                }
            }

            UserGroupEntity groupDB = (UserGroupEntity) ObjectConverter.apiToJpa(group);
            groupDB.setTenant(tenant);
            groupDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
            groupDB.setCreationDate(new Date());
            entityManager.persist(groupDB);

            group.setId(groupDB.getId());
            return group;
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
    public UserGroup update(Long tenantId, UserGroup group, User auditor) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TenantEntity tenant = entityManager.find(TenantEntity.class, tenantId);
            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class, group.getId());
            if(!groupDB.getCanonicalName().equalsIgnoreCase(group.getCanonicalName())) {
                for (UserGroupEntity g : tenant.getGroups()) {
                    if (g.getCanonicalName().equalsIgnoreCase(group.getCanonicalName())) {
                        throw new ItemAlreadyExistsException("Group", group.getCanonicalName());
                    }
                }
            }
            if (null != groupDB) {
                ObjectConverter.copyCommonBeanProperties(group, groupDB);
                groupDB.setLastUpdatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                groupDB.setLastUpdatedDate(new Date());
                return group;
            } else {
                throw new ItemAlreadyExistsException("Group", group.getCanonicalName());
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
    public void delete(UserGroup group) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.remove(entityManager.find(UserGroupEntity.class,
                    group.getId()));
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
    public PaginatedList<UserGroup> getTransitiveUserGroupsForUser(Long userId,
                                                                   long startRecord, long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            if (userId == null) {
                throw new ItemCannotBeNullException("User");
            }
            int total = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_GROUPS_BY_USER,
                            UserGroupEntity.class)
                    .setParameter("userId", userId).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList().size();

            List<UserGroupEntity> groupsDB = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_GROUPS_BY_USER,
                            UserGroupEntity.class)
                    .setParameter("userId", userId)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            List<UserGroup> groups = new ArrayList<UserGroup>();
            for (com.edifecs.epp.security.jpa.entity.UserGroupEntity g : groupsDB) {
                groups.add((UserGroup) ObjectConverter.jpaToApi(g));
            }
            return new PaginatedList<UserGroup>(groups, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<UserGroup> getTransitiveUserGroupsForUser(Long userId) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            if (userId == null) {
                throw new ItemCannotBeNullException("User");
            }
            List<UserGroupEntity> groupsDB = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_GROUPS_BY_USER,
                            UserGroupEntity.class)
                    .setParameter("userId", userId).getResultList();

            List<UserGroup> groups = new ArrayList<UserGroup>();
            for (com.edifecs.epp.security.jpa.entity.UserGroupEntity g : groupsDB) {
                groups.add((UserGroup) ObjectConverter.jpaToApi(g));
            }
            return groups;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void addUserToUserGroup(User user, UserGroup group)
            throws SecurityDataException {
        if (user == null) {
            throw new ItemCannotBeNullException("User");
        }
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            com.edifecs.epp.security.jpa.entity.UserEntity userDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.UserEntity.class,
                            user.getId());

            com.edifecs.epp.security.jpa.entity.UserGroupEntity groupDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.UserGroupEntity.class,
                            group.getId());

            if (null != userDB && null != groupDB) {
                if (!groupDB.getUsers().contains(userDB)) {
                    groupDB.getUsers().add(userDB);
                    userDB.getGroups().add(groupDB);
                }
            }
            userDB.setModifiedDateTime(new Date());
            return;
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
    public void addChildGroupToUserGroup(UserGroup group, UserGroup parentGroup)
            throws SecurityDataException {
        if (parentGroup == null) {
            throw new ItemCannotBeNullException("Parent Group");
        }
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            com.edifecs.epp.security.jpa.entity.UserGroupEntity groupDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.UserGroupEntity.class,
                            group.getId());

            com.edifecs.epp.security.jpa.entity.UserGroupEntity parentGroupDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.UserGroupEntity.class,
                            parentGroup.getId());

            if (null != groupDB && null != parentGroupDB) {
                if (!parentGroupDB.getChildGroups().contains(groupDB)) {
                    parentGroupDB.getChildGroups().add(groupDB);
                }
            }
            return;
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
    public void addOrganizationToUserGroup(Organization organization,
                                           UserGroup group) throws SecurityDataException {
        if (organization == null) {
            throw new ItemCannotBeNullException("Organization");
        }
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        try {
            OrganizationEntity organizationDB = entityManager.find(
                    OrganizationEntity.class, organization.getId());

            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class,
                    group.getId());

            if (null != organizationDB && null != groupDB) {
                if (!groupDB.getOrganizations().contains(organizationDB)) {
                    groupDB.getOrganizations().add(organizationDB);
                    organizationDB.getGroups().add(groupDB);
                }
            }
            return;
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
    public void removeUserFromUserGroup(User user, UserGroup group)
            throws SecurityDataException {
        if (user == null) {
            throw new ItemCannotBeNullException("User");
        }
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            com.edifecs.epp.security.jpa.entity.UserEntity userDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.UserEntity.class,
                            user.getId());

            com.edifecs.epp.security.jpa.entity.UserGroupEntity groupDB = entityManager
                    .find(com.edifecs.epp.security.jpa.entity.UserGroupEntity.class,
                            group.getId());

            if (null != userDB && null != groupDB) {
                if (groupDB.getUsers().contains(userDB)) {
                    groupDB.getUsers().remove(userDB);
                    userDB.getGroups().remove(groupDB);
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
    public UserGroup getUserGroupByUserGroupName(String groupName)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            UserGroupEntity groupDB = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_GROUP_BY_NAME,
                            UserGroupEntity.class)
                    .setParameter("name", groupName).getSingleResult();

            if (null != groupDB)
                return (UserGroup) ObjectConverter.jpaToApi(groupDB);
        } finally {
            entityManager.close();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<UserGroup> getRange(long startRecord, long recordCount)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<UserGroup> groups = new ArrayList<UserGroup>();
            List<UserGroupEntity> groupsDB = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_ALL_GROUPS)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (UserGroupEntity g : groupsDB) {
                groups.add((UserGroup) ObjectConverter.jpaToApi(g));
            }
            return groups;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<UserGroup> getUserGroupsForUser(User user)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            if (user == null) {
                throw new ItemCannotBeNullException("User");
            }
            UserEntity userDB = entityManager.find(UserEntity.class,
                    user.getId());
            if (null != userDB) {
                final Collection<UserGroup> groups = new ArrayList<UserGroup>();
                for (com.edifecs.epp.security.jpa.entity.UserGroupEntity g : userDB
                        .getGroups()) {
                    groups.add((UserGroup) ObjectConverter.jpaToApi(g));
                }
                return groups;
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<UserGroup> getGroupsForTenant(Long id,
                                                       long startRecord, long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<UserGroup> userGroups = new ArrayList<UserGroup>();
            int total = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_GROUPS_BY_TENANT,
                            UserGroupEntity.class).setParameter("id", id)
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<UserGroupEntity> userGroupsDB = entityManager
                    .createNamedQuery(UserGroupEntity.FIND_GROUPS_BY_TENANT,
                            UserGroupEntity.class).setParameter("id", id)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (UserGroupEntity g : userGroupsDB) {
                userGroups.add((UserGroup) ObjectConverter.jpaToApi(g));
            }
            return new PaginatedList<UserGroup>(userGroups, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void removeOrganizationFromUserGroup(Organization organization,
                                                UserGroup group) throws SecurityDataException {
        if (organization == null) {
            throw new ItemCannotBeNullException("Organization");
        }
        if (group == null) {
            throw new ItemCannotBeNullException("Group");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        try {
            OrganizationEntity organizationDB = entityManager.find(
                    OrganizationEntity.class, organization.getId());

            UserGroupEntity groupDB = entityManager.find(UserGroupEntity.class,
                    group.getId());

            if (null != organizationDB && null != groupDB) {
                if (groupDB.getOrganizations().contains(organizationDB)) {
                    groupDB.getOrganizations().remove(organizationDB);
                    organizationDB.getGroups().remove(groupDB);
                }
            }
            return;
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
    public Collection<UserGroup> getChildGroupsForUserGroup(UserGroup group)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<UserGroup> groups = new ArrayList<UserGroup>();
            @SuppressWarnings("unchecked")
            List<UserGroupEntity> gListDB = entityManager
                    .createNamedQuery(
                            UserGroupEntity.FIND_CHILD_GROUPS_BY_GROUP)
                    .setParameter("id", group.getId()).getResultList();

            for (UserGroupEntity g : gListDB) {
                groups.add((UserGroup) ObjectConverter.jpaToApi(g));
            }
            return groups;
        } finally {
            entityManager.close();
        }
    }
    
    @Override
    public UserGroup importgroups(Long tenantId, UserGroup group) throws ItemAlreadyExistsException,
            SecurityDataException {
        if (group.getId() != null) {
            throw new ItemAlreadyExistsException("Tenant : "+tenantId, "group :"+group.getCanonicalName());
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TenantEntity tenant = entityManager.find(TenantEntity.class, tenantId);
            for (UserGroupEntity g : tenant.getGroups()) {
                if (g.getCanonicalName().equalsIgnoreCase(group.getCanonicalName())) {
                	throw new ItemAlreadyExistsException(" Group : "+group.getCanonicalName(), " tenant : "+tenant.getCanonicalName());
                }
            }

            UserGroupEntity groupDB = (UserGroupEntity) ObjectConverter.apiToJpa(group);
            groupDB.setTenant(tenant);
            groupDB.setCreationDate(new Date());
            entityManager.persist(groupDB);

            group.setId(groupDB.getId());
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
        return group;
    }

    @Override
	public GroupsValidator validateImportGroups(Long tenantId, UserGroup group) {
		GroupsValidator  objValidator = (new TenantValidationHelper()).new GroupsValidator();
		objValidator.setName(group.getCanonicalName());
		 if (group.getId() != null) {
			 	objValidator.setErrorCodes(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
			 	return objValidator;
	        }
	        EntityManager entityManager = DatabaseDataStore.createEntityManager();
	        EntityTransaction tx = entityManager.getTransaction();
	        try {
	            tx.begin();
	            TenantEntity tenant = entityManager.find(TenantEntity.class, tenantId);
	            for (UserGroupEntity g : tenant.getGroups()) {
	                if (g.getCanonicalName().equalsIgnoreCase(group.getCanonicalName())) {
	                	objValidator.setErrorCodes(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
	    			 	return objValidator;
	                }
	            }
	        } catch (Exception e) {
	            tx.rollback();
	            objValidator.setErrorCodes(ImportValidatorErrorCodes._INVALID_ENTITY);
			 	return objValidator;
	        } finally {
	            if (tx.isActive()) {
	                tx.commit();
	            }
	            entityManager.close();
	        }
	        objValidator.setErrorCodes(ImportValidatorErrorCodes._VALID_ENTITY);
		 	return objValidator;
	}
}
