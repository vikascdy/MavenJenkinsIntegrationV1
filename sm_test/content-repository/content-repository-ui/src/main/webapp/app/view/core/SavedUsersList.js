// VIEW: Saved Users List
// A Grid that lists all saved users
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.SavedUsersList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.saveduserslist',

    store : 'SavedUsersStore',
    title : 'Saved Users',
    itemId: 'savedUsersList',
    initComponent : function(){
    this.columns= [

        {
            text: 'Username',
            dataIndex:'username',
            flex:2,
            renderer: function(value) {
                return "<a href='#' class='config-link'>" + value + "</a>";
            }
        },
        {
            text: 'First Name',
            dataIndex: 'fname',
            flex: 2

        },
        {
            text: 'Last Name',
            dataIndex: 'lname',
            flex: 2
        },
        {
            text: 'Email ID',
            dataIndex: 'email',
            flex: 3
        },
        {
            text: 'Role',
            dataIndex: 'role',
            flex: 1,
            renderer: function(value) {
                return Functions.capitalize(value);
            }
        }

    ];
    Ext.getStore('SavedUsersStore').load();
    this.callParent(arguments);
    this.getSelectionModel().on('selectionchange', this.onSelectChange, this);
    },
    
    onSelectChange: function(selModel, selections){
    	var record=selModel.getSelection()[0]; 
    	if(record){
		    	if(record.get('username')!=UserManager.username){
			        this.down('#delete').setDisabled(selections.length === 0);
			        this.down('#reset').setDisabled(selections.length === 0);
		    	}
		    	else
		    		{
		    		 this.down('#delete').setDisabled(true);
		 	        this.down('#reset').setDisabled(true);
		    		}
    	}
    }
});


