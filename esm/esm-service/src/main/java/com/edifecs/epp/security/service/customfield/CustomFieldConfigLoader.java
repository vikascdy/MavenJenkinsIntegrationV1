package com.edifecs.epp.security.service.customfield;

import com.edifecs.epp.security.datastore.ISecurityDataStore;
import com.edifecs.epp.security.exception.SecurityDataException;

import java.io.File;

public interface CustomFieldConfigLoader {
    String NAME = "customFieldConfigLoader";

    void load(File customFieldConfigJsonFile, ISecurityDataStore dataStore) throws SecurityDataException;
}
