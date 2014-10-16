package com.edifecs.epp.security.jpa.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.edifecs.core.configuration.configuration.Scope;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.datastore.IPropertyDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.entity.OrganizationEntity;
import com.edifecs.epp.security.jpa.entity.PropertyEntity;
import com.edifecs.epp.security.jpa.entity.SiteEntity;
import com.edifecs.epp.security.jpa.entity.TenantEntity;
import com.edifecs.epp.security.jpa.entity.UserEntity;
import com.edifecs.epp.security.jpa.entity.UserGroupEntity;

public class PropertyDataStore implements IPropertyDataStore {

	@Override
	public Collection<Property> getAll() throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			List<Property> properties = new ArrayList<Property>();
			@SuppressWarnings("unchecked")
			List<PropertyEntity> pListDB = entityManager.createNamedQuery(
					PropertyEntity.FIND_ALL_PROPERTIES).getResultList();

			for (PropertyEntity propertyDB : pListDB) {
				properties.add((Property) ObjectConverter.jpaToApi(propertyDB));
			}
			return properties;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public Collection<Property> getRange(long startRecord, long recordCount)
			throws SecurityDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedList<Property> getPaginatedRange(long startRecord,
			long recordCount) throws SecurityDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getById(long id) throws ItemNotFoundException,
			SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			PropertyEntity property = entityManager.find(PropertyEntity.class, id);
			if (null != property) {
				return (Property) ObjectConverter.jpaToApi(property);
			} else {
				return null;
			}
		} finally {
			entityManager.close();
		}
	}

	@Override
	public Property create(Property property, User auditor)
			throws ItemAlreadyExistsException, SecurityDataException {
		if (property.getId() != null) {
             throw new ItemAlreadyExistsException("Tenant", property.getModuleName());
		}
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			PropertyEntity propertyDB = (PropertyEntity) ObjectConverter.apiToJpa(property);
            propertyDB.setCreatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
            propertyDB.setCreationDate(new Date());
			entityManager.persist(propertyDB);
			property.setId(propertyDB.getId());
			return property;
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
	public Property update(Property updated, User auditor) throws ItemNotFoundException,
			SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			PropertyEntity propertyDB = entityManager.find(PropertyEntity.class,
					updated.getId());
			if (null != propertyDB) {
				ObjectConverter.copyCommonBeanProperties(updated, propertyDB);
                propertyDB.setLastUpdatedBy((UserEntity) ObjectConverter.apiToJpa(auditor));
                propertyDB.setLastUpdatedDate(new Date());
				entityManager.merge(propertyDB);
				return (Property) ObjectConverter.jpaToApi(propertyDB);
			} else {
	             throw new ItemAlreadyExistsException("Property ", String.valueOf(updated.getId()));
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
	public void delete(Property toDelete) throws SecurityDataException {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			PropertyEntity toDeleteDB = entityManager.find(PropertyEntity.class,
					toDelete.getId());
			entityManager.remove(toDeleteDB);
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
	public Collection<Property> getProperties(User user) throws Exception {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			List<Property> properties = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<PropertyEntity> propertiesDB = entityManager
					.createNamedQuery(PropertyEntity.FIND_PROPERTIES_OF_CURRENT_USER)
					.setParameter("currentUserId", user.getId())
					.getResultList();
			for (PropertyEntity property : propertiesDB) {
				properties.add((Property) ObjectConverter.jpaToApi(property));
			} 
			return properties;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public Collection<Property> getProperties(User user, String moduleName)
			throws Exception {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			List<Property> properties = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<PropertyEntity> propertiesDB = entityManager
					.createNamedQuery(PropertyEntity.FIND_PROPERTIES_OF_CURRENT_USER_AND_MODULE)
					.setParameter("currentUserId", user.getId())
					.setParameter("moduleName", moduleName)
					.getResultList();
			for (PropertyEntity property : propertiesDB) {
				properties.add((Property) ObjectConverter.jpaToApi(property));
			} 
			return properties;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public Collection<Property> getProperties(Long ownerId, Scope scope)
			throws Exception {
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		try {
			List<Property> properties = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<PropertyEntity> propertiesDB = entityManager
					.createNamedQuery(PropertyEntity.FIND_PROPERTIES_OF_SCOPE)
					.setParameter("ownerId", ownerId)
					.setParameter("ownerScope", scope)
					.getResultList();
			for (PropertyEntity property : propertiesDB) {
				properties.add((Property) ObjectConverter.jpaToApi(property));
			} 
			return properties;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public void setProperties(User user, Collection<Property> properties)
			throws Exception {
		if (null == user.getId()) throw new IllegalArgumentException("id should not be null");
		
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		
		UserEntity userDB = entityManager.find(UserEntity.class, user.getId());
		if (null == userDB){
             throw new ItemNotFoundException("User ", user.getId());
		}
		for (Property property : properties) {
			if (property.getOwnerScope() != null && property.getOwnerScope() != Scope.USER)
				throw new IllegalArgumentException();
			if (property.getOwnerId() != null && property.getOwnerId() != user.getId())
				throw new IllegalArgumentException();
			property.setOwnerId(user.getId());
			property.setOwnerScope(Scope.USER);
		}
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			for (Property property : properties) {
				if (null == property.getId()) {
					PropertyEntity propertyDB = (PropertyEntity) ObjectConverter.apiToJpa(property);
					entityManager.persist(propertyDB);
					property.setId(propertyDB.getId());
				} else {
					PropertyEntity propertyDB = entityManager.find(PropertyEntity.class, property.getId());
					ObjectConverter.copyCommonBeanProperties(property, propertyDB);
					entityManager.merge(propertyDB);
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
	public void setProperties(Long ownerId, Scope scope,
			Collection<Property> properties) throws Exception {
		if (null == ownerId) throw new IllegalArgumentException("id should not be null");
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		switch (scope) {
			case SITE:
				if (null == entityManager.find(SiteEntity.class, ownerId)) {
		             throw new ItemNotFoundException("Site ", ownerId);
				}
				break;
			case TENANT:
				if (null == entityManager.find(TenantEntity.class, ownerId)) {
		         	throw new ItemNotFoundException("Tenant ", ownerId);
				}
				break;
			case ORGANIZATION:
				if (null == entityManager.find(OrganizationEntity.class, ownerId)) {
		         	throw new ItemNotFoundException("Organization ", ownerId);
				}
				break;
			case GROUP:
				if (null == entityManager.find(UserGroupEntity.class, ownerId)) {
		         	throw new ItemNotFoundException("UserGroup ", ownerId);
				}
				break;
			case USER:
				if (null == entityManager.find(UserEntity.class, ownerId)) {
		            throw new ItemNotFoundException("User ", ownerId);
				}
				break;
		}
		
		for (Property property : properties) {
			if (property.getOwnerScope() != null && property.getOwnerScope() != scope)
				throw new IllegalArgumentException();
			if (property.getOwnerId() != null && property.getOwnerId() != ownerId)
				throw new IllegalArgumentException();
			property.setOwnerId(ownerId);
			property.setOwnerScope(scope);
		}
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			for (Property property : properties) {
				PropertyEntity propertyDB = entityManager.find(PropertyEntity.class,
						property.getId());
				if (null != propertyDB) {
					ObjectConverter.copyCommonBeanProperties(property, propertyDB);
					entityManager.merge(propertyDB);
				} else {
					propertyDB = (PropertyEntity) ObjectConverter.apiToJpa(property);
					entityManager.persist(propertyDB);
					property.setId(propertyDB.getId());
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
	public void setProperties(User user, String moduleName,
			Collection<Property> properties) throws Exception {
		if (null == user.getId()) throw new IllegalArgumentException("id should not be null");
		
		EntityManager entityManager = DatabaseDataStore.createEntityManager();
		
		UserEntity userDB = entityManager.find(UserEntity.class, user.getId());
		if (null == userDB) {
            throw new ItemNotFoundException("User ", user.getId());
		}
		for (Property property : properties) {
			if (property.getOwnerScope() != null && property.getOwnerScope() != Scope.USER)
				throw new IllegalArgumentException();
			if (property.getOwnerId() != null && property.getOwnerId() != user.getId())
				throw new IllegalArgumentException();
			property.setOwnerId(user.getId());
			property.setOwnerScope(Scope.USER);
			property.setModuleName(moduleName);
		}
		EntityTransaction tx = entityManager.getTransaction();
		try {
			tx.begin();
			for (Property property : properties) {
				PropertyEntity propertyDB = entityManager.find(PropertyEntity.class,
						property.getId());
				if (null != propertyDB) {
					ObjectConverter.copyCommonBeanProperties(property, propertyDB);
					entityManager.merge(propertyDB);
				} else {
					propertyDB = (PropertyEntity) ObjectConverter.apiToJpa(property);
					entityManager.persist(propertyDB);
					property.setId(propertyDB.getId());
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

}
