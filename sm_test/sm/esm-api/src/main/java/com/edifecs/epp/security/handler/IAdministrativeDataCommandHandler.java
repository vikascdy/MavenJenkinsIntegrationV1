package com.edifecs.epp.security.handler;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.security.data.RealmConfig;
import com.edifecs.epp.security.data.SecurityRealm;

import java.util.List;

/**
 * @author willclem
 */

// TODO: Move these commands into more appropriate command handlers
@Deprecated
@CommandHandler
public interface IAdministrativeDataCommandHandler {

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:realm:test")
    public boolean testLdapConnection(
            @Arg(name = "realm", required = true) SecurityRealm realm)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:realm:view")
    public List<RealmConfig> getRealmPropertiesMeta(
            @Arg(name = "realmType", required = true) String realmType);

    // TODO: This needs to be reviewed
    @SyncCommand
    @NullSessionAllowed
    public Boolean isEmailServiceAvailable();

}
