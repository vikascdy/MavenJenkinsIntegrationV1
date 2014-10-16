Ext.define('Security.view.organization.OrganizationDetailPaneHeader', {
    extend:'Ext.container.Container',
    alias:'widget.organizationdetailpaneheader',
    padding:20,
    initComponent : function() {
        var me = this;



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
                        itemId:'organizationName',
                        text:'No Organization Selected',
                        cls:'detailPaneHeading'
                    },
                    {
                        xtype:'tbspacer',
                        flex:1
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
            }
            ,
            {
                xtype:'container',
                layout
                    :
                    'anchor',
                margin
                    :
                    '10 0 10 0',
                itemId
                    :
                    'organizationDescriptionContainer',
                items
                    :
                    [
                        {
                            xtype:'displayfield',
                            anchor:'100%',
                            value: '<i>This Organization has no description.</i>'
                        }
                    ]

            }
        ]
            ;
        this.callParent(arguments);
    },

    loadOrganizationDetail : function(record, callback) {
        if (record) {
        	Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Organization Details...'});
            var organizationNameLabel = this.down('#organizationName');
            var organizationDescriptionField = this.down('#organizationDescriptionContainer').down('displayfield');


            organizationNameLabel.setText(record.get('canonicalName'));
            this.organization = record;
            if (record.get('description') && record.get('description').length > 0) {
                organizationDescriptionField.setValue(record.get('description'));
            }
            else
                organizationDescriptionField.reset();


        }
        Security.removeLoadingWindow(function(){
        	Ext.callback(callback, this);
    	});
        
    },
    
    reset : function(){
    	this.down('#organizationName').setText('No Organization Selected');
        this.down('#organizationDescriptionContainer').down('displayfield').reset();    	
    }
});