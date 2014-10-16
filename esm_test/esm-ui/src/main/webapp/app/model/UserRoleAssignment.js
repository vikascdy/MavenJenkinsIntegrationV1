Ext.define('Security.model.UserRoleAssignment', {
    extend: 'Ext.data.Model',
    fields: ['id', 'userId', 'roleId'//,
  //      {name:'roleCaption', mapping:'role.canonicalName', persist:false}
    ],

    associations: [
        {type: 'belongsTo', model: 'Security.model.User', autoLoad:false, getterName:'getUser', setterName:'setUser', foreignKey:'userId'},
        {type: 'belongsTo', model: 'Security.model.Role', autoLoad:true, getterName:'getRole', setterName:'setRole', foreignKey:'roleId'}
    ]
});
