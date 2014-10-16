package com.edifecs.epp.security.flexfields;

import com.edifecs.epp.flexfields.model.FlexFieldValue;
import com.edifecs.epp.flexfields.model.FlexGroup;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.annotations.SyncCommand;

import java.util.Collection;
import java.util.HashMap;


/**
 * Created by sandeep.kath on 7/18/2014.
 */
@CommandHandler(namespace = "FlexField", description = "FlexFields Handler using ESM")
public interface IFlexFieldHandler {
    @SyncCommand
    public Collection<FlexGroup> getFields(@Arg(name = "contextMap", required = true) HashMap<String, String> contextMap) throws Exception;

    @SyncCommand
    FlexFieldValue setFlexFieldValue(@Arg(name = "flexFieldValue", required = true) FlexFieldValue flexFieldValue) throws Exception;
}
