Ext.define('Security.view.user.UserDetailPaneHeader', {
    extend:'Ext.container.Container',
    alias:'widget.userdetailpaneheader',
    padding:20,
    initComponent : function() {
        var me = this;
        
        var actionMenu = Ext.widget({xtype:'useractionmenu'});

        this.items = [

            {
                xtype:'container',
//                padding:'20 20 10 20',
                flex:1,
                layout:{
                    type:'hbox',
                    align:'stretch'
                },
                items:[
                    {
                        xtype:'label',
                        itemId:'userName',
                        text:'No User Selected',
                        cls:'detailPaneHeading'
                    },
                    {
                        xtype:'tbspacer',
                        flex:1
                    },
                    {
                        xtype:'button',
                        text:'Actions',
                        menu:actionMenu,
                        disabled:true,
                        itemId:'actionButton'
                    }
                ]
            },
            {
                xtype:'component',
                cls
                    :
                    'horizontalLine',
                margin
                    :
                    '10 0 10 0',
                html
                    :
                    '<div></div>'
            },
            {
                xtype:'userprops',
                hidden:true,
                margin:'10 0 5 0',
                itemId:'userProps'
            }
        ]
            ;
        this.callParent(arguments);
    },

    loadUserDetail : function(record, callback) {
    	var me=this;
        if (record) {

        	var actionButton=this.down('#actionButton');
        	var userNameLabel = this.down('#userName');
            var userProps = this.down('#userProps');
            
            if(actionButton)
	            actionButton.enable();
            
	        me.down('useractionmenu').setUserRecord(record);
            

            userNameLabel.setText(record.get('name'));
            this.user = record;
           
            userProps.show();
            userProps.update(record.data);

        }
        Ext.callback(callback, this);
    },
    
    reset : function(){
    	this.down('#userName').setText('No User Selected');
        this.down('#userProps').hide();
        this.down('#actionButton').disable();
    }
});