Ext.define('Security.view.tenant.TenantDetailPaneHeader', {
    extend:'Ext.container.Container',
    alias:'widget.tenantdetailpaneheader',
    padding:20,
    initComponent : function() {
        var me = this;


//        var actionMenu = Ext.widget({xtype:'tenantactionmenu'});

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
                        itemId:'tenantName',
                        text:'No Tenant Selected',
                        cls:'detailPaneHeading'
                    },
                    {
                        xtype:'tbspacer',
                        flex:1
                    }
//                    {
//                        xtype:'button',
//                        text:'Actions',
//                        menu:actionMenu,
//                        disabled:true,
//                        itemId:'actionButton'
//                    }
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
                xtype:'component',
                hidden:true,
                margin
                    :
                    '10 0 5 0',
                itemId
                    :
                    'tenantInfoView',
                listeners
                    :
                {
                    'afterrender'
                        :
                        function () {
                            this.getEl().on('click', function(e, t, opts) {
                                e.stopEvent();
                                var x = e.browserEvent.clientX;
                                var y = e.browserEvent.clientY;
                                var assigneeMenu = Ext.widget({xtype:'assigneemenu',tenant:me.tenant,mode:'assignee'});
                                assigneeMenu.showAt(x - 50, y + 20);
                            }, null, {delegate: '.assignee'});

                            this.getEl().on('click', function(e, t, opts) {
                                e.stopEvent();
                                var x = e.browserEvent.clientX;
                                var y = e.browserEvent.clientY;
                                var priorityMenu = Ext.widget({xtype:'prioritymenu',tenant:me.tenant});
                                priorityMenu.showAt(x - 90, y + 20);
                            }, null, {delegate: '.priority'});

                            this.getEl().on('click', function(e, t, opts) {
                                e.stopEvent();
                                var x = e.browserEvent.clientX;
                                var y = e.browserEvent.clientY;
                                var priorityMenu = Ext.widget({xtype:'duedatemenu',tenant:me.tenant});
                                priorityMenu.showAt(x - 50, y + 20);
                            }, null, {delegate: '.dueDate'});
                        }
                }
            },
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
                    'tenantDescriptionContainer',
                items
                    :
                    [
                        {
                            xtype:'displayfield',
                            anchor:'100%',
                            value: '<i>This Tenant has no description.</i>'
                        }
                    ]

            }
        ];
        
        this.callParent(arguments);
    },

    loadTenantDetail : function(record, callback) {
    	var me=this;
        if (record) {

//            var actionButton=me.down('#actionButton');
            var tenantNameLabel = me.down('#tenantName');
//            var tenantInfoView = this.down('#tenantInfoView');
            var tenantDescriptionField = me.down('#tenantDescriptionContainer').down('displayfield');

//            actionButton.enable();

            tenantNameLabel.setText(record.get('canonicalName'));

            me.tenant = record;
            
            if (record.get('description') && record.get('description').length > 0) {
                tenantDescriptionField.setValue(record.get('description'));
            }
            else
                tenantDescriptionField.reset();

//            var infoTpl = new Ext.XTemplate(
//                '<table style="width:100%;">',
//                '<tr>',
//                '<td class="header-icon mico-calender dueDate" title="Due Date">',
//                '{formattedDueDate}',
//                '</td>',
//                '<td class="header-icon mico-member assignee" title="Assignee">',
//                '{assignee}',
//                '</td>',
//                '<td class="header-icon mico-status-new" title="Status">',
//                'Active',
//                '</td>',
//                '<td class="header-icon mico-member" title="Owner">',
//                '{owner}',
//                '</td>',
//                '</tr>',
//                '</table>'
//            );
//            tenantInfoView.update('');
//            tenantInfoView.update(infoTpl.apply(record.data));

        }
        Ext.callback(callback, this);
    },
    
    reset : function(){
//    	 this.down('#actionButton').disable();
    	 this.down('#tenantName').setText('No Tenant Selected');
         this.down('#tenantDescriptionContainer').down('displayfield').reset();    	
    }
});