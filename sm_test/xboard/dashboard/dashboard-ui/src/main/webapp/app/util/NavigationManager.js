Ext.define('Util.NavigationManager', {});

window.NavigationManager = {

    initiateRoutes : function(callback) {


        Path.map("#/login").to(function() {
            NavigationManager.showLoginPage();
        });

        Path.map("#/dashboard").to(function() {
            UserManager.checkIfLoggedIn(function() {
                DashboardManager.getXBoards(function(xBoards) {
                    if (xBoards.length == 0)
                        window.location.href = '#/editDashboard';
                    else
                        NavigationManager.showDashboardHome(function() {

                        });
                });
            });
        });

        Path.map("#/dashboard/:dashboardId").to(function() {
            var me = this;
            UserManager.checkIfLoggedIn(function() {
                DashboardManager.isEditMode = false;
                var loadingText = 'Loading Dashboard...';

                if (DashboardManager.dashboardName != 'Dashboard')
                    loadingText = 'Loading Dashboard "' + DashboardManager.dashboardName + '"...';

                DD.loadingWindow = Ext.widget('progressbarwindow', {
                    text: loadingText
                });
                DashboardManager.getXBoards(function() {
                    DD.setPage(Ext.create('DD.view.dashboard.DashboardDesignerPane'), function() {
                        DashboardManager.parseXBoardConfiguration(me.params["dashboardId"], function() {
                            DashboardManager.progressBarStatus.updateText('Done !');
                            DD.removeLoadingWindow(function() {
                                DD.currentPage.down('dashboardcanvasholder').showDashboardOptions(true, function() {

                                });
                            });
                        });
                    });
                });
            });
        });

        Path.map("#/editDashboard").to(function() {
            var me = this;
            UserManager.checkIfLoggedIn(function() {
                DashboardManager.isEditMode = true;
                DashboardManager.currentDashboardId = null;
                var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');
                DD.loadingWindow = Ext.widget('progresswindow', {
                    text: 'Creating "New Dashboard" ...'
                });

                DD.setPage(Ext.create('DD.view.dashboard.DashboardDesignerPane'), function() {
                    DD.currentPage.down('dashboardcanvasholder').resizeCanvas(DashboardManager.noOfRows, DashboardManager.noOfCols, function(canvas) {
                        DD.currentPage.down('dashboardcanvasholder').setDashboardName('New Dashboard', function() {
                            var rootNode = dashboardElementsTreeStore.getRootNode();
                            rootNode.removeAll();
                            rootNode.set('text', 'New Dashboard');
                            rootNode.commit();
                            DD.removeLoadingWindow(function() {
                            });
                        });

                    });
                });
            });
        });

        Path.map("#/editDashboard/:dashboardId").to(function() {
            var me = this;
            UserManager.checkIfLoggedIn(function() {
                if (me.params["dashboardId"]) {
                    DashboardManager.getXBoards(function(records, dashboardListStore) {
                        var index = dashboardListStore.find('id', me.params["dashboardId"]);
                        if (index != -1) {
                            DashboardManager.currentDashboardId = me.params["dashboardId"];
                            DashboardManager.isEditMode = true;
                            DD.loadingWindow = Ext.widget('progressbarwindow', {
                                text: 'Loading Dashboard...'
                            });

                            DD.setPage(Ext.create('DD.view.dashboard.DashboardDesignerPane'), function() {
                                DashboardManager.parseXBoardConfiguration(DashboardManager.currentDashboardId, function(xBoardObj) {
                                    DashboardManager.progressBarStatus.updateText('Done !');
                                    DD.removeLoadingWindow(function() {
                                        DD.currentPage.down('dashboardcanvasholder').showDashboardOptions(false, function() {

                                        });
                                    });
                                });
                            });
                        }
                        else
                            window.location.href = '#/dashboard';
                    });
                }
                else
                    window.location.href = '#/dashboard';
            });
        });


        Path.root("#/dashboard");

        Path.rescue(function() {
            NavigationManager.showErrorPage();
        });

        Path.listen();

        Ext.callback(callback, this);

    },

    showErrorPage : function() {
        //Implementation in future
        DD.removeLoadingWindow(function() {
            Ext.Msg.alert("Invalid URL", "Redirecting to home page.");
            NavigationManager.showDashboardHome();
        });
    },

    showLoginPage : function() {
        UserManager.checkIfLoggedIn(function(userObj) {
            window.location.href = '#/dashboard';
        });
    },

    showDashboardHome : function(callback) {
        DD.setPage(Ext.create('DD.view.dashboard.DashboardDesignerPane'), function() {
            DD.currentPage.down('dashboardcanvasholder').loadDefaultDashboardToCanvas(function() {
                Ext.callback(callback, this);
            });
        });
    }

};