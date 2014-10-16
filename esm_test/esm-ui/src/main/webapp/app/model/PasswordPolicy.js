Ext.define('Security.model.PasswordPolicy', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields:[
        {name:'id', type:'long'},
        {name:'changePasswdAtFirstLogin', type:'boolean'},
        {name:'enabled', type:'boolean'},
        {name:'passwdAge', type:'integer'},
        {name:'passwdHistory', type:'integer'},
        {name:'passwdLockoutDuration',type:'integer'},
        {name:'passwdMaxFailure',type:'integer'},
        {name:'passwdResetFailureLockout',type:'integer'},
        {name:'passwdRegexName',type:'string'},
		{name:'passwdRegex',type:'string'},
        {name:'passwdRegexDesc',type:'string'}
    ]
});

