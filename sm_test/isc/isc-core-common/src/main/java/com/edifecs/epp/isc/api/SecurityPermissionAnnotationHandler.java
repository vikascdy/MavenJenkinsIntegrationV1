package com.edifecs.epp.isc.api;

import java.lang.annotation.Annotation;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.subject.Subject;

import com.edifecs.epp.isc.annotations.RequiresPermissions;

public class SecurityPermissionAnnotationHandler extends AuthorizingAnnotationHandler {

    public SecurityPermissionAnnotationHandler() {
        super(RequiresPermissions.class);
    }
    
    /**
     * Returns the annotation {@link RequiresPermissions#value value}, from which the Permission will be constructed.
     *
     * @param a the RequiresPermissions annotation being inspected.
     * @return the annotation's <code>value</code>, from which the Permission will be constructed.
     */
    protected String[] getAnnotationValue(Annotation a) {
        RequiresPermissions rpAnnotation = (RequiresPermissions) a;
        return rpAnnotation.value();
    }
	
    /**
     * Ensures that the calling <code>Subject</code> has the Annotation's specified permissions, and if not, throws an
     * <code>AuthorizingException</code> indicating access is denied.
     *
     * @param a the RequiresPermission annotation being inspected to check for one or more permissions
     * @throws org.apache.shiro.authz.AuthorizationException
     *          if the calling <code>Subject</code> does not have the permission(s) necessary to
     *          continue access or execution.
     */
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequiresPermissions)) return;

        RequiresPermissions rpAnnotation = (RequiresPermissions) a;
        String[] perms = getAnnotationValue(a);
        Subject subject = getSubject();

        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return;
        }
        //TODO: Reenable this annotation type
//        if (Logical.AND.equals(rpAnnotation.logical())) {
//            getSubject().checkPermissions(perms);
//            return;
//        }
//        if (Logical.OR.equals(rpAnnotation.logical())) {
//            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
//            boolean hasAtLeastOnePermission = false;
//            for (String permission : perms) if (getSubject().isPermitted(permission)) hasAtLeastOnePermission = true;
//            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
//            if (!hasAtLeastOnePermission) getSubject().checkPermission(perms[0]);
//
//        }
    }
	
}
