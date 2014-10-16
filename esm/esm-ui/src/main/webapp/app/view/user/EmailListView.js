Ext.define('Security.view.user.EmailListView', {
    extend:'Ext.view.View',
    alias:'widget.emaillistview',
    initComponent : function() {
        this.store = Ext.create('Ext.data.Store', {
            fields:['email','emailType']
        });
        this.tpl = new Ext.XTemplate(
            '<tpl for=".">' +
                '<div class="emailContainer">'+
                '<div class="emailDetails">{email}</div>'+
                '<div class="emailType emailType-{emailType}">{emailType}</div>'+
                '</div>'+
                '<div class="emailSeparator"></div>'+
            '</tpl>'
        );
        this.callParent(arguments);
    }
});