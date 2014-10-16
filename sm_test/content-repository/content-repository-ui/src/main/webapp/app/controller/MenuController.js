// CONTROLLER: Menu Controller
// Manages the Edifecs global header menu and its event handlers.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.MenuController', {
    extend: 'Ext.app.Controller',

    views: [
        'Edifecs.ApplicationBar'
    ],
    init: function() {
        var me=this;
        this.control({
            'ApplicationBar #Logout': {
                click: function(btn) {
                    SM.displayLoginPage();
                }
            },
            'ApplicationBar #changePassword': {
                click: function(btn) {
                    Ext.widget('changepasswordwindow', {user: UserManager.username,type:'Change'});
                }
            },
            'ApplicationBar #serviceManager' : {
                click: function(btn) {
                    me.application.fireEvent('changeurl', ['config']);
                }
            },
            'ApplicationBar #manageUsers' : {
                click: function(btn) {
                    me.application.fireEvent('changeurl', ['security']);
                }
            },
            'ApplicationBar #jobScheduler' : {
                click: function(btn) {
                    me.application.fireEvent('changeurl', ['jobs']);
                }
            },

            'ApplicationBar #contentRepositoryManager' : {
                click: function(btn) {
                    me.application.fireEvent('changeurl', ['content']);                
                }
            }
        });
    }
});
