package com.edifecs.epp.security.jpa.helper;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.Site;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.datastore.ISiteDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.entity.SiteEntity;
import com.edifecs.epp.security.jpa.entity.UserEntity;

public class SiteDataStore implements ISiteDataStore {

	@Override
	public Collection<Site> getAll() throws SecurityDataException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Site> getRange(long startRecord, long recordCount) throws SecurityDataException {
        throw new UnsupportedOperationException();
	}

	@Override
	public PaginatedList<Site> getPaginatedRange(long startRecord,
			long recordCount) throws SecurityDataException {
        throw new UnsupportedOperationException();
	}

	@Override
	public Site getById(long id) throws ItemNotFoundException,
			SecurityDataException {
        throw new UnsupportedOperationException();
	}

	@Override
	public Site create(Site site, User auditor) throws ItemAlreadyExistsException,
			SecurityDataException {
		if (site.getId() != null) {
		    throw new ItemAlreadyExistsException("Site ", site.getCanonicalName());
		}
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			SiteEntity siteDB = (SiteEntity) ObjectConverter.apiToJpa(site);
            if (null != auditor)
                siteDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
            siteDB.setCreationDate(new Date());
			entityManager.persist(siteDB);

			site.setId(siteDB.getId());
			return site;
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
	public Site update(Site updated, User auditor) throws ItemNotFoundException,
			SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			SiteEntity siteDB = entityManager.find(SiteEntity.class,
					updated.getId());
			if (null != siteDB) {
				ObjectConverter.copyCommonBeanProperties(updated, siteDB);
                siteDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                siteDB.setCreationDate(new Date());
				return updated;
			} else {
			    throw new ItemNotFoundException("Site",updated.getId());
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
	public void delete(Site toDelete) throws SecurityDataException {
        throw new UnsupportedOperationException();
	}

	@Override
	public Site getSite() throws ItemNotFoundException, SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			SiteEntity site = entityManager.createNamedQuery(SiteEntity.FIND_ALL_SITES, SiteEntity.class)
                .setFirstResult(0)
                .setMaxResults(1).getSingleResult();
			if (null != site) {
				return (Site) ObjectConverter.jpaToApi(site);
			} else {
				return null;
			}
		} catch (NoResultException e) {
            throw new ItemNotFoundException("Site does not exist.", "");
        } finally {
			entityManager.close();
		}
	}
}
