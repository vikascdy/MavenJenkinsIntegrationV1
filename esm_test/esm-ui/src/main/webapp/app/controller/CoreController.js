Ext.define('Security.controller.CoreController', {
    extend: 'Ext.app.Controller',

    stores: [],
    views: [
        'core.Home',
        'core.InvalidPage',
        'core.ProgressWindow',
        'common.FlexFieldComponent',
        'common.BaseGridList',
        'common.CustomProgressBar'
    ],

    init: function() {
    	        
    }
});