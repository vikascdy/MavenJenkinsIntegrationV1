Ext.define('Security.view.common.PageHeader', {
    extend:'Ext.container.Container',
    alias:"widget.pageheader",
    margins:{top:10, right:10, bottom:10, left:0},
    title:'Page Header',
    constructor: function(config) {
        Ext.apply(this, config || {});
        Ext.apply(this, {
            html: Ext.String.format('<div class="header-page">{0}</div>', this.title)
        });
        this.callParent();
    }
});