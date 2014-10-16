
// VIEW: Service Info Pane
// Displays comprehensive information on a single Service. An Info Pane is
// displayed in the center pane of the Service Manager Page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ServiceInfoPane', {
    extend: 'Ext.container.Container',
    alias : 'widget.serviceinfopane',
    service: null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    padding: '0 20 0 15',

    showEditForm: function() {
        this.down("#subheader").hide();
        this.down("#editform").show();
        this.down("#editform").loadRecord(this.service);
    },

    hideEditForm: function() {
        this.down("#editform").hide();
        this.down("#subheader").show();
    },

    initComponent: function(config) {
    	var me=this;
    	
        // TODO: Perform actual permission checking here.
        var admin = true; //UserManager.admin;
        
        var active = this.service.get('status') == 'active';
        
        this.items = [{
            xtype: 'infopaneheader',
            item: this.service,
            editButtonVisible: admin,
            propertiesButtonVisible: false,
            actionButtonVisible:true,
            actionButtons:[
							{
							    xtype:'button',
							    disabled:active,
							    cls: 'header-properties-button',
							    tooltip:'Start Service',
							    icon: 'resources/images/toolbar-play.png',
							    itemId: 'startService'
							}, {
							    xtype:'button',
							    disabled:!active,
							    cls: 'header-properties-button',
							    tooltip:'Stop Service',
							    icon: 'resources/images/toolbar-stop.png',
							    itemId: 'stopService'
							}
                           ],
           updateStatus : function(service){

        	   var active = service.get('status') == 'active';
        	   me.down('#startService').setDisabled(!active);
        	   me.down('#stopService').setDisabled(active);        	   
       	   
           },
            listeners: {
                editbutton: function(item, btn, header) {
                    header.up('serviceinfopane').showEditForm();
                }
            }
        }, {
            xtype: 'infopanesubheader',
            itemId: 'subheader',
            item: this.service,
            fields: [
                Functions.statusIconHeader1,
                Functions.statusIconHeader2,
            {
                name: 'serviceName',
                title: 'Service Type Name'
            }, {
                name: 'serviceVersion',
                title: 'Version'
            }, {
                name: 'className',
                title: 'Class Name',
                dataFn: function(item, callback) {
                    var type = item.getServiceType();
                    return type ? type.get('className') : '[Unknown]';
                }
            }]
        }, {
            xtype: 'serviceform',
            itemId: 'editform',
            height: 140,
            service: this.service,
            horizontal: true,
            columnPadding: 16,
            hidden: true,
            buttons: [{
                text: 'Save',
                formBind:true,
                handler: function(btn) {
                    if (!btn.up('serviceform').getForm().isValid()) {
                        Functions.errorMsg("One or more of the form values is invalid or missing.");
                        return;
                    }
                    btn.up('serviceform').save();
                    btn.up('serviceinfopane').hideEditForm();
                }
            }, {
                text: 'Cancel',
                handler: function(btn) {
                    btn.up('serviceinfopane').hideEditForm();
                }
            }]
        }, {
            xtype:'component',
            cls:'separator-line',
            layout:{align:'left'},
            html:'<div></div>'
        }, {
            xtype: 'container',
            border: false,
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'propertiesform',
                cls:'sm-item-header header-plain-bkg plain-body-panel',
                object: this.service,
                flex: 1,
                margin: '15 15 0 0',
                bodyPadding: '10 25 10 10'
            }, {
                xtype: 'tabpanel',
                cls:'sm-item-header',
                activeTab: 0,
                flex: 1,
                margin: '15 0 0 0',
                items: [{
                    xtype: 'dependencylist',
                    service: this.service
                }, {
                    xtype: 'resourcedependencylist',
                    service: this.service
                }]
            }]
        }, {
            xtype: 'tabpanel',
            cls:'sm-item-header sm-tab-plain-bkd',
            flex: 1,
            margin: '15 0 0 0',
            activeTab: 0,
            items: [{
                xtype: 'errorlist',
                parentItem: this.service
            }, {
                xtype: 'logfilelist',
                service: this.service,
                disabled : this.service.get('status')=='offline'
            }, {
                xtype: 'servicelist',
                title: '<span class="sm-item-header">All Instances in Cluster</span>',
                parentItem: this.service.getParent().getParent().getParent(), // Cluster
                extraCriteria: {serviceName: this.service.get('serviceName')}
            }]
        }];

        this.callParent(arguments);
    }
});

