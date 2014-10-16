Ext.define('Security.model.User', {
    extend: 'Ext.data.Model',
    requires: [
        'Security.model.Contact'
    ],

    idProperty: 'id',
    fields:[
        {name:'id', type:'long'},
        {name:'humanUser', type:'boolean'},
        {name:'changePasswordAtFirstLogin', type:'boolean'},
        {name:'active', type:'boolean', defaultValue:true},
        {name:'deleted', type:'boolean', defaultValue:false},
        {name:'createdDateTime', type:'string'},
        {name:'formattedCreatedDateTime',convert : function(value, record) {
            var dueDate = record.get('createdDateTime');
            if (dueDate) {
                var date = new Date(dueDate);
                return Ext.Date.format(date, 'd M Y');
            } else
                return "";
        }},
        {name:'modifiedDateTime', type:'string'},
        {name:'formattedModifiedDateTime',convert : function(value, record) {
            var dueDate = record.get('modifiedDateTime');
            if (dueDate) {
                var date = new Date(dueDate);
                return Ext.Date.format(date, 'd M Y');
            } else
                return "";
        }},
        {name:'lastLoginDateTime', type:'date', dateFormat:'time'},
        {name:'contact', type: Functions.childArrayType('Security.model.Contact')},
        {name:'name', convert : function(value, record) {
            var contact = record.get('contact')[0];
            return contact.get('salutation') + ' ' + contact.get('firstName') + ' ' + contact.get('lastName');
        	}
        },
        {name:'salutation', convert : function(value, record) {
            var contact = record.get('contact')[0];
            return contact.get('salutation');
        	}
        },
        {name:'firstName', convert : function(value, record) {
            var contact = record.get('contact')[0];
            return contact.get('firstName');
        	}
        },
        {name:'middleName', convert : function(value, record) {
            var contact = record.get('contact')[0];
            return contact.get('middleName');
        	}
        },
        {name:'lastName', convert : function(value, record) {
            var contact = record.get('contact')[0];
            return contact.get('lastName');
        	}
        },
        {name:'address', convert : function(value, record) {
            var contact = record.get('contact')[0];
            return contact.get('address');
        	}
        },
        {name:'emailAddress', convert : function(value, record) {
            return record.get('contact')[0].get('emailAddress');
        	}
        },
        {name:'username', type:'string'}
    ]
});

