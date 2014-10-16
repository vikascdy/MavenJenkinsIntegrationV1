Ext.define('Security.view.user.UserActionMenu', {
    extend:'Ext.menu.Menu',
    alias:'widget.useractionmenu',
    plain:true,
    config : {userRecord:null},
    items:[
//        {
//            text:"Import Users"
//        },
        {
            text:"Update Credentials",
            itemId:'updateCredentials'
        }
//        {
//            text:"Send Password Reset Email"
//        }
    ]
});