package com.edifecs.epp.flexfields.jpa.helper;

import com.edifecs.epp.flexfields.datastore.IFlexFieldDefinitionDataStore;
import com.edifecs.epp.flexfields.exception.FlexFieldRegistryException;
import com.edifecs.epp.flexfields.exception.ItemAlreadyExistsException;
import com.edifecs.epp.flexfields.exception.ItemNotFoundException;
import com.edifecs.epp.flexfields.jpa.entity.FlexFieldDefinitionEntity;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sandeep.kath on 5/7/2014.
 */
public class FlexFieldDefinitionDataStore implements IFlexFieldDefinitionDataStore {

    @Override
    public FlexFieldDefinition createFlexField(FlexFieldDefinition flexFieldDefinition) throws FlexFieldRegistryException {
        if (flexFieldDefinition.getId() != null) {
            throw new ItemAlreadyExistsException(FlexFieldDefinition.class, flexFieldDefinition.getId());
        }
        FlexFieldDefinitionEntity flexFieldDefinitionEntity = (FlexFieldDefinitionEntity) ObjectConverter.apiToJpa(flexFieldDefinition);

        if(flexFieldDefinitionExists(flexFieldDefinitionEntity)) {
            throw new ItemAlreadyExistsException(FlexFieldDefinition.class, flexFieldDefinition.getName());
        }

        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();

            entityManager.persist(flexFieldDefinitionEntity);
            return (FlexFieldDefinition) ObjectConverter.jpaToApi(flexFieldDefinitionEntity);
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
    public FlexFieldDefinition updateFlexField(FlexFieldDefinition flexFieldDefinition) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            FlexFieldDefinitionEntity flexFieldDefinitionEntity = entityManager.find(FlexFieldDefinitionEntity.class, flexFieldDefinition.getId());
            if (null != flexFieldDefinitionEntity) {
                ObjectConverter.copyCommonBeanProperties(flexFieldDefinition, flexFieldDefinitionEntity);
                return (FlexFieldDefinition) ObjectConverter.jpaToApi(flexFieldDefinitionEntity);
            } else {
                throw new ItemNotFoundException(FlexFieldDefinition.class, flexFieldDefinition.getId());
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
    public void deleteFlexField(FlexFieldDefinition flexFieldDefinition) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.remove(entityManager.find(FlexFieldDefinitionEntity.class,
                    flexFieldDefinition.getId()));
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
    public Collection<FlexFieldDefinition> getRange(long startRecord, long recordCount) throws FlexFieldRegistryException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<FlexFieldDefinition> flexFieldDefinitions = new ArrayList<FlexFieldDefinition>();
            @SuppressWarnings("unchecked")
            List<FlexFieldDefinitionEntity> flexFieldDefinitionEntities = entityManager
                    .createNamedQuery(FlexFieldDefinitionEntity.FIND_ALL_FLEXFIELD_DEFINITIONS)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount)
                    .getResultList();

            for (FlexFieldDefinitionEntity field : flexFieldDefinitionEntities) {
                flexFieldDefinitions.add((FlexFieldDefinition) ObjectConverter.jpaToApi(field));
            }
            return flexFieldDefinitions;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public FlexFieldDefinition getById(long id) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            FlexFieldDefinitionEntity flexFieldDefinitionEntity = entityManager.find(FlexFieldDefinitionEntity.class, id);
            if (null != flexFieldDefinitionEntity) {
                return (FlexFieldDefinition) ObjectConverter.jpaToApi(flexFieldDefinitionEntity);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }

    private boolean flexFieldDefinitionExists(FlexFieldDefinitionEntity flexFieldDefinitionEntity) {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        Query checkFieldExists = entityManager.createQuery("SELECT COUNT(fe.name) FROM FlexFieldDefinitionEntity fe WHERE "
                 + " lower(fe.name)=:name  "
                 + " and fe.namespace = :namespace " );

        checkFieldExists.setParameter("name", flexFieldDefinitionEntity.getName().toLowerCase());
        checkFieldExists.setParameter("namespace", flexFieldDefinitionEntity.getNamespace());

        long fieldExists = (Long) checkFieldExists.getSingleResult();
        if (fieldExists > 0) {
            return true;
        } else {
            return false;
        }
    }
}
