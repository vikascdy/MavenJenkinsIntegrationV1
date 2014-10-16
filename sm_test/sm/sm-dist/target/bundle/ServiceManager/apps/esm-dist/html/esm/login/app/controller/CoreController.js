Ext.define('Security.controller.CoreController', {
    extend : 'Ext.app.Controller',
    models: [
        'User',
        'Contact'
    ],
    stores : [],
    views : [ 'core.LoginPage', 'core.InvalidPage', 'core.ForgotPassword', 'core.ResetPassword', 'core.ChangePassword',
        'core.Home', 'core.ProgressWindow',  'core.LoadingWindow' ],

    init : function() {
        this.definePasswordType();
        this.control({
            'loginpage #login' : {
                click : function(btn) {
                    var values = btn.up('form').getForm().getValues();
                    var rememberMe = btn.up('loginpage').down('#rememberMe').getValue();

                    UserManager.login(values.username, values.password,values.domain, values.organization, rememberMe,
                        function(redirectURL, sessionId) {
                            SessionManager.getCurrentUser(function(user) {

                                if (user != null) {

                                    SessionManager.createUserCookie(user,
                                        sessionId, function() {
                                            var url = SessionManager
                                                .getRedirectURL();
                                            if (url == null) {
                                                url = '/';
                                            }
                                            if(user.changePasswordAtFirstLogin==true)
                                                location.href='/esm/login/#!/ChangePassword';
                                            else {
                                                TenantManager.getCurrentUserTenant(function(tenant) {
                                                    if (tenant && typeof(tenant.landingPage) !=='undefined'
                                                        && tenant.landingPage.length >0) {

                                                        location.href=tenant.landingPage;

                                                    }
                                                    else {
                                                        location.href = '/';
                                                    }
                                                });

                                            }
                                        });
                                }
                            });
                        });
                }
            },
            'forgotpassword #resetPassword' : {
                click : function(btn,page) {
                    var form = btn.up('form').getForm();
                    var messageHolder=page.down('#messageHolder');
                    var values = form.getValues();
                    if (form.isValid()) {
                        Security.loadingWindow = Ext.widget('progresswindow', {
                            text: 'Processing Request...'
                        });
                        PasswordManager.sendResetPasswordEmail(
                            values.emailAddress, function() {
                                messageHolder.setText('Follow the link sent to "'+values.emailAddress+'" to reset password.');
                                messageHolder.show();
                                form.reset();
                                Security.removeLoadingWindow(function() {

                                });
                            }, this);
                    }
                }
            },
            'resetpassword #updatePassword' : {
                click : function(btn,page) {
                    var form = btn.up('form').getForm();
                    var values = form.getValues();
                    var messageHolder=page.down('#messageHolder');
                    if (form.isValid()) {
                        Security.loadingWindow = Ext.widget('progresswindow', {
                            text: 'Updating password...'
                        });
                        PasswordManager.updatePassword(
                            values.newPasswd, values.token, function(status) {
                                if(status==true)
                                    messageHolder.setText('Password successfully updated. Please re-login.');
                                else
                                    messageHolder.setText(status);

                                messageHolder.show();
                                form.reset();
                                Security.removeLoadingWindow(function() {

                                });
                            }, this);
                    }
                }
            },
            'changepassword #updatePassword' : {
                click : function(btn,page) {

                    var form = btn.up('form');
                    var formBasic = form.getForm();
                    var values = formBasic.getValues();
                    var messageHolder=page.down('#messageHolder');

                    if (formBasic.isValid()) {
                        Security.loadingWindow = Ext.widget('progresswindow', {
                            text: 'Updating password...'
                        });
                        //var userRecord=Ext.create('Security.model.User',page.user);
                        UserManager.changePasswordAtFirstLogin(page.user,page.user.usename,values.password,
                            function(status) {
                                if(status==true){
                                    messageHolder.setText('Password successfully updated.');
                                    Ext.MessageBox.show({
                                        title: 'Password Changed',
                                        msg: 'Password successfully updated..',
                                        buttons: Ext.MessageBox.YES,
                                        buttonText:{
                                            yes: "Home"
                                        },
                                        fn: function(){
                                            window.location='/';
                                        }
                                    });
                                    form.setDisabled(true);
                                }

                                else
                                    messageHolder.setText(status);

                                messageHolder.show();
                                formBasic.reset();
                                
                                Security.removeLoadingWindow(function() {

                                });
                            }, this);
                    }
                }
            }
        });
    },
    definePasswordType: function() {
        Ext.apply(Ext.form.VTypes, {
            password : function(val, field) {
                if (field.initialPassField) {
                    var pwd = Ext.getCmp(field.initialPassField);
                    return (val == pwd.getValue());
                }
                return true;
            },
            passwordText: 'The password and confirmation do not match!'
        });
    }
});