package com.edifecs.discussionforum.jpa.datastore;

import com.edifecs.discussionforum.api.datastore.IForumDataStore;
import com.edifecs.discussionforum.api.exception.ForumException;
import com.edifecs.discussionforum.api.model.Category;
import com.edifecs.discussionforum.api.model.Reply;
import com.edifecs.discussionforum.api.model.Topic;
import com.edifecs.discussionforum.db.helper.ObjectConverter;
import com.edifecs.discussionforum.jpa.entity.CategoryEntity;
import com.edifecs.discussionforum.jpa.entity.ReplyEntity;
import com.edifecs.discussionforum.jpa.entity.TopicEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep.kath on 5/18/2014.
 */
public class ForumDataStoreDBImpl implements IForumDataStore {

    private DatabaseDataStore databaseDataStore;

    public ForumDataStoreDBImpl() throws Exception{
        //TODO - Pass DB properties
        databaseDataStore = new DatabaseDataStore();
    }
    @Override
    public Category createCategory(Category category) throws ForumException {
        if (category.getRowId() != null) {
            throw new ForumException("Item already exists..");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            CategoryEntity categoryEntity = (CategoryEntity) ObjectConverter.modelToEntity(category);
            entityManager.persist(categoryEntity);
            return (Category) ObjectConverter.entityToModel(categoryEntity);
        } catch (Exception e) {
            tx.rollback();
            throw new ForumException(e.getMessage());
        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public Topic createTopic(Category category, Topic topic) throws ForumException {
        if (topic.getRowId() != null) {
            throw new ForumException("Item already exists..");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            CategoryEntity categoryEntity = (CategoryEntity) ObjectConverter.modelToEntity(category);
            CategoryEntity categoryEntityPersisted = entityManager.find(CategoryEntity.class,
                    categoryEntity.getId());
            if(categoryEntityPersisted !=null) {
                TopicEntity topicEntity = (TopicEntity) ObjectConverter.modelToEntity(topic);
                topicEntity.setCategory(categoryEntityPersisted);
                entityManager.persist(topicEntity);
                categoryEntityPersisted.getTopics().add(topicEntity);
                return (Topic) ObjectConverter.entityToModel(topicEntity);
            } else {
                throw new ForumException("Category ID is not valid.");
            }
        } catch (Exception e) {
            tx.rollback();
            throw new ForumException(e.getMessage());

        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public Reply addReply(Topic topic, Reply reply) throws ForumException {
        if (reply.getRowId() != null) {
            throw new ForumException("Item already exists..");
        }
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            TopicEntity topicEntity = (TopicEntity) ObjectConverter.modelToEntity(topic);
            TopicEntity topicEntityPersisted = entityManager.find(TopicEntity.class,
                    topicEntity.getId());
            if(topicEntityPersisted !=null) {
                ReplyEntity replyEntity = (ReplyEntity) ObjectConverter.modelToEntity(reply);
                replyEntity.setTopic(topicEntityPersisted);
                entityManager.persist(replyEntity);
                topicEntityPersisted.getReplies().add(replyEntity);
                return (Reply) ObjectConverter.entityToModel(replyEntity);
            } else {
                throw new ForumException("Topic ID is not valid.");
            }
        } catch (Exception e) {
            tx.rollback();
            throw new ForumException(e.getMessage());

        } finally {
            if (tx.isActive()) {
                tx.commit();
            }
            entityManager.close();
        }
    }

    @Override
    public Category[] getCategories(long tenantId, int start, int end) throws ForumException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Category> categories = new ArrayList<Category>();
            @SuppressWarnings("unchecked")
            List<CategoryEntity> categoryEntities = entityManager
                    .createNamedQuery(CategoryEntity.FIND_ALL_CATEGORIES)
                    .setParameter("tenantId", tenantId)
//                    .setFirstResult(start)
//                    .setMaxResults(end)
                    .getResultList();

            for (CategoryEntity categoryEntity : categoryEntities) {
                categories.add((Category) ObjectConverter.entityToModel(categoryEntity));
            }
            return categories.toArray(new Category[categories.size()]);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Topic[] getTopic(Category category, int start, int end) throws ForumException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Topic> topics = new ArrayList<Topic>();
            CategoryEntity categoryEntity = (CategoryEntity) ObjectConverter.modelToEntity(category);
            @SuppressWarnings("unchecked")
            List<TopicEntity> topicEntities = entityManager
                    .createNamedQuery(TopicEntity.FIND_ALL_TOPICS)
                    .setParameter("category", categoryEntity)
//                    .setFirstResult(start)
//                    .setMaxResults(end)
                    .getResultList();

            for (TopicEntity topicEntity : topicEntities) {
                topics.add((Topic) ObjectConverter.entityToModel(topicEntity));
            }
            return topics.toArray(new Topic[topics.size()]);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Reply[] getReplies(Topic topic, int start, int end) throws ForumException {
        EntityManager entityManager = DatabaseDataStore.createEntityManager();
        try {
            List<Reply> replies = new ArrayList<Reply>();
            TopicEntity topicEntity = (TopicEntity) ObjectConverter.modelToEntity(topic);
            @SuppressWarnings("unchecked")
            List<ReplyEntity> replyEntities = entityManager
                    .createNamedQuery(ReplyEntity.FIND_ALL_REPLIES)
                    .setParameter("topic",topicEntity)
//                    .setFirstResult(start)
//                    .setMaxResults(end)
                     .getResultList();

            for (ReplyEntity replyEntity : replyEntities) {
                replies.add((Reply) ObjectConverter.entityToModel(replyEntity));
            }
            return replies.toArray(new Reply[replies.size()]);
        } finally {
            entityManager.close();
        }
    }
}
