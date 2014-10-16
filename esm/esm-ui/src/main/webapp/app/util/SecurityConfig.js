// SECURITYCONFIG.JS
// Configuration for Security UI.
// ----------------------------------------------------------------------------

Ext.define('Util.SecurityConfig', {}); // Placeholder, ignore this.

window.SecurityConfig = {

    configs: {

        DATE_TIME_FORMAT: 'd-M-Y g:iA',

        DATE_FORMAT: 'd-M-Y'

    },

    views: {
        MANAGE_USERS: 'users',
        MANAGE_ROLES: 'roles',
        AUDIT_PERMISSIONS: 'auditpermissions',
        AUDIT_LOG_SETTINGS: 'auditlog'
    },

    permissions: {
        users:{
            view : true,
            add : true,
            edit : true,
            remove : true
        },
        roles:{
            view : true,
            add : true,
            edit : true,
            remove : true
        },
        permissionlog:{
            edit: true
        }
    }
};

