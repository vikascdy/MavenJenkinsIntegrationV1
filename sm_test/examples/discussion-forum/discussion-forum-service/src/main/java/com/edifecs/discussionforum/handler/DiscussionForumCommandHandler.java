package com.edifecs.discussionforum.handler;

import com.edifecs.discussionforum.api.DiscussionForumAPI;
import com.edifecs.discussionforum.api.exception.ForumException;
import com.edifecs.discussionforum.api.model.Category;
import com.edifecs.discussionforum.api.model.Reply;
import com.edifecs.discussionforum.api.model.Topic;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.exception.SecurityManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by sandeep.kath on 5/15/2014.
 */
public class DiscussionForumCommandHandler extends AbstractCommandHandler implements IDiscussionForumCommandHandler {

    private Logger logger = LoggerFactory.getLogger(DiscussionForumCommandHandler.class);
    DiscussionForumAPI forumAPI = new DiscussionForumAPI();

    @Override
    public String greeting() throws SecurityManagerException{
        return getCommandCommunicator().getSecurityManager().getSubjectManager().getTenant().getCanonicalName() +" Discussion Forum";
    }

    @Override
    public Category createCategory(@Arg(name = "name", required = true) String name, @Arg(name = "description") String description ) throws ForumException, SecurityManagerException {
        long tenantId = getCommandCommunicator().getSecurityManager().getSubjectManager().getTenant().getId();
        String username = getCommandCommunicator().getSecurityManager().getSubjectManager().getUser().getUsername();
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setTenantId(tenantId);
        category.setCreatedBy(username);
        Category categoryPersisted = forumAPI.createCategory(category);

        return categoryPersisted;
    }

    @Override
    public Topic createTopic(@Arg(name = "categoryId", required = true) long categoryId, @Arg(name = "topicSubject", required = true) String topicSubject) throws ForumException,SecurityManagerException {
        String username = getCommandCommunicator().getSecurityManager().getSubjectManager().getUser().getUsername();
        Category category = new Category();
        category.setId(categoryId);

        Topic topic = new Topic();
        topic.setCategory(category);
        topic.setSubject(topicSubject);
        topic.setCreatedBy(username);
        return  forumAPI.createTopic(category,topic);
    }

    @Override
    public Reply addReply(@Arg(name = "topicId", required = true) long topicId, @Arg(name = "message", required = true) String message) throws ForumException, SecurityManagerException {
        String username = getCommandCommunicator().getSecurityManager().getSubjectManager().getUser().getUsername();
        Date date = new Date();
        Topic topic = new Topic();
        topic.setId(topicId);
        Reply reply = new Reply();
        reply.setMessage(message);
        reply.setTopic(topic);
        reply.setReplyDate(date);
        reply.setCreatedBy(username);
        Reply replyPersisted = forumAPI.addReply(topic, reply);
        return replyPersisted;
    }


    @Override
    public Category[] getCategories(@Arg(name = "start") int start, @Arg(name = "end") int end) throws ForumException {
        try {
            long tenantId = getCommandCommunicator().getSecurityManager().getSubjectManager().getTenant().getId();
            return forumAPI.getCategories(tenantId, start, end);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }


    @Override
    public Topic[] getTopics(@Arg(name = "categoryId", required = true) String categoryId, @Arg(name = "start") int start, @Arg(name = "end") int end) throws ForumException {
        Category category = new Category();
        category.setId(Long.parseLong(categoryId));
        return forumAPI.getTopics(category, start, end);
    }

    @Override
    public Reply[] getReplies(@Arg(name = "topicId", required = true) String topicId, @Arg(name = "start") int start, @Arg(name = "end") int end) throws ForumException {
        Topic topic = new Topic();
        topic.setId(Long.parseLong(topicId));
        return forumAPI.getReplies(topic, start, end);
    }
}
