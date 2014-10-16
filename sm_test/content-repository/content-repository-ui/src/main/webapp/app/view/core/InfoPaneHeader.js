
// VIEW: Info Pane Header
// The title at the top of the center Info Pane in the Service Manager Page.
// Displays the type and name of the item being displayed on the pane, and
// optionally provides Edit and Properties buttons.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.InfoPaneHeader', {
    extend: 'Ext.container.Container',
    alias : 'widget.infopaneheader',
    border: false,
    item   : null,
    iconCls: null,
    editButtonVisible: true,
    propertiesButtonVisible: false,

    initComponent: function(config) {
        var me=this;
        this.items = [{
            xtype: 'component',
            cls: 'info-pane-header',
            itemId: 'header-title',

            border: false,
            data: {
                type: this.item.getType()=='Cluster'?'Configuration':this.item.getType(),
                name: this.item.get('name'),
                iconCls: "lrgico-" + this.item.getType().toLowerCase()
            },
            tpl: "<div class='large-icon {iconCls}' style='float: left; '></div><h2 style='margin-left: 32px;'>{type}: {name}</h2>",
            reload: function() {
                var item = this.up('infopaneheader').item;
                this.update({
                    type: item.getType()=='Cluster'?'Configuration':item.getType(),
                    name: item.get('name'),
                    iconCls: "lrgico-" + item.getType().toLowerCase()
                });
            }
        }];
        if (this.editButtonVisible) this.items.push({
            xtype: 'button',
            cls: 'header-edit-button',
            itemId: 'header-edit-button',
            tooltip:'Edit information',
            text: '',
            iconCls: 'ico-edit',
            handler: function(btn) {
                btn.up('infopaneheader').fireEvent('editbutton',
                    btn.up('infopaneheader').item, btn, btn.up('infopaneheader'));
            }
        });
        if (this.propertiesButtonVisible) this.items.push({
            xtype: 'button',
            cls: 'header-properties-button',
            itemId: 'header-properties-button',
            text: 'Properties',
//            iconCls: 'ico-properties',
            handler: function(btn) {
                btn.up('infopaneheader').item.showPropertiesWindow();
            }
        });
        
        if (this.actionButtonVisible) 
        	Ext.each(this.actionButtons,function(button){
        		
        		me.items.push(button);
        		
        	});
        	

        this.callParent(arguments);
    }
});

