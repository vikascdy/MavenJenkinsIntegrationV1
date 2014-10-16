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
		{name:'suspended', type:'boolean', defaultValue:false},
        {name:'deleted', type:'boolean', defaultValue:false},
        {name:'createdDateTime', type:'string'},
        {name:'formattedCreatedDateTime',convert : function(value, record) {
            var dueDate = record.get('createdDateTime');
            if (dueDate) {
                var date = new Date(dueDate);
                return Ext.Date.format(date, 'd M Y, H:i:s');
            } else
                return "";
        }},
        {name:'modifiedDateTime', type:'string'},
        {name:'formattedModifiedDateTime',convert : function(value, record) {
            var dueDate = record.get('modifiedDateTime');
            if (dueDate) {
                var date = new Date(dueDate);
                return Ext.Date.format(date, 'd M Y, H:i:s');
            } else {
                return "";
			}
        }},
        {name:'lastLoginDateTime', type:'string'},
        {name:'formattedLastLoginDateTime',convert : function(value, record) {
            var lastLoginDateTime = record.get('lastLoginDateTime');
            if (lastLoginDateTime) {
                var date = new Date(lastLoginDateTime);
                return Ext.Date.format(date, 'd M Y, H:i:s');
            } else {
				return;
			}
        }},
		
        {name:'contact', type:'Security.model.Contact', defaultValue: null},
		
        {name:'name', convert : function(value, record) {
            var contact = record.get('contact');
			if (contact) {
				if(contact.middleName==undefined)
					contact.middleName="";
				
				if (contact.salutation) {
					return contact.salutation + ' ' + contact.firstName + ' ' + contact.middleName + ' ' + contact.lastName;
				} else {
					return contact.firstName + ' ' + contact.middleName + ' ' +contact.lastName;
				}
			} else {
				return record.get('username');
        	}
        }},
        {name:'salutation',defaultValue:'', convert : function(value, record) {
            var contact = record.get('contact');
			if (contact) {
				return contact.salutation;
			} else {
				return;
        	}
		}},
        {name:'firstName',defaultValue:'', convert : function(value, record) {
            var contact = record.get('contact');
			if (contact) {
				return contact.firstName;
        	} else {
				return record.get('username');
        	}
        }},
        {name:'middleName',defaultValue:'', convert : function(value, record) {
            var contact = record.get('contact');
			if (contact && contact.middleName) {
				return contact.middleName;
        	} else {
				return '';
        	}
        }},
        {name:'lastName',defaultValue:'', convert : function(value, record) {
            var contact = record.get('contact');
			if (contact) {
				return contact.lastName;
        	} else {
				return;
        	}
        }},
        {name:'address', convert : function(value, record) {
            var contact = record.get('contact');
			if (contact) {
				return contact.address;
        	} else {
				return;
        	}
        }},
        {name:'emailAddress', convert : function(value, record) {
			var contact = record.get('contact');
			if (contact) {
				return contact.emailAddress;
        	} else {
				return;
        	}
        }},
        {name:'username', type:'string'}
    ]
});

