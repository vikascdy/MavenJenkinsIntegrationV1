package com.edifecs.epp.flexfields.jpa.helper;

import com.edifecs.epp.flexfields.datastore.IFlexGroupDataStore;
import com.edifecs.epp.flexfields.exception.FieldValueException;
import com.edifecs.epp.flexfields.exception.FlexFieldRegistryException;
import com.edifecs.epp.flexfields.exception.ItemAlreadyExistsException;
import com.edifecs.epp.flexfields.exception.ItemNotFoundException;

import com.edifecs.epp.flexfields.jpa.entity.FlexFieldDefinitionEntity;
import com.edifecs.epp.flexfields.jpa.entity.FlexGroupEntity;
import com.edifecs.epp.flexfields.jpa.entity.GroupFields;
import com.edifecs.epp.flexfields.model.Context;
import com.edifecs.epp.flexfields.model.FlexFieldDefinition;
import com.edifecs.epp.flexfields.model.FlexGroup;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by sandeep.kath on 5/8/2014.
 */
public class FlexGroupDataStore implements IFlexGroupDataStore {
    private FlexValueDataStore flexValueDataStore;

    @Override
    public FlexGroup createFlexGroup(FlexGroup flexGroup) throws ItemAlreadyExistsException {
        if (flexGroup.getId() != null) {
            throw new ItemAlreadyExistsException(FlexGroup.class, flexGroup.getId());
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            FlexGroupEntity flexGroupEntity = (FlexGroupEntity) ObjectConverter.apiToJpa(flexGroup);
            entityManager.persist(flexGroupEntity);
            return (FlexGroup) ObjectConverter.jpaToApi(flexGroupEntity);
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
    public FlexGroup updateFlexGroup(FlexGroup flexGroup) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class, flexGroup.getId());
            if (null != flexGroupEntity) {
                ObjectConverter.copyCommonBeanProperties(flexGroup, flexGroupEntity);
                return (FlexGroup) ObjectConverter.jpaToApi(flexGroupEntity);
            } else {
                throw new ItemNotFoundException(FlexGroup.class, flexGroup.getId());
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
    public void deleteFlexGroup(FlexGroup flexGroup) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.remove(entityManager.find(FlexGroupEntity.class,
                    flexGroup.getId()));
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
    public Collection<FlexGroup> getRange(long startRecord, long recordCount) throws FlexFieldRegistryException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<FlexGroup> flexGroups = new ArrayList<FlexGroup>();
            @SuppressWarnings("unchecked")
            List<FlexGroupEntity> flexGroupEntities = entityManager
                    .createNamedQuery(FlexGroupEntity.FIND_ALL_FLEX_GROUPS)
                    .setFirstResult((int) startRecord)
                    .setMaxResults((int) recordCount).getResultList();

            for (FlexGroupEntity group : flexGroupEntities) {
                flexGroups.add((FlexGroup) ObjectConverter.jpaToApi(group));
            }
            return flexGroups;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public FlexGroup getById(long id) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class, id);
            if (null != flexGroupEntity) {
                return (FlexGroup) ObjectConverter.jpaToApi(flexGroupEntity);
            } else {
                return null;
            }
        } finally {
            entityManager.close();
        }

    }

    @Override
    public void addFlexFieldToGroup(FlexGroup flexGroup, FlexFieldDefinition flexFieldDefinition, int sequence) throws ItemAlreadyExistsException, ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class,
                    flexGroup.getId());
            FlexFieldDefinitionEntity flexFieldDefinitionEntity = entityManager.find(FlexFieldDefinitionEntity.class,
                    flexFieldDefinition.getId());

            if (null != flexGroupEntity && null != flexFieldDefinitionEntity) {
                try {
                    GroupFields groupFields = (GroupFields) entityManager.createQuery("SELECT groupFields FROM GroupFields groupFields WHERE groupFields.field=:field AND groupFields.group=:group")
                            .setParameter("field", flexFieldDefinitionEntity)
                            .setParameter("group", flexGroupEntity).getSingleResult();
                    if (groupFields != null) {
                        throw new ItemAlreadyExistsException(FlexFieldDefinition.class, flexFieldDefinition);
                    }
                } catch (NoResultException noResultException) {
                    GroupFields groupFieldsDB = new GroupFields();
                    groupFieldsDB.setSequence(sequence);
                    groupFieldsDB.setField(flexFieldDefinitionEntity);
                    groupFieldsDB.setGroup(flexGroupEntity);
                    entityManager.persist(groupFieldsDB);
                    flexGroupEntity.getGroupFields().add(groupFieldsDB);
                }

            } else {
                throw new ItemNotFoundException(FlexFieldDefinition.class, flexFieldDefinition);
            }
            entityManager.flush();

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
    public void removeFlexFieldFromGroup(FlexGroup flexGroup, FlexFieldDefinition flexFieldDefinition) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class,
                    flexGroup.getId());
            FlexFieldDefinitionEntity flexFieldDefinitionEntity = entityManager.find(FlexFieldDefinitionEntity.class,
                    flexFieldDefinition.getId());

            if (null != flexGroupEntity && null != flexFieldDefinitionEntity) {
                try {
                    GroupFields groupFields = (GroupFields) entityManager.createQuery("SELECT groupFields FROM GroupFields groupFields WHERE groupFields.field=:field AND groupFields.group=:group")
                            .setParameter("field", flexFieldDefinitionEntity)
                            .setParameter("group", flexGroupEntity).getSingleResult();
                    entityManager.remove(entityManager.find(GroupFields.class,
                            groupFields.getId()));
                } catch (NoResultException noResultException) {
                    throw new ItemNotFoundException(FlexFieldDefinition.class, flexFieldDefinition);
                }

            } else {
                throw new ItemNotFoundException(FlexFieldDefinition.class, flexFieldDefinition);
            }
            entityManager.flush();

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
    public Collection<FlexFieldDefinition> getFields(FlexGroup flexGroup) {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            List<FlexFieldDefinition> flexFieldDefinitions = new ArrayList<FlexFieldDefinition>();
            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class,
                    flexGroup.getId());
            for (GroupFields groupFields : flexGroupEntity.getGroupFields()) {
                FlexFieldDefinition flexFieldDefinition = (FlexFieldDefinition) ObjectConverter.jpaToApi(groupFields.getField());
                flexFieldDefinitions.add(flexFieldDefinition);
            }
            return flexFieldDefinitions;
        } finally {
            entityManager.close();
        }
    }

    //TODO : Detailed discussion is required.
    @Override
    public String[] getRequiredPermission(FlexGroup group) throws ItemNotFoundException {
        return new String[0];
    }


    @Override
    public Collection<FlexGroup> getFlexGroupsByContext(String contextName, String contextValue) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<FlexGroup> flexGroups = new ArrayList<FlexGroup>();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FlexGroupEntity> cq = cb.createQuery(FlexGroupEntity.class);

            Root<FlexGroupEntity> from = cq.from(FlexGroupEntity.class);
            CriteriaQuery<FlexGroupEntity> select = cq.select(from);

            Predicate predicate = cb.equal(from.get(contextName), contextValue);
            cq.where(predicate);

            TypedQuery<FlexGroupEntity> typedQuery = entityManager.createQuery(select);

            List<FlexGroupEntity> flexGroupEntities = typedQuery.getResultList();

            for (FlexGroupEntity group : flexGroupEntities) {
                flexGroups.add((FlexGroup) ObjectConverter.jpaToApi(group));
            }
            return flexGroups;
        } catch (NoResultException e) {
            throw new ItemNotFoundException(String.class, contextName + ":" + contextValue);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Collection<FlexGroup> getFlexGroupsByContext(Map<Context, String> contextMap) throws ItemNotFoundException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();

        try {
            List<FlexGroup> flexGroups = new ArrayList<FlexGroup>();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FlexGroupEntity> cq = cb.createQuery(FlexGroupEntity.class);

            Root<FlexGroupEntity> from = cq.from(FlexGroupEntity.class);
            CriteriaQuery<FlexGroupEntity> select = cq.select(from);
            List<Predicate> predicateList = new ArrayList<>();

            for (Map.Entry<Context, String> entry : contextMap.entrySet()) {
                if (entry.getValue() != "*") {
                    Predicate predicate = cb.equal(from.get(entry.getKey().getText()), entry.getValue());
                    predicateList.add(predicate);
                }
            }

            cq.where(predicateList.toArray(new Predicate[]{}));
            TypedQuery<FlexGroupEntity> typedQuery = entityManager.createQuery(select);
            List<FlexGroupEntity> flexGroupEntities = typedQuery.getResultList();

            for (FlexGroupEntity group : flexGroupEntities) {
                // this check should be in criteria query.
                if (group.getParent() == null) {
                    flexGroups.add((FlexGroup) ObjectConverter.jpaToApi(group));
                }
            }
            return flexGroups;
        } catch (NoResultException e) {
            throw new ItemNotFoundException(String.class, "Could not find FlexGroup as per given context map.");
        } finally {
            entityManager.close();
        }
    }

    @Override
    public FlexGroup setParent(FlexGroup parent, FlexGroup child) throws ItemNotFoundException, ItemAlreadyExistsException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            FlexGroupEntity flexGroupParentEntity = entityManager.find(FlexGroupEntity.class,
                    parent.getId());
            FlexGroupEntity flexGroupChildEntity = entityManager.find(FlexGroupEntity.class,
                    child.getId());

            if (null != flexGroupParentEntity && null != flexGroupChildEntity) {
                if (flexGroupParentEntity.getChildren().contains(flexGroupChildEntity)) {
                    throw new ItemAlreadyExistsException(FlexGroup.class, child.getId());
                } else {
                    flexGroupChildEntity.setParent(flexGroupParentEntity);
                    flexGroupParentEntity.getChildren().add(flexGroupChildEntity);
                }
            } else {
                throw new ItemNotFoundException(FlexGroup.class, parent.getId());
            }
            entityManager.flush();
            return (FlexGroup) ObjectConverter.jpaToApi(flexGroupParentEntity);
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
    public FlexGroup getFlexGroupHierarchy(FlexGroup flexGroup, long entityId) throws ItemNotFoundException, FieldValueException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            FlexGroupEntity flexGroupEntity = entityManager.find(FlexGroupEntity.class,
                    flexGroup.getId());

            flexValueDataStore = new FlexValueDataStore();
            flexGroup.setChildren(new HashSet<FlexGroup>());
            processNestedGroups(flexGroupEntity, flexGroup, entityId);
            return flexGroup;
        } finally {
            entityManager.close();
        }
    }

    private void processNestedGroups(FlexGroupEntity flexGroupEntity, FlexGroup parentGroupNode, long entityId) throws FieldValueException {
        parentGroupNode.setFlexFieldsCollection(flexValueDataStore.getFlexFields(parentGroupNode, entityId));
        Collection<FlexGroupEntity> children = flexGroupEntity.getChildren();
        for (FlexGroupEntity flexGroupEntityChild : children) {
            FlexGroup _flexGroup = (FlexGroup) ObjectConverter.jpaToApi(flexGroupEntityChild);
            _flexGroup.setChildren(new HashSet<FlexGroup>());
            parentGroupNode.addChild(_flexGroup);
            processNestedGroups(flexGroupEntityChild, _flexGroup,entityId);
        }
    }

}
