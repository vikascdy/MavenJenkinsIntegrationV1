package com.edifecs.epp.flexfields.handler;

import com.edifecs.epp.flexfields.model.*;
import com.edifecs.epp.isc.annotations.*;

import java.util.Collection;
import java.util.Map;

/**
 * Created by sandeep.kath on 5/27/2014.
 */
@CommandHandler(namespace = "FlexField", description = "FlexField Handler")
public interface IFlexFieldHandler {

    @Command
    public Collection<FlexGroup> getFields(@Arg(name = "contextMap", required = true) Map<String, String> contextMap) throws Exception;

    @Command
    FlexFieldValue setFlexFieldValue(@Arg(name = "flexFieldValue", required = true) FlexFieldValue flexFieldValue) throws Exception;

    @Command
    public FlexGroup addFieldToGroup(@Arg(name = "flexFieldDefinition", required = true) FlexFieldDefinition flexFieldDefinition,
                                     @Arg(name = "flexGroup", required = true)
                                     FlexGroup flexGroup) throws Exception;

    @Command
    public FlexGroup addFieldsToGroup(@Arg(name = "flexFields", required = true) Collection<FlexFieldDefinition> flexFields,
                                      @Arg(name = "flexGroup", required = true)
                                      FlexGroup flexGroup) throws Exception;

    @Command
    public void deleteFlexGroup(@Arg(name = "flexGroup", required = true) FlexGroup flexGroup) throws Exception;

    @Command
    public void deleteFlexFieldDefinition(@Arg(name = "flexFieldDefinition", required = true) FlexFieldDefinition flexFieldDefinition) throws Exception;

    @Command
    public FlexGroup setParent(@Arg(name = "parent", required = true) FlexGroup parent,@Arg(name = "child", required = true) FlexGroup child) throws Exception;

    //TODO: Message Streams are not working, using string for Manifest
    @Command
    public boolean parseAppManifest(@Arg(name = "manifestString") String manifestString) throws Exception;

}
