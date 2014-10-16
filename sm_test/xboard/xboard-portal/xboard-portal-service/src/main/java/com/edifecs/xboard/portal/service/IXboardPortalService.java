package com.edifecs.xboard.portal.service;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Service;

@Service(
    name = SystemVariables.NAVIGATION_SERVICE_TYPE_NAME,
    version = "1.0",
    description = "Allows Products and Services to register navigation menu entries in the Doormat global navigation bar."
)
public interface IXboardPortalService {

	@Handler
    public IXPNavigationCommandHandler getNavigationCommandHandler();

    @Handler
    public IXPFeatureItemCommandHandler getFeatureItemCommandHandler();
}
