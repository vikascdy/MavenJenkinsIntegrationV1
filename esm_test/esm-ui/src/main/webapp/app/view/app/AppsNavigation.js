Ext.define('Security.view.app.AppsNavigation', {
    extend:'Ext.view.View',
    alias:'widget.appsnavigation',
    initComponent : function() {
        var me=this;
        this.store = Ext.create('Ext.data.Store', {
            fields:['street','city','zip','country','addressType']
        });
        this.tpl = new Ext.XTemplate(
            '<tpl for=".">' +
                '<div class="addressContainer">'+
                '<div class="addressDetails">{street}<br/>{city} {state} {zip}<br/>{country}</div>'+
                '<div class="addressType">{addressType}</div>'+
                '</div>'+
                '<div class="addressSeparator"></div>'+
            '</tpl>'
        );
        this.listeners = {
            'afterrender':function () {
                this.getEl().on('click', function(e, t, opts) {
                    e.stopEvent();

                }, null, {delegate: '.addressType'});


                this.setAddresses();
            }
        };
        this.callParent(arguments);
    },

    setAddresses : function() {
        var view = this;
        var data=[
            {'street':'2600 116th Street #200','city':'Bellevue','state':'WA','country':'United States','zip':98004,'addressType':'OFFICE'},
            {'street':'456789 119 Street ','city':'Mercer','state':'WA','country':'United States','zip':98004,'addressType':'BRANCH'}
        ];
        this.getStore().loadData(data);
//            view.update(view.tpl.apply(data));

    }
});