// VIEW: Define Config Window
// A popup window displayed after selecting a Product, Template or imported
// file on the Create Config Page. Allows the user to define basic attributes
// of the config before proceeding to the Service Manager Page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.DefineConfigWindow', {
    extend: 'SM.view.core.EditConfigWindow',
    alias : 'widget.defineconfigwindow',

    title : 'Define General Configuration Properties',
    iconCls : 'ico-config',
    modal : true,
    resizable:false,
    initComponent: function(){
    this.buttons= [
        {
            text: "Create",
            handler: function(btn) {
                var dcw = btn.up('defineconfigwindow');
                var form = dcw.down('configform');
                if (!form.getForm().isValid())
                    Functions.errorMsg("One or more of the form values is invalid or missing.");
                else {
                    form.save();
                    dcw.config.data.timestamp = Date.now();
                    
//                    var formVal=form.getForm();
//                    var productName=formVal.findField('productName').getValue();
//                    var productVersion=formVal.findField('productVersion').getValue();
                    
                    SM.changesSavedStatus=false;
                    var page=Ext.create('SM.view.core.ServiceManagerPage',{
                    	unsavedChanges:true
                    });
                    SM.setPage(page,true);
                    SM.checkServiceManagerChanges=true;
                    dcw.close();
                }
            }
        },
        {
            text: "Cancel",
            handler: function(btn) {
                var dcw = btn.up('defineconfigwindow');
                dcw.close();
            }
        }
    ];
    this.callParent(arguments);
    }
});

