package com.edifecs.epp.isc.api;

import java.lang.annotation.Annotation;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;

import com.edifecs.epp.isc.annotations.RequiresRoles;

public class SecurityRoleAnnotationHandler extends AuthorizingAnnotationHandler {

    public SecurityRoleAnnotationHandler() {
        super(RequiresRoles.class);
    }
	
    /**
     * Ensures that the calling <code>Subject</code> has the Annotation's specified roles, and if not, throws an
     * <code>AuthorizingException</code> indicating that access is denied.
     *
     * @param a the RequiresRoles annotation to use to check for one or more roles
     * @throws org.apache.shiro.authz.AuthorizationException
     *          if the calling <code>Subject</code> does not have the role(s) necessary to
     *          proceed.
     */
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequiresRoles)) return;

        RequiresRoles rrAnnotation = (RequiresRoles) a;
        String[] roles = rrAnnotation.value();

        if (roles.length == 1) {
            getSubject().checkRole(roles[0]);
            return;
        }
        // TODO: Reenable this annotation capability
//        if (Logical.AND.equals(rrAnnotation.logical())) {
//            getSubject().checkRoles(Arrays.asList(roles));
//            return;
//        }
//        if (Logical.OR.equals(rrAnnotation.logical())) {
//            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
//            boolean hasAtLeastOneRole = false;
//            for (String role : roles) if (getSubject().hasRole(role)) hasAtLeastOneRole = true;
//            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
//            if (!hasAtLeastOneRole) getSubject().checkRole(roles[0]);
//        }
    }
	
}
