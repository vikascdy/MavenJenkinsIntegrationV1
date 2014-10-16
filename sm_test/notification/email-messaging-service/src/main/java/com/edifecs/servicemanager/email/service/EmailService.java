package com.edifecs.servicemanager.email.service;

import com.edifecs.servicemanager.annotations.Resource;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * Service designed to test all types of annotations.
 * 
 * @author abhising
 */
@Service(
	name = "Email Service", 
	version = "1.0", 
	description = "Messaging service for sending emails",
	resources = {
		@Resource(name = "Email Service SMTP Server", type = "SMTP Server", unique = false)
	}
)
public class EmailService extends AbstractService implements IEmailService {

	@Override
	public void start() throws Exception {
		getLogger().debug("{}: Service Successfully Started", getServiceAnnotation().name());
	}

	@Override
	public void stop() throws Exception {
		getLogger().debug("{}: Service Successfully Stopped",
				getServiceAnnotation().name());
	}

    @Override
    public IEmailCommandHandler getEmailCommandHandler() {
        return new EmailCommandHandler(getResources().get("Email Service SMTP Server"));
    }
}
