package com.edifecs.epp.security.service.context;

import java.util.Properties;

public final class SecurityContext {

    // TODO : static variables on a multi instance cluster?

    private static String currentUser;

    private static AuthRealmType authenticationRealm;

    private SecurityContext() {
        // private constructor for singleton
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(String currentUser) {
        SecurityContext.currentUser = currentUser;
    }

    public static AuthRealmType getAuthenticationRealm() {
        return authenticationRealm;
    }

    public static void setAuthenticationRealm(AuthRealmType authenticationRealm) {
        SecurityContext.authenticationRealm = authenticationRealm;
    }

    public static Properties getAsMap() {

        Properties p = new Properties();
        p.put("currentUser", currentUser);
        p.put("authenticationRealm", authenticationRealm);
        return p;
    }

    public enum AuthRealmType {
        LDAP("Ldap"), DB("Database"), CERTIFICATE("Certificate");

        private String type;

        private AuthRealmType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
