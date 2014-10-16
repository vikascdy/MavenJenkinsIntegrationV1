Ext.define('DD.controller.ApplicationBarController', {
    extend: 'Ext.app.Controller',
    views:[],
    models:[],
    stores:[],
    refs: [

    ],
    init: function() {
        this.control({
            'DoormatApplicationBar #workspaceId':{
                click : function() {
                    window.location.href='#/dashboard';
                }
            }
        });
    }
});