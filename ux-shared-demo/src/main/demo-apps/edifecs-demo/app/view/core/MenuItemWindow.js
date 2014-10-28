Ext.define('PP.view.core.MenuItemWindow', {
    extend: 'Ext.container.Container',
    alias : 'widget.menuitemwindow',
    autoScroll:true,
    bodyPadding:'10',
    initComponent : function() {
    //Using IFRAME to display the preview of the component. No AJAX call made here.
    this.html = '<iframe frameBorder="0" style="width:100%;height:100%;border:0px;" src="'+this.menuItem+'.html"></iframe>';
	this.callParent(arguments);
    }
});
