package com.edifecs.discussionforum.service;

import com.edifecs.discussionforum.handler.IDiscussionForumCommandHandler;
import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Property;
import com.edifecs.servicemanager.annotations.Service;

/**
 * Created by sandeep.kath on 5/20/2014.
 */

@Service(
        name = "discussion-forum-service",
        version = "1.0",
        description = "Discussion Forum Service",
        properties = {@Property(name = "name", propertyType = Property.PropertyType.STRING, description = "Discussion Forum Name" , defaultValue = "Technology Trends", required = false)}
)
public interface IDiscussionForumService {

    @Handler
    IDiscussionForumCommandHandler getDiscussionForumCommandHandler();
}
