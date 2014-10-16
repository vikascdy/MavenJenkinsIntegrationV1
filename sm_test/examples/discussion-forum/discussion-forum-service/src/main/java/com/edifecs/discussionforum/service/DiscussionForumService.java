package com.edifecs.discussionforum.service;

import com.edifecs.discussionforum.handler.DiscussionForumCommandHandler;
import com.edifecs.discussionforum.handler.IDiscussionForumCommandHandler;
import com.edifecs.servicemanager.api.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sandeep.kath on 5/15/2014.
 */
public class DiscussionForumService extends AbstractService implements IDiscussionForumService {
    private Logger logger = LoggerFactory.getLogger(DiscussionForumService.class);

    @Override
    public void start() throws Exception {
        String greet = getProperties().getProperty("name");
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public IDiscussionForumCommandHandler getDiscussionForumCommandHandler() {
        return new DiscussionForumCommandHandler();
    }
}
