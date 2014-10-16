package com.edifecs.xboard.portal.service;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.xboard.portal.FeaturedItemJsonWrapper;
import com.edifecs.xboard.portal.api.XPFeatureItemDatastore;

import java.util.Collection;

/**
 * Created by abhising on 25-08-2014.
 */
public class XPFeatureItemHandler extends AbstractCommandHandler implements IXPFeatureItemCommandHandler {

    @Override
    public Collection<FeaturedItemJsonWrapper.Section> getSectionedFeatureItems() {
        return XPFeatureItemDatastore.getUserSections(getSecurityManager());
    }
}
