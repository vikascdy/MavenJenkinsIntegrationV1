package com.edifecs.discussionforum.api.datastore;

import com.edifecs.discussionforum.api.exception.ForumException;
import com.edifecs.discussionforum.api.model.Category;
import com.edifecs.discussionforum.api.model.Reply;
import com.edifecs.discussionforum.api.model.Topic;

/**
 * Created by sandeep.kath on 5/14/2014.
 */
public interface IForumDataStore {
    public Category createCategory(Category category) throws ForumException;
    public Topic createTopic(Category category, Topic topic ) throws  ForumException;
    public Reply addReply(Topic topic, Reply reply) throws ForumException;
    public Category[] getCategories(long tenantId, int start, int end) throws ForumException;
    public Topic[] getTopic(Category category, int start, int end) throws ForumException;
    public Reply[] getReplies(Topic topic, int start, int end) throws ForumException;

}
