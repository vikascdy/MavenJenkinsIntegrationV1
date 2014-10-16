package com.edifecs.epp.flexfields.jpa.helper;

import com.edifecs.epp.flexfields.datastore.IFlexFieldValueDataStore;
import com.edifecs.epp.flexfields.exception.FieldValueException;
import com.edifecs.epp.flexfields.exception.ItemAlreadyExistsException;
import com.edifecs.epp.flexfields.exception.ItemNotFoundException;
import com.edifecs.epp.flexfields.jpa.entity.FlexFieldDefinitionEntity;
import com.edifecs.epp.flexfields.jpa.entity.FlexFieldValueEntity;
import com.edifecs.epp.flexfields.jpa.entity.FlexGroupEntity;
import com.edifecs.epp.flexfields.jpa.entity.GroupFields;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sandeep.kath on 5/10/2014.
 */
public class FlexValueDataStore implements IFlexFieldValueDataStore {

    @Override
    public FlexFieldValue createFlexFieldValue(FlexFieldValue flexFieldValue) throws ItemAlreadyExistsException, FieldValueException {
        if (flexFieldValue.getId() != null) {
            throw new ItemAlreadyExistsException(FlexFieldValue.class, flexFieldValue.getId());
        }
        validateFieldValue(flexFieldValue);
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        FlexFieldValueEntity flexFieldValueEntity = null;
        try {
            tx.begin();
            GroupFields groupField = getGroupFields(entityManager, flexFieldValue.getFlexGroupId(), flexFieldValue.getFlexFieldDefinitionId());

            Query q = entityManager
                    .createQuery("SELECT flexFieldValueEntity FROM FlexFieldValueEntity flexFieldValueEntity WHERE flexFieldValueEntity.groupField = :groupField " +
                            " and flexFieldValueEntity.entityName=:entityName and flexFieldValueEntity.entityID=:entityId")
                    .setParameter("groupField", groupField)
                    .setParameter("entityName", flexFieldValue.getEntityName())
                    .setParameter("entityId", flexFieldValue.getEntityID());

            List<FlexFieldValueEntity> flexFieldValueEntityList = (List<FlexFieldValueEntity>) q.getResultList();
            if (flexFieldValueEntityList.size() > 0) {
                for (FlexFieldValueEntity fieldValueEntityTemp :
                        flexFieldValueEntityList) {
                    fieldValueEntityTemp.setValue(flexFieldValue.getValue());
                    flexFieldValueEntity = fieldValueEntityTemp;
                }
            } else {
                flexFieldValueEntity = (FlexFieldValueEntity) ObjectConverter.apiToJpa(flexFieldValue);
                GroupFields groupFieldTemp = getGroupFields(entityManager, flexFieldValue.getFlexGroupId(), flexFieldValue.getFlexFieldDefinitionId());
                flexFieldValueEntity.setGroupField(groupFieldTemp);
                entityManager.persist(flexFieldValueEntity);
            }
            return (FlexFieldValue) ObjectConverter.jpaToApi(flexFieldValueEntity);
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
    public FlexFieldValue updateFlexFieldValue(FlexFieldValue flexFieldValue) throws ItemNotFoundException, FieldValueException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {

            tx.begin();
            validateFieldValue(flexFieldValue);
            FlexFieldValueEntity flexFieldValueEntity = (FlexFieldValueEntity) ObjectConverter.apiToJpa(flexFieldValue);
            GroupFields groupField = getGroupFields(entityManager, flexFieldValue.getFlexGroupId(), flexFieldValue.getFlexFieldDefinitionId());
            flexFieldValueEntity.setGroupField(groupField);

            if (flexFieldValueEntity != null) {
                ObjectConverter.copyCommonBeanProperties(flexFieldValue, flexFieldValueEntity);
                return (FlexFieldValue) ObjectConverter.jpaToApi(flexFieldValueEntity);
            } else {
                throw new ItemNotFoundException(FlexFieldValue.class, flexFieldValue.getId());
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
    public void deleteFlexFieldValue(FlexFieldValue flexFieldValue) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.remove(entityManager.find(FlexFieldValueEntity.class,
                    flexFieldValue.getId()));
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
    public FlexFieldValue getById(long id) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {

            FlexFieldValueEntity flexFieldValueEntity = entityManager.find(FlexFieldValueEntity.class, id);
            if (null != flexFieldValueEntity) {
                return (FlexFieldValue) ObjectConverter.jpaToApi(flexFieldValueEntity);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }

    }


    @Override
    public Boolean validateFieldValue(FlexFieldValue flexFieldValue) throws FieldValueException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            FlexFieldDefinition flexFieldDefinition = (FlexFieldDefinition) ObjectConverter.jpaToApi(entityManager.find(FlexFieldDefinitionEntity.class, flexFieldValue.getFlexFieldDefinitionId()));
            if (flexFieldDefinition.getRegEx() != null && flexFieldDefinition.getRegEx().length() != 0 && flexFieldValue.getValue() != null) {
                if (!Validator.regExMatched(flexFieldDefinition.getRegEx(), flexFieldValue.getValue())) {
                    throw new FieldValueException(flexFieldDefinition.getValidationMessage());
                }
            }
            if (!Validator.validateValue(flexFieldDefinition.getDataType(), flexFieldValue.getValue())) {
                throw new FieldValueException(flexFieldDefinition.getDataType().getText());
            } else {
                return true;
            }
        } finally {
            entityManager.close();
        }

    }

    private GroupFields getGroupFields(EntityManager entityManager, Long flexGroupId, Long flexFieldDefinitionId) throws FieldValueException {
        FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class,
                flexGroupId);
        FlexFieldDefinitionEntity flexFieldDefinitionEntity = entityManager.find(FlexFieldDefinitionEntity.class,
                flexFieldDefinitionId);

        if (null != flexGroupEntity && null != flexFieldDefinitionEntity) {
            GroupFields groupFields = (GroupFields) entityManager.createQuery("SELECT groupFields FROM GroupFields groupFields WHERE groupFields.field=:field AND groupFields.group=:group")
                    .setParameter("field", flexFieldDefinitionEntity)
                    .setParameter("group", flexGroupEntity).getSingleResult();

            return groupFields;


        } else throw new FieldValueException("FlexGroup", "FlexField");
    }

    public Map<FlexFieldDefinition, FlexFieldValue> getFlexFieldValues(FlexGroup flexGroup) throws FieldValueException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            Map<FlexFieldDefinition, FlexFieldValue> fieldValuesMap = new HashMap<FlexFieldDefinition, FlexFieldValue>();

            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class,
                    flexGroup.getId());
            if (null != flexGroupEntity) {
                // Get group fields
                for (GroupFields groupField : flexGroupEntity.getGroupFields()) {
                    try {
                        FlexFieldValueEntity flexFieldValueEntity = (FlexFieldValueEntity) entityManager.createQuery("SELECT flexFieldValue FROM FlexFieldValueEntity flexFieldValue JOIN flexFieldValue.groupField groupFields WHERE groupFields=:group")
                                .setParameter("group", groupField).getSingleResult();
                        fieldValuesMap.put((FlexFieldDefinition) ObjectConverter.jpaToApi(groupField.getField()), (FlexFieldValue) ObjectConverter.jpaToApi(flexFieldValueEntity));
                    } catch (NoResultException nre) {
                        fieldValuesMap.put((FlexFieldDefinition) ObjectConverter.jpaToApi(groupField.getField()), null);
                    }
                }
                //
                return fieldValuesMap;
            } else throw new FieldValueException("FlexGroup");

        } finally {
            entityManager.close();
        }
    }

    public List<FlexFieldDefinition> getFlexFields(FlexGroup flexGroup, long entityId) throws FieldValueException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        List<FlexFieldDefinition> flexFields = new ArrayList<>();
        try {
            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class,
                    flexGroup.getId());
            if (null != flexGroupEntity) {
                // Get group fields
                for (GroupFields groupField : flexGroupEntity.getGroupFields()) {
                    if (groupField.getField().getActiveFlag() == null || groupField.getField().getActiveFlag() == true) {
                        FlexFieldDefinition flexFieldDefinition = new FlexFieldDefinition();
                        try {

                            List<FlexFieldValueEntity> flexFieldValueEntityList = (ArrayList<FlexFieldValueEntity>) entityManager.createQuery("SELECT flexFieldValue FROM FlexFieldValueEntity flexFieldValue JOIN flexFieldValue.groupField groupFields WHERE groupFields=:group and entityID=:entityId")
                                    .setParameter("group", groupField)
                                    .setParameter("entityId", entityId)
                                    .setMaxResults(1)
                                    .getResultList();
                            flexFieldDefinition = (FlexFieldDefinition) ObjectConverter.jpaToApi(groupField.getField());
                            if (flexFieldValueEntityList.size() > 0) {
                                flexFieldDefinition.setFlexFieldValue((FlexFieldValue) ObjectConverter.jpaToApi(flexFieldValueEntityList.get(0)));
                            }

                        } catch (NoResultException nre) {
                            flexFieldDefinition = (FlexFieldDefinition) ObjectConverter.jpaToApi(groupField.getField());
                            flexFieldDefinition.setFlexFieldValue(null);

                        }
                        flexFields.add(flexFieldDefinition);
                    }
                    //
                }
                return flexFields;
            } else throw new FieldValueException("FlexGroup");

        } finally {
            entityManager.close();
        }
    }
}
