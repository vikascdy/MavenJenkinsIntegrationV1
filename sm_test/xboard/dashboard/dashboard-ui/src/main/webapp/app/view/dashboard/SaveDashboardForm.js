Ext.define('DD.view.dashboard.SaveDashboardForm', {
    extend:'Ext.form.Panel',
    alias:'widget.savedashboardform',
    bodyPadding: 5,
    layout: 'anchor',
    border:false,
    defaults: {
        anchor: '100%'
    },
    defaultType: 'textfield',
    initComponent : function() {
        var me = this;
        var fields = [];

        var dashboardListStore = Ext.StoreManager.lookup('DashboardListStore');
        var index = dashboardListStore.find('id', DashboardManager.currentDashboardId);
        var selectedDashboard = null;
        if (index != -1) {
            selectedDashboard = dashboardListStore.getAt(index);
            if (me.mode == 'clone')
            selectedDashboard.set('name', 'Copy of ' + selectedDashboard.get('name'));
        }
        Ext.each(me.dashboardProperties, function(prop) {
            if (prop.name != 'Configuration') {
                fields.push({
                    xtype:prop.name == 'Description' ? 'textarea' : 'textfield',
                    fieldLabel:prop.name,
                    name:prop.name,
                    allowBlank:!prop.isRequired,
                    value:DashboardManager.currentDashboardId && selectedDashboard ? selectedDashboard.get(prop.name.toLowerCase()) : ''
                });
            }
        });
        this.items = fields;
        this.callParent(arguments);
    }
});