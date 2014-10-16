package com.edifecs.discussionforum.handler;

import com.edifecs.discussionforum.api.exception.ForumException;
import com.edifecs.discussionforum.api.model.Category;
import com.edifecs.discussionforum.api.model.Reply;
import com.edifecs.discussionforum.api.model.Topic;
import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.security.exception.SecurityManagerException;

@Akka(enabled = true)
@CommandHandler(namespace = "discussion-forum", description = "")
public interface IDiscussionForumCommandHandler {
    @SyncCommand
    public String greeting() throws SecurityManagerException;

    @SyncCommand
    @RequiresPermissions("discussionforum:category:add")
    public Category createCategory(@Arg(name = "name", required = true) String name, @Arg(name = "description") String description) throws ForumException, SecurityManagerException;

    @SyncCommand
    @RequiresPermissions("discussionforum:topic:add")
    public Topic createTopic(@Arg(name = "categoryId", required = true) long categoryId, @Arg(name = "topicSubject", required = true) String topicSubject) throws ForumException, SecurityManagerException;

    @SyncCommand
    @RequiresPermissions("discussionforum:reply:add")
    public Reply addReply(@Arg(name = "topicId", required = true) long topicId, @Arg(name = "message", required = true) String message) throws ForumException, SecurityManagerException;

    @SyncCommand
    public Category[] getCategories(@Arg(name = "start") int start, @Arg(name = "end") int end) throws ForumException;


    @SyncCommand
    public Topic[] getTopics(@Arg(name = "categoryId", required = true) String categoryId, @Arg(name = "start") int start, @Arg(name = "end") int end) throws ForumException;

    @SyncCommand
    public Reply[] getReplies(@Arg(name = "topicId", required = true) String topicId, @Arg(name = "start", required = true) int start, @Arg(name = "end") int end) throws ForumException;

}
