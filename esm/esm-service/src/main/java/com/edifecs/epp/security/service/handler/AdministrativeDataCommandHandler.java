package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.data.CustomProperty;
import com.edifecs.epp.security.data.RealmConfig;
import com.edifecs.epp.security.data.RealmType;
import com.edifecs.epp.security.data.SecurityRealm;
import com.edifecs.epp.security.handler.IAdministrativeDataCommandHandler;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.realm.SecurityActiveDirectoryRealm;
import com.edifecs.epp.security.service.realm.SecurityLdapRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * @author willclem
 */
public class AdministrativeDataCommandHandler extends AbstractCommandHandler
        implements IAdministrativeDataCommandHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final SecurityContext context;

    public AdministrativeDataCommandHandler(SecurityContext context) {
        this.context = context;
    }

    @Override
    public boolean testLdapConnection(SecurityRealm realm) throws Exception {
        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(null, realm);

        Properties p = CustomProperty.parseProperties(realm.getProperties());
        String username = p.getProperty("username");
        String password = p.getProperty("password");
        p.remove("username");
        p.remove("password");
        return ldapRealm.testLdapConnection(username, password);
    }

    @Override
    public List<RealmConfig> getRealmPropertiesMeta(String realmType) {

        switch (RealmType.valueOf(realmType)) {
            case LDAP:
                return SecurityLdapRealm.getLdapConfigMeta();
            case ACTIVEDIRECTORY:
                return SecurityActiveDirectoryRealm.getConfigMeta();
            default:
                return null;
        }
    }

    @Override
    public Boolean isEmailServiceAvailable() {
        try {
            List<Address> adds = getCommandCommunicator()
                    .getAddressRegistry().getAddressesForServiceTypeName(
                            "Email Service");
            logger.debug("addesss : {}", adds.size());
            if (!adds.isEmpty())
                return true;

        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("email service not configured.");
        }
        return false;
    }

}
