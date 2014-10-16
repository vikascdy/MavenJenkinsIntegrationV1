
// VIEW: New Resource Window 
// A popup window for creating new Resources for a Cluster.
// ----------------------------------------------------------------------------

Ext.define('SM.view.resource.NewResourceWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.newresourcewindow',

    title   : 'Create New Resource',
    layout  : {
        type: 'vbox',
        align: 'stretch'
    },
    autoShow: true,
    cluster : null,
    modal:true,
    selector: null,
    width   : 360,
    height  : 430,
    border  : false,
    resizable:false,
    draggable: false,
    items   : [{
        xtype : 'form',
        itemId: 'resourceform',
        id: 'resourceform',
        layout: 'anchor',
        height: 150,
        bodyPadding: '0 12 0 12',
        border:false,
        defaults: {anchor: '100%'},
        bodyStyle: "background-color:#F6F8FA !important;",
        items : [{
            xtype: 'textfield',
            name: 'name',
            fieldLabel: 'Name',
            labelSeparator:'',
            allowBlank: false
        }, {
            xtype: 'textarea',
            name: 'description',
            labelSeparator:'',
            fieldLabel: 'Description'
        }, {
            xtype: 'combobox',
            itemId: 'restypeSelector',
            store: 'ResourceTypeStore',
            queryMode: 'local',
            name: 'restype',
            labelSeparator:'',
            fieldLabel: 'Type',
            displayField: 'name',
            valueField: 'name',
            editable: false,
            allowBlank: false,
            listeners: {
                change: function(cbox, value) {
                    cbox.up('newresourcewindow').showTypeForm(cbox.findRecordByValue(value));
                }
            }
        }]
    }, {
        xtype : 'container',
        itemId: 'propertiesformctr',
        layout: 'fit',
        margin:'10 0 0 0',
        html  : '<p style="padding:0px 12px 0px 12px;">Select a Resource type above, and properties will appear here.</p>',
        flex  : 1
    }],

    buttons: [{
        text: "Create Resource",
        itemId: "create",
        handler: function(btn) {
            var nrw = btn.up('newresourcewindow');
            nrw.createResource();
        }
    }, {
        text: "Cancel",
        handler: function(btn) {
            var nrw = btn.up('newresourcewindow');
            nrw.close();
        }
    }],

    showTypeForm: function(type) {
        var propFormCtr = this.down('#propertiesformctr');
        propFormCtr.removeAll();
        propFormCtr.add({
            xtype: 'propertiesform',
            preventHeader: true,
            bodyPadding:'0 12 0 12',
            autoSave: false,
            object: type,
            border:false
        });
    },

    createResource: function() {
        try {
            var form1 = this.down('#resourceform').getForm();
            if (!form1.isValid())
                Ext.Error.raise("Some of the mandatory fields are empty or invalid.");
            var values = form1.getFieldValues();
            var form2 = this.down('propertiesform');
            if (!form2)
                Ext.Error.raise("You must select a Resource Type.");
            else if (!form2.getForm().isValid())
                Ext.Error.raise("One or more of the property values is invalid or missing.");
            var properties = form2.getForm().getFieldValues();
            var cluster = this.cluster;
            //var cluster = this.down('configitempicker').getItem();
            //if (!cluster)
            //    Ext.Error.raise("No Cluster selected.");
            cluster.shouldBeA('Cluster');
            var success = cluster.spawnResource(values.name, values.restype,
                values.description, properties);
            if (success) {
                SM.reloadAll();
                if (this.selector)
                    this.selector.setItem(ConfigManager.config.getChildrenWith({
                        type: 'Resource',
                        name: values.name
                    })[0]);
            } else
                Ext.Error.raise("Failed to create resource.");
            this.close();
        } catch (err) {
            Functions.errorMsg(err);
        }
    },

    initComponent: function() {
        this.callParent(arguments);
        if (!this.cluster)
            this.cluster = ConfigManager.config.get('clusters')[0];
        //if (this.cluster)
        //    this.down('configitempicker').setItem(this.cluster);
        Ext.getStore('ResourceTypeStore').load();
    }
});
