package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.datastore.IOrganizationDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemCannotBeNullException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.entity.*;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.OrgValidationHelper;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.*;



class OrganizationDataStore implements IOrganizationDataStore {

    @Override
    public Collection<Organization> getAll() throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Organization> organizations = new ArrayList<Organization>();
            @SuppressWarnings("unchecked")
            List<OrganizationEntity> oListDB = entityManager.createNamedQuery(
                    OrganizationEntity.FIND_ALL_ORGANIZATIONS).getResultList();

            for (OrganizationEntity o : oListDB) {
                organizations.add((Organization) ObjectConverter.jpaToApi(o));
            }
            return organizations;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Organization> getPaginatedRange(long startRecord,
                                                         long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Organization> organizations = new ArrayList<Organization>();
            int total = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ALL_ORGANIZATIONS,
                            OrganizationEntity.class).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList().size();

            List<OrganizationEntity> oListDB = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ALL_ORGANIZATIONS,
                            OrganizationEntity.class)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (OrganizationEntity o : oListDB) {
                organizations.add((Organization) ObjectConverter.jpaToApi(o));
            }
            return new PaginatedList<Organization>(organizations, total);

        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Organization> getRange(long startRecord, long recordCount)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Organization> organizations = new ArrayList<Organization>();
            @SuppressWarnings("unchecked")
            List<OrganizationEntity> oListDB = entityManager
                    .createNamedQuery(OrganizationEntity.FIND_ALL_ORGANIZATIONS)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (OrganizationEntity o : oListDB) {
                organizations.add((Organization) ObjectConverter.jpaToApi(o));
            }
            return organizations;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Organization getById(long id) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            OrganizationEntity organization = entityManager.find(
                    OrganizationEntity.class, id);

            if (null != organization) {
                return (Organization) ObjectConverter.jpaToApi(organization);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override 
    public Organization create(Long tenantId, Organization organization, User auditor)
            throws ItemAlreadyExistsException, SecurityDataException {
        if (organization.getId() != null) {
             throw new ItemAlreadyExistsException("Organization "+organization.getCanonicalName(), "tenant ");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            //check for duplicate orgs in same tenant
            try {
                OrganizationEntity orgDB = entityManager.createNamedQuery(OrganizationEntity.FIND_ORGANIZATION_BY_NAME_TENANT_ID,
                        OrganizationEntity.class)
                        .setParameter("organizationName", organization.getCanonicalName().toLowerCase())
                        .setParameter("tenantId", tenantId)
                        .getSingleResult();
                if (null != orgDB) {
                     throw new ItemAlreadyExistsException("Organization", organization.getCanonicalName());
                }
            } catch (NoResultException e) {
                // do nothing
            }
            
            OrganizationEntity organizationDB = (OrganizationEntity) ObjectConverter.apiToJpa(organization);
            TenantEntity tenant = new TenantEntity();
            tenant.setId(tenantId);
            organizationDB.setTenant(tenant);
            if (auditor != null) {
                organizationDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
            }
            organizationDB.setCreationDate(new Date());
            entityManager.persist(organizationDB);

            organization.setId(organizationDB.getId());
            return organization;
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
    public Organization update(Long tenantId, Organization organization, User auditor)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            OrganizationEntity organizationDB = entityManager.find(
                    OrganizationEntity.class, organization.getId());

            //check for duplicate orgs in same tenant
            if(!organizationDB.getCanonicalName().equalsIgnoreCase(organization.getCanonicalName())) {
                try {
                    OrganizationEntity orgDB = entityManager.createNamedQuery(OrganizationEntity.FIND_ORGANIZATION_BY_NAME_TENANT_ID,
                            OrganizationEntity.class)
                            .setParameter("organizationName", organization.getCanonicalName().toLowerCase())
                            .setParameter("tenantId", tenantId)
                            .getSingleResult();
                    throw new ItemAlreadyExistsException("Organization", organization.getCanonicalName());
                } catch (NoResultException e) {
                    // do nothing
                }
            }
            if (null != organizationDB) {
                organizationDB
                        .setCanonicalName(organization.getCanonicalName());
                organizationDB.setDescription(organization.getDescription());
                organizationDB.setLastUpdatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                organizationDB.setLastUpdatedDate(new Date());
                return organization;
            } else {
                 throw new ItemNotFoundException("Organization  ", organization.getId());
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
    public void delete(Organization organization) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            OrganizationEntity objEntity = entityManager.find(OrganizationEntity.class,
                    organization.getId());
            if (objEntity.getParentOrganization() != null){
            	OrganizationEntity parentEntity = objEntity.getParentOrganization();
            	parentEntity.getChildOrganizations().remove(
            			 objEntity);
            	objEntity.setParentOrganization(null);
            } else {
            	entityManager.remove(objEntity);
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
    public SecurityRealm addRealmToOrganization(Long organizationId,
                                                SecurityRealm realm) {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            OrganizationEntity organizationDB = entityManager.find(OrganizationEntity.class, organizationId);

            SecurityRealmEntity realmEntity = (SecurityRealmEntity) ObjectConverter.apiToJpa(realm);
            entityManager.persist(realmEntity);
            realmEntity.setOrganization(organizationDB);
            organizationDB.getSecurityRealms().add(realmEntity);

            return (SecurityRealm) ObjectConverter.jpaToApi(realmEntity);
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
    public Organization getOrganizationByDomainAndOrganizationName(String domain, String orgName)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            OrganizationEntity organization = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ORGANIZATION_IN_DOMAIN_BY_NAME,
                            OrganizationEntity.class)
                    .setParameter("domain", domain)
                    .setParameter("organizationName", orgName)
                    .getSingleResult();

            if (null != organization) {
                return (Organization) ObjectConverter.jpaToApi(organization);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Organization getOrganizationByName(String name)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            OrganizationEntity organization = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ORGANIZATION_BY_NAME,
                            OrganizationEntity.class)
                    .setParameter("name", name).getSingleResult();

            if (null != organization) {
                return (Organization) ObjectConverter.jpaToApi(organization);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Organization getUserOrganizationByUserId(Long userId)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            UserEntity user = entityManager.find(UserEntity.class, userId);
            OrganizationEntity organization = user.getOrganization();

            if (null != organization) {
                return (Organization) ObjectConverter.jpaToApi(organization);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public SecurityRealm updateRealm(SecurityRealm realm)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            SecurityRealmEntity realmEntity = entityManager.find(
                    SecurityRealmEntity.class, realm.getId());

            // TODO : should call update org
            if (null != realmEntity) {
                long orgId = realmEntity.getOrganization().getId();
                ObjectConverter.copyCommonBeanProperties(realm, realmEntity);
                realmEntity.setOrganization(entityManager.find(
                        OrganizationEntity.class, orgId));
                entityManager.merge(realmEntity);
            } else {
                 throw new ItemNotFoundException("Realm ", realm.getId());
            }
            return (SecurityRealm) ObjectConverter.jpaToApi(realmEntity);
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
    public Tenant getTenantByOrganizationId(Long organizationId)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            TenantEntity tenant = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_TENANT_BY_ORGANIZATION_ID,
                            TenantEntity.class)
                    .setParameter("organizationId", organizationId)
                    .getSingleResult();
            if (null != tenant) {
                return (Tenant) ObjectConverter.jpaToApi(tenant);
            } else {
                return null;
            }
        } catch (NoResultException e) {
             throw new ItemNotFoundException("Organization ", organizationId);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<Organization> getChildOrganizationsById(
            Long organizationId) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Organization> organizations = new ArrayList<Organization>();
            @SuppressWarnings("unchecked")
            List<OrganizationEntity> oListDB = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_CHILD_ORGANIZATIONS_BY_ORGANIZATION_ID)
                    .setParameter("organizationId", organizationId)
                    .getResultList();

            for (OrganizationEntity o : oListDB) {
                organizations.add((Organization) ObjectConverter.jpaToApi(o));
            }
            return organizations;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void addChildOrganization(Long organizationId, Organization childOrganization) throws SecurityDataException {
        if (childOrganization == null) {
             throw new ItemCannotBeNullException("Organization");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            OrganizationEntity organizationDB = entityManager.find(OrganizationEntity.class, organizationId);
            OrganizationEntity childOrganizationDB = entityManager.find(OrganizationEntity.class, childOrganization.getId());

            if (null != childOrganizationDB.getParentOrganization()) {
                // TODO: Fix this
             	String type = "add child organization  "+childOrganizationDB.getCanonicalName()+" to "+organizationDB.getCanonicalName()+" already ";
             	String id = childOrganizationDB.getParentOrganization().getCanonicalName() +" Organization.";
                throw new ItemAlreadyExistsException(type, id);
            }
            if (null != organizationDB && null != childOrganizationDB) {
                if (!organizationDB.getChildOrganizations().contains(
                        childOrganizationDB)) {
                    childOrganizationDB.setTenant(organizationDB.getTenant());
                    organizationDB.getChildOrganizations().add(
                            childOrganizationDB);
                    childOrganizationDB.setParentOrganization(organizationDB);
                }
            }
            return;
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
    public void removeChildOrganization(Long organizationId, Organization childOrganization) throws SecurityDataException {
        if (childOrganization == null) {
             throw new ItemCannotBeNullException("Child Organization");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            OrganizationEntity organizationDB = entityManager.find(
                    OrganizationEntity.class, organizationId);
            OrganizationEntity childOrganizationDB = entityManager.find(
                    OrganizationEntity.class, childOrganization.getId());

            if (null != organizationDB && null != childOrganizationDB) {
                if (organizationDB.getChildOrganizations().contains(childOrganizationDB)) {
                    organizationDB.getChildOrganizations().remove(childOrganizationDB);
                    childOrganizationDB.setParentOrganization(null);
                } else {
                    // TODO: Fix this
                 	String type = "set child  Organization "+childOrganizationDB.getCanonicalName() +" does not ";
                 	String id = "Organization "+organizationDB.getCanonicalName();
                    throw new ItemAlreadyExistsException(type, id);
                }
            }

            entityManager.flush();
            return;
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
    public PaginatedList<Organization> getOrganizationsForTenant(Long id,
                                                                 long startRecord, long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Organization> organizations = new ArrayList<Organization>();
            int total = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ORGANIZATIONS_BY_TENANT_ID,
                            OrganizationEntity.class).setParameter("id", id)
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<OrganizationEntity> oListDB = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ORGANIZATIONS_BY_TENANT_ID,
                            OrganizationEntity.class).setParameter("id", id)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (OrganizationEntity o : oListDB) {
                organizations.add((Organization) ObjectConverter.jpaToApi(o));
            }
            return new PaginatedList<Organization>(organizations, total);

        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Organization> getOrganizationsForGroup(Long id,
                                                                long startRecord, long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Organization> organizations = new ArrayList<Organization>();
            int total = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ORGANIZATIONS_BY_GROUP_ID,
                            OrganizationEntity.class).setParameter("id", id)
                    .setFirstResult(0).setMaxResults(Integer.MAX_VALUE)
                    .getResultList().size();

            List<OrganizationEntity> oListDB = entityManager
                    .createNamedQuery(
                            OrganizationEntity.FIND_ORGANIZATIONS_BY_GROUP_ID,
                            OrganizationEntity.class).setParameter("id", id)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (OrganizationEntity o : oListDB) {
                organizations.add((Organization) ObjectConverter.jpaToApi(o));
            }
            return new PaginatedList<Organization>(organizations, total);

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void addRoleToOrganization(Long organizationId, Long roleId)
            throws SecurityDataException {

        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();

        try {
            tx.begin();
            OrganizationEntity organizationDB = entityManager.find(
                    OrganizationEntity.class, organizationId);
            RoleEntity roleDB = entityManager.find(RoleEntity.class, roleId);

            if (null != organizationDB && null != roleDB) {
                if (!organizationDB.getRoles().contains(roleDB)) {
                    organizationDB.getRoles().add(roleDB);
                }
            }
            return;
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
    public void removeRoleFromOrganization(Long organizationId, Long roleId)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();

        try {
            tx.begin();
            OrganizationEntity organizationDB = entityManager.find(
                    OrganizationEntity.class, organizationId);
            RoleEntity roleDB = entityManager.find(RoleEntity.class, roleId);

            if (null != organizationDB && null != roleDB) {
                if (organizationDB.getRoles().contains(roleDB)) {
                    organizationDB.getRoles().remove(roleDB);
                }
            }
            return;
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

    public void addUser(Long userId, Long organizationId, EntityManager entityManager) throws SecurityDataException {
        OrganizationEntity organizationDB = entityManager.find(OrganizationEntity.class, organizationId);
        UserEntity userDB = entityManager.find(UserEntity.class, userId);

        if (null != userDB.getOrganization()) {
            // TODO: Fix this
         	String type = "add user "+userDB.getId() +" to Organization "+organizationDB.getCanonicalName()+" already ";
         	String id = "Organization "+userDB.getOrganization().getCanonicalName();
             throw new ItemAlreadyExistsException(type, id);
        }

        if (null != organizationDB && null != userDB) {
            if (!organizationDB.getUsers().contains(userDB)) {
                organizationDB.getUsers().add(userDB);
                userDB.setOrganization(organizationDB);
            }
        }
    }

    @Override
    public OrganizationDetail getTransitiveChildOrganizationsForOrganization(
            Long organizationId) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {

            OrganizationDetail orgDetail = new OrganizationDetail();
            OrganizationEntity orgDB = entityManager.find(
                    OrganizationEntity.class, organizationId);

            // copy common properties
            orgDetail.setId(orgDB.getId());
            orgDetail.setText(orgDB.getCanonicalName());

            Set<Long> orgsTraversed = new HashSet<>();
            if (!orgDB.getChildOrganizations().isEmpty()) {
                for (OrganizationEntity o : orgDB.getChildOrganizations()) {
                    orgDetail.getChildOrganizations().add(
                            getAllChildOrganizations(o, orgsTraversed));
                }
            } else {
                orgDetail.setLeaf(true);
            }
            return orgDetail;

        } finally {
            entityManager.close();
        }
    }

    private OrganizationDetail getAllChildOrganizations(OrganizationEntity org,
                                                        Set<Long> orgsTraversed) {
        orgsTraversed.add(org.getId());
        OrganizationDetail orgDetail = new OrganizationDetail();

        orgDetail.setId(org.getId());
        orgDetail.setText(org.getCanonicalName());
        if (!org.getChildOrganizations().isEmpty()) {
            for (OrganizationEntity childOrg : org.getChildOrganizations()) {
                if (!orgsTraversed.contains(childOrg.getId())) {
                    orgDetail.getChildOrganizations().add(
                            getAllChildOrganizations(childOrg, orgsTraversed));
                }
            }
        } else {
            orgDetail.setLeaf(true);
        }
        return orgDetail;
    }
    
    @Override 
    public Organization ImportOrganization(Long tenantId, OrganizationEntity organizationDB, 
    		Map<String, String> myUserCredential, User auditor)
            throws ItemAlreadyExistsException, SecurityDataException { 
    	Organization organization = null;
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        
        organization = (Organization) ObjectConverter.jpaToApi(organizationDB);
        try {
            tx.begin();

            TenantEntity tenant = new TenantEntity();
            tenant.setId(tenantId);
            organizationDB.setTenant(tenant);
            organizationDB.setCreationDate(new Date());
            
            List<UserEntity> objUserEntity = organizationDB.getUsers();
            List<UserEntity> objDBUserEntity = organizationDB.getUsers();
            for (UserEntity objEntity : objUserEntity){
            	List<RoleEntity> roleEntity = objEntity.getRoles();
            	List<UserGroupEntity> grpEntity = objEntity.getGroups();
            	User user = (User) ObjectConverter.jpaToApi(objEntity);
            	try{
            		String userPasswd = myUserCredential.get(user.getUsername());
            		user = new UserDataStore().importUsers(organization.getId(), user, roleEntity, grpEntity, organization, userPasswd, auditor);
            		UserEntity userDB = (UserEntity) ObjectConverter.apiToJpa(user);
            		objDBUserEntity.add(userDB);
            	} catch (ItemAlreadyExistsException | SecurityDataException e){
       			// leave this catch block is safe ...
            	}
            }
            organizationDB.setUsers(objDBUserEntity);
            organization = (Organization) ObjectConverter.jpaToApi(organizationDB);
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
        return organization;
    }

    
	@Override
	public OrgValidationHelper validateOrgImports(Long tenantId, Organization organization) {
		OrgValidationHelper orgValidator = new OrgValidationHelper();
		orgValidator.setName(organization.getCanonicalName());
		
        if (organization.getId() != null) {
        	orgValidator.setDescription(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
        	return orgValidator;
       }
       EntityManager entityManager = DatabaseDataStore.createEntityManager();
       EntityTransaction tx = entityManager.getTransaction();
       try {
           tx.begin();

           //check for duplicate orgs in same tenant
           try {
               OrganizationEntity orgDB = entityManager.createNamedQuery(OrganizationEntity.FIND_ORGANIZATION_BY_NAME_TENANT_ID,
                       OrganizationEntity.class)
                       .setParameter("organizationName", organization.getCanonicalName().toLowerCase())
                       .setParameter("tenantId", tenantId)
                       .getSingleResult();
               if (null != orgDB) {
               	orgValidator.setDescription(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
            	return orgValidator;
               }
           } catch (NoResultException e) {
        	    orgValidator.setDescription(ImportValidatorErrorCodes._VALID_ENTITY);
           		return orgValidator;
           }
           
       } catch (Exception e) {
           tx.rollback();
           orgValidator.setDescription(ImportValidatorErrorCodes._INVALID_ENTITY);
      		return orgValidator;
       } finally {
           if (tx.isActive()) {
               tx.commit();
           }
           entityManager.close();
       }
       orgValidator.setDescription(ImportValidatorErrorCodes._VALID_ENTITY);
  	   return orgValidator;
	}


}
