Ext.define('Security.model.MemberRoles', {
    extend: 'Ext.data.Model',
    fields: ['id', 'memberOfRoleId', 'memberRoleId',
        {name:'memberOfRoleCaption', mapping:'memberOfRole.caption', persist:false},
        {name: 'memberRoleCaption', mapping:'memberRole.caption',persist:false}],

    proxy: {
        type: 'rest',
        url: 'security-data/member/roles',

        reader: {
            type: 'json',
            root: 'results'
        }
    },
    associations: [
        {type: 'belongsTo', model: 'Security.model.Role', autoLoad:false, getterName:'getMemberRole', setterName:'setMemberRole', foreignKey:'memberOfRoleId'},
        {type: 'belongsTo', model: 'Security.model.Role', autoLoad:false, getterName:'getMemberOfRole', setterName:'setMemberOfRole', foreignKey:'memberRoleId'}
    ]
});