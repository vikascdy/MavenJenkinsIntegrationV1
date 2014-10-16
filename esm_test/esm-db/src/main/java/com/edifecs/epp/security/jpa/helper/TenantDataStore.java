package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.PasswordPolicy;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.datastore.ITenantDataStore;
import com.edifecs.epp.security.jpa.entity.SiteEntity;
import com.edifecs.epp.security.jpa.entity.TenantEntity;
import com.edifecs.epp.security.jpa.entity.UserEntity;
import com.edifecs.epp.security.jpa.util.ImportValidatorErrorCodes;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import java.util.*;

class TenantDataStore implements ITenantDataStore {


    @Override
    public Collection<Tenant> getAll() throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Tenant> tenants = new ArrayList<Tenant>();
            @SuppressWarnings("unchecked")
            List<TenantEntity> tenantDB = entityManager.createNamedQuery(
                    TenantEntity.FIND_ALL_TENANTS).getResultList();

            for (TenantEntity t : tenantDB) {
                tenants.add((Tenant) ObjectConverter.jpaToApi(t));
            }
            return tenants;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Tenant getTenantByName(String canonicalName)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            String lowerCanonicalName = null;
            if (canonicalName != null && canonicalName.length() >= 1) {
                lowerCanonicalName = canonicalName.toLowerCase();
            }
            TenantEntity tenantDB = entityManager
                    .createNamedQuery(TenantEntity.FIND_TENANT_BY_NAME,
                            TenantEntity.class)
                    .setParameter("name", lowerCanonicalName).getSingleResult();

            if (null == tenantDB) {
                throw new ItemNotFoundException("Tenant ", canonicalName);
            }
            return (Tenant) ObjectConverter.jpaToApi(tenantDB);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Tenant getTenantByDomain(String domain)
            throws ItemNotFoundException, SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            TenantEntity tenantDB = entityManager
                    .createNamedQuery(TenantEntity.FIND_TENANT_BY_DOMAIN,
                            TenantEntity.class).setParameter("domain", domain)
                    .getSingleResult();

            if (null == tenantDB) {
                throw new ItemNotFoundException("Tenant ", domain + " domain");
            }
            return (Tenant) ObjectConverter.jpaToApi(tenantDB);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public PaginatedList<Tenant> getPaginatedRange(long startRecord,
                                                   long recordCount) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Tenant> tenants = new ArrayList<Tenant>();

            int total = entityManager
                    .createNamedQuery(TenantEntity.FIND_ALL_TENANTS,
                            TenantEntity.class).setFirstResult(0)
                    .setMaxResults(Integer.MAX_VALUE).getResultList().size();

