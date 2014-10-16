package com.edifecs.resource.validation.service;

import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

@Service(
        name = "DB Validation Service",
        version = "1.0",
        description = "Validates DB Resources"
)
public class DBValidationService extends AbstractService implements IDBValidationService {

	@Override
	public void start() throws Exception {
		getLogger().debug("{} Started", this.getClass());
	}

	@Override
	public void stop() throws Exception {
		 getLogger().debug("{} Stopped", this.getClass());
	}

    @Override
    public IDBValidationHandler getDBValidationHandler() {
        return new DBValidationHandler();
    }
}
