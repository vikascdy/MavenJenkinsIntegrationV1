package com.edifecs.epp.security.handler.rest;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.security.data.Site;

/**
 * There is only ever one site configured for an installation, and the default site is initialized on startup. This
 * means there is never a need to create or delete a site from a remote location. The only thing that is needed is to
 * view the configured site, or too update an existing site.
 */
@CommandHandler(
        namespace = "site",
        description = "Contains methods that can be used to get back information about Sites")
public interface ISiteCommandHandler {

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:site:view")
    public Site getSite()
            throws Exception;


    @SyncCommand
    @RequiresPermissions("platform:security:administrative:site:edit")
    public Site updateSite(@Arg(name = "Site", required = true) Site Site)
            throws Exception;

}
