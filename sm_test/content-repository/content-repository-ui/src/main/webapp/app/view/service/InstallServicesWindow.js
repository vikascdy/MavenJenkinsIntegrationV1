
// VIEW: Install Services Window
// Displays a list of available Service Types next to a list of currently
// installed Services on a given Node, and allows installation and removal of
// Services using drag-and-drop.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.InstallServicesWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.installserviceswindow',

    title: 'Install New Services',
    modal:true,
    width: 600,
    height: 450,
    autoShow: true,
    layout: 'border',
    padding: 10,
    node: null,
    resizable:false,
    draggable: false,
    modal:true,
    initComponent: function(config) {    
        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: function(){
            	SM.reloadAll();
            	this.close();
            }
        }];

        this.items = [{
            xtype: 'servicetypelist',
            title: 'Available Services',
            hideHeaders: true,
            width:267,
            margin: 2,
            region:'west',
            viewConfig: {
                plugins: [
                    Ext.create('Ext.grid.plugin.DragDrop', {
                        ddGroup: 'install',
                        dragText: 'Drag to the right column to install.',
                        enableDrop: false
                })]
            }
        }, {
            xtype: 'container',
            border: false,
            region:'center',
            width: 40,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'component',
                border: false,
                flex: 1
            }, {
                xtype: 'button',
                itemId: 'add',
                margin: 2,
                width: 36,
                height: 32,
                padding: '7 1',
                iconCls: 'next-button-icon'
            }, {
                xtype: 'button',
                itemId: 'remove',
                margin: 2,
                width: 36,
                height: 32,
                padding: '7 1',
                iconCls: 'back-button-icon'
            }, {
                xtype: 'component',
                border: false,
                flex: 1
            }]
        }, {
            xtype: 'servicelist',
            region:'east',
            title: 'Currently Installed Services',
            parentItem: this.node,
            hideHeaders: true,
            showColumns: ['serviceName'],
            width:267,
            margin: 2,
            tbar: [{
                xtype: 'label',
                text: 'Node:',
                margin: '0 4'
            }, {
                xtype: 'configitempicker',
                searchCriteria: {type: 'Node', isEditable: true},
                flex: 1
            }],
    
            viewConfig: {
                plugins: [
                    Ext.create('Ext.grid.plugin.DragDrop', {
                        ddGroup: 'install',
                        dragText: 'Drag to the left column to uninstall.',
                        enableDrop: false
                })]
            }
        },{
        	xtype:'component',
        	flex:1,
        	region:'south',
        	maxHeight:80,
        	autoScroll:true,
        	margin:'5 0 0 0',
        	id:'serviceTypeDescription',
        	hidden:true,
        	cls:'service-type-description',
        	title:'Service Description',
        	html:'No description found'
        }];

        this.callParent(arguments);
        if (this.node && this.node.isEditable())
            this.down('configitempicker').setItem(this.node);
        else {
            var node = this.down('configitempicker').getItem();
            if (!node)
                Ext.Error.raise("No free nodes available; either no nodes exist or all nodes have roles.");
            this.down('servicelist').parentItem = node;
            this.down('servicelist').reload();
        }
    }
});

