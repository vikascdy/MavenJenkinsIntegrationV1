Ext.onReady(function() {
	    Ext.create('Ext.panel.Panel', {
                renderTo: document.getElementById('panel'),
                componentCls: 'roundAllPanel removeBG',
                border: 1,
                title: 'This is a panel',
                width:445,
                collapsible: true,
                items:{
                    xtype: 'gridpanel',
                    store: Ext.data.StoreManager.lookup('employeeStore'),
                    border: 1,
                    padding:10,
                    columns: [
                                {text: 'First Name',  dataIndex:'firstname'},
                                {text: 'Last Name',  dataIndex:'lastname'},
                                {text: 'Hired Month',  dataIndex:'hired', xtype:'datecolumn', format:'M'},
                                {text: 'Department (Yrs)', xtype:'templatecolumn', tpl:'{dep} ({senority})'}
                            ]
                    }
	});
		   
});


