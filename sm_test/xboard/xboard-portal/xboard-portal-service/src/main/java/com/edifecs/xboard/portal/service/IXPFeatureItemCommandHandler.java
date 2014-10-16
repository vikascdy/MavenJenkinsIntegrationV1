package com.edifecs.xboard.portal.service;

import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.annotations.SyncCommand;
import com.edifecs.xboard.portal.FeaturedItemJsonWrapper;

import java.util.Collection;

/**
 * Created by abhising on 25-08-2014.
 */
@CommandHandler
public interface
        IXPFeatureItemCommandHandler {

    @SyncCommand
    Collection<FeaturedItemJsonWrapper.Section> getSectionedFeatureItems();
}
