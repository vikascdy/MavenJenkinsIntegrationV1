package com.edifecs.epp.security.service.multitenancy;

import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.Collection;

public class MultiTenantAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        assertRealmsConfigured();
        UsernamePasswordAuthenticationToken tat = null;
        Realm tenantRealm = null;

        if (!(authenticationToken instanceof UsernamePasswordAuthenticationToken)) {
            //ToDo: SK - Need to have validation if it is not tenant token.
            //For now: it is calling super if it is not multi tenant and allowing authenticate.
            //throw new AuthenticationException("Unrecognized token , not a type of TenantAuthenticationToken ");
            super.doAuthenticate(authenticationToken);
        } else {
            tat = (UsernamePasswordAuthenticationToken) authenticationToken;
            tenantRealm = lookupRealm(tat.getDomain());
        }

        return doSingleRealmAuthentication(tenantRealm, tat);

    }

    protected Realm lookupRealm(String clientId) throws AuthenticationException {
        Collection<Realm> realms = getRealms();
        for (Realm realm : realms) {
            if (realm.getName().equalsIgnoreCase(clientId)) {
                return realm;
            }
        }
        throw new AuthenticationException("No realm configured for Client " + clientId);
    }
}