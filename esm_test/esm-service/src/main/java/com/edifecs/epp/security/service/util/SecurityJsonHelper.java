package com.edifecs.epp.security.service.util;

import com.edifecs.epp.security.data.Permission;

import java.util.ArrayList;
import java.util.List;

public class SecurityJsonHelper {

    private List<SecurityJsonPermission> permissions = new ArrayList<>();

    private List<RolesWithPermissions> roles = new ArrayList<>();

    public List<SecurityJsonPermission> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        return permissions;
    }

    public void setPermissions(List<SecurityJsonPermission> permissions) {
        this.permissions = permissions;
    }

    public List<RolesWithPermissions> getRoles() {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        return roles;
    }

    public void setRoles(List<RolesWithPermissions> roles) {
        this.roles = roles;
    }

    SecurityJsonHelper(List<SecurityJsonPermission> permissions,
                       List<RolesWithPermissions> roles) {
        super();
        this.permissions = permissions;
        this.roles = roles;
    }

    public Permission getPermissionFromString(String jsonPermission) {
        // no name field in permission api
        Permission permission = new Permission();

        // parse permission string
        final String[] permissionArray = jsonPermission.split(":");
        switch (permissionArray.length) {
            case 5:
                permission.setCanonicalName(permissionArray[4]);
                permission.setSubTypeCanonicalName(permissionArray[3]);
                permission.setTypeCanonicalName(permissionArray[2]);
                permission.setCategoryCanonicalName(permissionArray[1]);
                permission.setProductCanonicalName(permissionArray[0]);
                break;
            default:
                throw new SecurityException("'" + jsonPermission
                        + "' is not a valid permission string.");
        }

        return permission;
    }

    public Permission getPermissionDataObj(SecurityJsonPermission jsonPermission) {

        // no name field in permission api
        Permission permission = new Permission();
        permission.setDescription(jsonPermission.getDescription());

        // hold ref to id if permission is persisted
        if (null != jsonPermission.getEntityId())
            permission.setId(jsonPermission.getEntityId());

        // parse permission string
        final String[] permissionArray = jsonPermission.getPermission().split(":");
        switch (permissionArray.length) {
            case 5:
                permission.setCanonicalName(permissionArray[4]);
                permission.setSubTypeCanonicalName(permissionArray[3]);
                permission.setTypeCanonicalName(permissionArray[2]);
                permission.setCategoryCanonicalName(permissionArray[1]);
                permission.setProductCanonicalName(permissionArray[0]);
                break;
            default:
                throw new SecurityException("'" + jsonPermission.getPermission()
                        + "' is not a valid permission string.");
        }

        return permission;
    }

    public Permission getJsonPermissionByRefId(String refId) {

        for (SecurityJsonPermission p : permissions) {
            if (p.getId().equals(refId))
                return getPermissionDataObj(p);
        }
        return null;
    }

    /**
     * Mapper classes for mapping with json format, specified in the json file
     *
     * @author abhising
     */
    public class RolesWithPermissions {

        private String name;
        private String description;
        private List<String> permissions;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getPermissions() {
            if (permissions == null) {
                permissions = new ArrayList<>();
            }
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }

    }

    public class SecurityJsonPermission {

        private String permission;
        private String name;
        private String description;
        // used for ref within json file only
        private String id;
        private Long entityId;

        public Long getEntityId() {
            return entityId;
        }

        public void setEntityId(Long entityId) {
            this.entityId = entityId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}
