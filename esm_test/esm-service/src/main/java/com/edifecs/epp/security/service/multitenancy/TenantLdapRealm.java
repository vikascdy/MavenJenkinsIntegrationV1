package com.edifecs.epp.security.service.multitenancy;

import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;

public class TenantLdapRealm extends ActiveDirectoryRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token) && (token instanceof UsernamePasswordAuthenticationToken);
    }
}
