Ext.define('DD.view.core.NewCanvasWindow', {
    extend:'Ext.window.Window',
    alias :'widget.newcanvaswindow',
    title:'New Canvas',
    height:180,
    width:300,
    modal:true,
    resizable:false,
    draggable:false,
    layout:'fit',
    items:[
        {
            xtype:'form',
            layout:'anchor',
            defaults:{anchor:'100%'},
            bodyPadding:10,
            items:[
                {
                    xtype:'numberfield',
                    allowBlank:false,
                    fieldLabel:'No of Rows',
                    name:'noOfRows',
                    minValue:10,
                    value:DashboardManager.noOfRows
                },
                {
                    xtype:'numberfield',
                    allowBlank:false,
                    fieldLabel:'No of Coloumns',
                    name:'noOfCols',
                    minValue:15,
                    value:DashboardManager.noOfCols
                }
            ]
        }
    ],
    buttons:[
        {
            text:'Cancel',
            handler : function() {
                this.up('window').close();
            }
        },
        {
            text:'Create',
            handler:function() {
                var canvasWindow = this.up('window');
                var form = canvasWindow.down('form').getForm();

                if (form.isValid()) {
                    var values = form.getValues();
                    var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');

                    DashboardManager.isEditMode = true;
                    DashboardManager.currentDashboardId = null;
                    DD.loadingWindow = Ext.widget('progresswindow', {
                        text: 'Creating "New Dashboard" ...'
                    });

                    canvasWindow.close();
                    window.location.href='#/editDashboard';

                    DD.setPage(Ext.create('DD.view.dashboard.DashboardDesignerPane'), function() {
                        DD.currentPage.down('dashboardcanvasholder').resizeCanvas(values.noOfRows, values.noOfCols, function(canvas) {
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
                }
            }
        }
    ]
});
