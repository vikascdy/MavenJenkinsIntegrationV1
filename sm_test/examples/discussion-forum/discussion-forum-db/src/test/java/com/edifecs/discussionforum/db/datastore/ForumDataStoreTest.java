package com.edifecs.discussionforum.db.datastore;

import com.edifecs.discussionforum.api.datastore.IForumDataStore;
import com.edifecs.discussionforum.api.model.Category;
import com.edifecs.discussionforum.api.model.Reply;
import com.edifecs.discussionforum.api.model.Topic;
import com.edifecs.discussionforum.jpa.datastore.DatabaseDataStore;
import com.edifecs.discussionforum.jpa.datastore.ForumDataStoreDBImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sandeep.kath on 5/14/2014.
 */
public class ForumDataStoreTest {
    private IForumDataStore forumDataStore;
    private DatabaseDataStore databaseDataStore;
    @Before
    public void before() throws Exception {
        databaseDataStore = new DatabaseDataStore();
        forumDataStore = new ForumDataStoreDBImpl();
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: createCategory(Category category)
     */
    @Test
    public void testCreateCategory() throws Exception {
        try {
            Category category = new Category();
            category.setName("Java 8 Features");
            category.setDescription("Java 8 Features");
            category.setTenantId(1);
            Category categoryPersisted = forumDataStore.createCategory(category);
            assertNotNull(categoryPersisted.getId());
            assertEquals(categoryPersisted.getName(), "Java 8 Features");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: createTopic(Category category, Topic topic)
     */
    @Test
    public void testCreateTopic() throws Exception {
        try {
            Category category = new Category();
            category.setName("Open Stack");
            category.setDescription("Open Stack Features");
            category.setTenantId(1);
            Category categoryPersisted = forumDataStore.createCategory(category);

            assertNotNull(categoryPersisted.getId());
            assertEquals(categoryPersisted.getName(), "Open Stack");

            Topic topic = new Topic();
            topic.setSubject("Dev Stack");

            Topic topicPersisted = forumDataStore.createTopic(categoryPersisted, topic);

            assertNotNull(topicPersisted.getId());
            assertEquals(topicPersisted.getSubject(), "Dev Stack");

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Method: addReply(Topic topic, Reply reply)
     */
    @Test
    public void testAddReply() throws Exception {
        try {
            Category category = new Category();
            category.setName("Core OS");
            category.setTenantId(1);
            category.setDescription("Core OS Features");
            Category categoryPersisted = forumDataStore.createCategory(category);

            assertNotNull(categoryPersisted.getId());
            assertEquals(categoryPersisted.getName(), "Core OS");

            Topic topic = new Topic();
            topic.setSubject("Dockers");

            Topic topicPersisted = forumDataStore.createTopic(categoryPersisted, topic);

            assertNotNull(topicPersisted.getId());
            assertEquals(topicPersisted.getSubject(), "Dockers");

            Reply reply = new Reply();
            reply.setMessage("Dockers Cartridges");

            Reply replyPersisted = forumDataStore.addReply(topicPersisted, reply);
            assertNotNull(replyPersisted.getId());
            assertEquals(replyPersisted.getMessage(), "Dockers Cartridges");

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Method: getCategories(int start, int end)
     */
    @Test
    public void testGetCategories() throws Exception {
        try {
            Category category = new Category();
            category.setName("Scala");
            category.setDescription("Scala");
            Category categoryPersisted = forumDataStore.createCategory(category);
            Category[] categories = forumDataStore.getCategories(1, 0, 10);

            assertNotNull(categories);
            assertTrue(categories.length > 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Method: getTopic(Category category, int start, int end)
     */
    @Test
    public void testGetTopic() throws Exception {
        try {
            Category category = new Category();
            category.setName("Open Stack");
            category.setTenantId(1);
            category.setDescription("Open Stack Features");
            Category categoryPersisted = forumDataStore.createCategory(category);

            assertNotNull(categoryPersisted.getId());
            assertEquals(categoryPersisted.getName(), "Open Stack");

            Topic topic = new Topic();
            topic.setSubject("Dev Stack");
            topic.setCategory(categoryPersisted);

            Topic topicPersisted = forumDataStore.createTopic(categoryPersisted, topic);

            assertNotNull(topicPersisted.getId());
            assertEquals(topicPersisted.getSubject(), "Dev Stack");

            Topic[] topics = forumDataStore.getTopic(categoryPersisted, 0, 100);
            assertNotNull(topics);
            assertTrue(topics.length > 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Method: getReplies(Topic topic, int start, int end)
     */
    @Test
    public void testGetReplies() throws Exception {
        try {
            Category category = new Category();
            category.setName("Core OS");
            category.setDescription("Core OS Features");
            category.setTenantId(1);
            Category categoryPersisted = forumDataStore.createCategory(category);

            assertNotNull(categoryPersisted.getId());
            assertEquals(categoryPersisted.getName(), "Core OS");

            Topic topic = new Topic();
            topic.setSubject("Dockers");
            topic.setCategory(categoryPersisted);

            Topic topicPersisted = forumDataStore.createTopic(categoryPersisted, topic);

            assertNotNull(topicPersisted.getId());
            assertEquals(topicPersisted.getSubject(), "Dockers");

            Reply reply = new Reply();
            reply.setMessage("Dockers Cartridges");
          //  reply.setTopic(topicPersisted);

            Reply replyPersisted = forumDataStore.addReply(topicPersisted, reply);
            assertNotNull(replyPersisted.getId());
            assertEquals(replyPersisted.getMessage(), "Dockers Cartridges");

            Reply[] replies = forumDataStore.getReplies(topicPersisted, 0, 100);
            assertNotNull(replies);
            assertTrue(replies.length > 0);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


}

