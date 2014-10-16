package com.edifecs.epp.security.datastore;

import com.edifecs.epp.security.data.Site;

public interface ISiteDataStore extends IBaseOwnerDataStore<Site> {

    public Site getSite();

}
