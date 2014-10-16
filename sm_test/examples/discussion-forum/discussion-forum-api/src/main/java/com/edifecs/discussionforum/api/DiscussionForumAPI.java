package com.edifecs.discussionforum.api;

import com.edifecs.discussionforum.api.datastore.IForumDataStore;
import com.edifecs.discussionforum.api.exception.ForumException;
import com.edifecs.discussionforum.api.model.Category;
import com.edifecs.discussionforum.api.model.Reply;
import com.edifecs.discussionforum.api.model.Topic;
import com.edifecs.discussionforum.jpa.datastore.ForumDataStoreDBImpl;

/**
 * Created by sandeep.kath on 5/18/2014.
 */
public class DiscussionForumAPI {
    private IForumDataStore forumDataStore;

    public DiscussionForumAPI() {
        try {
            forumDataStore = new ForumDataStoreDBImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Category createCategory(Category category) {
        try {
            return forumDataStore.createCategory(category);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Topic createTopic(Category category, Topic topic) {
        try {
            return forumDataStore.createTopic(category,topic);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Category[] getCategories(long tenantId, int start, int end) {
        try {
            Category[] categories = forumDataStore.getCategories(tenantId, start, end);
            return categories;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Topic[] getTopics(Category category, int start, int end) {
        try {
            Topic[] topics = forumDataStore.getTopic(category, start, end);
            return topics;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Reply[] getReplies(Topic topic, int start, int end) {
        try {
            Reply[] replies = forumDataStore.getReplies(topic, start, end);
            return replies;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Reply addReply(Topic topic, Reply reply) throws ForumException {
        return forumDataStore.addReply(topic, reply);
    }

    public static void main(String... args) {
        DiscussionForumAPI discussionForumAPI = new DiscussionForumAPI();
        Topic topic = new Topic();
        topic.setId(58L);
        System.out.println(discussionForumAPI.getReplies(topic, 0, 100).length);
    }

}