            List<TenantEntity> tenantDB = entityManager
                    .createNamedQuery(TenantEntity.FIND_ALL_TENANTS,
                            TenantEntity.class)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (TenantEntity t : tenantDB) {
                tenants.add((Tenant) ObjectConverter.jpaToApi(t));
            }
            return new PaginatedList<Tenant>(tenants, total);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Tenant getById(long id) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, id);
            if (null != tenant) {
                return (Tenant) ObjectConverter.jpaToApi(tenant);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Tenant create(Long siteId, Tenant tenant, User auditor)
            throws ItemAlreadyExistsException, SecurityDataException {
        if (tenant.getId() != null) {
            throw new ItemAlreadyExistsException("Tenant", tenant.getCanonicalName());
        }

        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            try {
                TenantEntity tenantDB = entityManager.createNamedQuery(TenantEntity.FIND_TENANT_BY_NAME,
                        TenantEntity.class)
                        .setParameter("name", tenant.getCanonicalName().toLowerCase())
                        .getSingleResult();
                if (null != tenantDB) {
                    throw new ItemAlreadyExistsException("Tenant", tenant.getCanonicalName());
                }
            } catch (NoResultException e) {
                // do nothing
            }

            TenantEntity tenantDB = (TenantEntity) ObjectConverter
                    .apiToJpa(tenant);

            SiteEntity site = new SiteEntity();
            site.setId(siteId);
            tenantDB.setSite(site);
            if (auditor != null) {
                tenantDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
            }
            tenantDB.setCreationDate(new Date());
            entityManager.persist(tenantDB);

            return (Tenant) ObjectConverter.jpaToApi(tenantDB);
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
    public TenantValidationHelper validateTenantImport(Tenant tenant)
            throws ItemAlreadyExistsException, SecurityDataException {
    	TenantValidationHelper tValidator = new TenantValidationHelper();
    	tValidator.setName(tenant.getCanonicalName());
    	
        if (tenant.getId() != null) {
        	tValidator.setDescription(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
        	return tValidator;
        }

        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            try {
                TenantEntity tenantDB = entityManager.createNamedQuery(TenantEntity.FIND_TENANT_BY_NAME,
                        TenantEntity.class)
                        .setParameter("name", tenant.getCanonicalName().toLowerCase())
                        .getSingleResult();
                if (null != tenantDB) {
                	tValidator.setDescription(ImportValidatorErrorCodes._ENTITY_ALREADY_EXIST);
                	return tValidator;
                }
            } catch (NoResultException e) {
            	tValidator.setDescription(ImportValidatorErrorCodes._VALID_ENTITY);
            	return tValidator;
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
        tValidator.setDescription(ImportValidatorErrorCodes._VALID_ENTITY);
    	return tValidator;
    }

    @Override
    public Tenant update(Long siteId, Tenant updated, User auditor) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TenantEntity tenantDB = entityManager.find(TenantEntity.class,
                    updated.getId());
            if (null != tenantDB) {
                ObjectConverter.copyCommonBeanProperties(updated, tenantDB);
                tenantDB.setLastUpdatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                tenantDB.setLastUpdatedDate(new Date());
                return (Tenant) ObjectConverter.jpaToApi(tenantDB);
            } else {
                throw new ItemNotFoundException("Tenant ", updated.getId());
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
    public Tenant updateTenantPasswordPolicy(Long tenantId,
                                             PasswordPolicy passwordPolicy) throws SecurityDataException,
            ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TenantEntity tenantDB = entityManager.find(TenantEntity.class,
                    tenantId);
            if (null != tenantDB) {
                ObjectConverter.copyCommonBeanProperties(passwordPolicy,
                        tenantDB.getPasswordPolicy());
                return (Tenant) ObjectConverter.jpaToApi(tenantDB);
            } else {
                List<String> listProps = new ArrayList<>();
                listProps.add(0, "Tenant ");
                listProps.add(1, String.valueOf(tenantId));
                throw new ItemNotFoundException("Tenant ", tenantId);
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
    public void delete(Tenant tenant) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.remove(entityManager.find(TenantEntity.class,
                    tenant.getId()));
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
    public Collection<Tenant> getRange(long startRecord, long recordCount)
            throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Tenant> tenants = new ArrayList<Tenant>();
            @SuppressWarnings("unchecked")
            List<TenantEntity> tenantDB = entityManager
                    .createNamedQuery(TenantEntity.FIND_ALL_TENANTS)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (TenantEntity t : tenantDB) {
                tenants.add((Tenant) ObjectConverter.jpaToApi(t));
            }
            return tenants;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Tenant getTenantByUserId(Long userId) throws ItemNotFoundException,
            SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            TenantEntity tenant = entityManager
                    .createNamedQuery(TenantEntity.FIND_TENANT_BY_USER_ID,
                            TenantEntity.class).setParameter("userId", userId)
                    .getSingleResult();
            if (null != tenant) {
                return (Tenant) ObjectConverter.jpaToApi(tenant);
            } else {
                return null;
            }
        } catch (NoResultException e) {
            throw new ItemNotFoundException("User ", "tenant.");

        } finally {
            entityManager.close();
        }
    }

    @Override
    public Boolean updateLogo(Long tenantId, String imgData) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TenantEntity tenantDB = entityManager.find(TenantEntity.class,
                    tenantId);
            if (null != tenantDB) {
                tenantDB.setLogo(imgData);
                entityManager.flush();
                return true;
            } else {
                throw new ItemNotFoundException("tenant", tenantId);
            }
//        } catch (IOException e) {
//            throw new SecurityDataException(e);
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
    public String getLogo(String tenant) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            String lowerCanonicalName = null;
            if (tenant != null && tenant.length() >= 1) {
                lowerCanonicalName = tenant.toLowerCase();
            }
            TenantEntity tenantDB = entityManager
                    .createNamedQuery(TenantEntity.FIND_TENANT_BY_NAME,
                            TenantEntity.class)
                    .setParameter("name", lowerCanonicalName).getSingleResult();
            String logo = tenantDB.getLogo();
            return logo;
        } catch (Exception e) {
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Boolean updateLandingPage(Long tenantId, String landingPage) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TenantEntity tenantDB = entityManager.find(TenantEntity.class,
                    tenantId);
            if (null != tenantDB) {
                tenantDB.setLandingPage(landingPage);
                entityManager.flush();
                return true;
            } else {
                throw new ItemNotFoundException("tenant ", tenantId);
            }
//        } catch (IOException e) {
//            throw new SecurityDataException(e);
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
    public String getLandingPage(Long tenantId) throws SecurityDataException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            TenantEntity tenantDB = entityManager.find(TenantEntity.class,
                    tenantId);
            if (null != tenantDB && tenantDB.getLandingPage() != null) {
                return tenantDB.getLandingPage();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            entityManager.close();
        }
    }

	@Override
	public PaginatedList<Tenant> getPaginatedRangeForSite(long siteId,
			long startRecord, long recordCount) throws SecurityDataException {
		 EntityManager entityManager = DatabaseDataStore.createEntityManager();
	        try {
	            List<Tenant> tenants = new ArrayList<Tenant>();

	            int total = entityManager
	                    .createNamedQuery(TenantEntity.FIND_ALL_TENANTS_BY_SITE_ID,
	                            TenantEntity.class).setParameter("siteId", siteId).
	                            setFirstResult(0)
	                    .setMaxResults(Integer.MAX_VALUE).getResultList().size();

	            List<TenantEntity> tenantDB = entityManager
	                    .createNamedQuery(TenantEntity.FIND_ALL_TENANTS_BY_SITE_ID,
	                            TenantEntity.class).setParameter("siteId", siteId)
	                    .setFirstResult((int) startRecord)
	                    .setMaxResults((int) recordCount).getResultList();

	            for (TenantEntity t : tenantDB) {
	                tenants.add((Tenant) ObjectConverter.jpaToApi(t));
	            }
	            return new PaginatedList<Tenant>(tenants, total);
	        } finally {
	            entityManager.close();
	        }
	}
}
