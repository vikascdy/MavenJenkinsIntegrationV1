Ext.define('Util.UserManager', {});

window.UserManager = {

    username : null,
    userId : null,

    userCacheMap : new Ext.util.HashMap(),

    checkIfLoggedIn : function(callback, scope) {

//        Ext.Ajax.request({
//            url : 'proxy/rest/service/Security-Service/user.getCurrentUser',
//            method:'POST',
//            success : function(response) {
//                var respObj = Ext.decode(response.responseText);
//                if (respObj.success != false) {
//                    UserManager.userId = respObj.id;
//                    Ext.callback(callback, this, [respObj]);
//                } else {
//                    var pathname = window.location.pathname;
//                    var searchString = window.location.search;
//                    var hashString = window.location.hash;
//                    window.location.href = '/security/login/?redirectURL=' + pathname + searchString + hashString;
//                }
//            },
//            failure : function(response) {
//                var respObj = Ext.decode(response.responseText);
//                Ext.Msg.alert('Operation Failed', respObj.error);
//            }
//        });

        UserManager.userId = 1;
        Ext.callback(callback, this, []);

    }
};