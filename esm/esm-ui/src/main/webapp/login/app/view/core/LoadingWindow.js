Ext.define('Security.view.core.LoadingWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.loadingwindow',
	width:150,
    modal:true,
    border: false,
    cls: 'transparency',
    resizable:false,
    draggable:false,
    closable:false,
    autoShow:true,
    frame: false,
    shadow: false,
    title:null,
    html:'<img src="../resources/images/ajax-loader.gif"/>'
});


