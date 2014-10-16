
// VIEW: Service type error Window
// Displays a list of required Service Types needed in current configuration
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ServiceTypeErrorWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.servicetypeerrorwindow',

    title: 'Required Service Type Instances',
    modal:true,
    width: 850,
    height:250,
    autoShow: true,
    padding: 10,
    resizable:false,
    draggable: false,
    initComponent: function(config) { 
    	var errors=this.errorList;
        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: this.close
        }];

        this.items = [{
            xtype: 'serviceerrorlist',
            errors:errors
        }];

        this.callParent(arguments);
       
    }
});

