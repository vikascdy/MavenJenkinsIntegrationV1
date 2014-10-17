
Ext.define('PP.controller.MenuController', {
    extend: 'Ext.app.Controller',
    views: [
        'Edifecs.ApplicationBar',
        'Edifecs.Navigation'
    ],
    init: function() {
    	var me=this;
        this.control({
            'ApplicationBar #uxDemoPortal': {
                click: function(btn) {
                	PP.setPage(Ext.create('PP.view.core.UXDemoPortal'));
                }
            },
			'ApplicationBar #logoutId': {
                click: function(btn) {
                   window.open('', '_self', '');window.close();
                }
            },
            'ApplicationBar #searchId': {
                'searchClicked': function(btn) {
                  console.log(btn);
                }
            }
        });
    }
});
