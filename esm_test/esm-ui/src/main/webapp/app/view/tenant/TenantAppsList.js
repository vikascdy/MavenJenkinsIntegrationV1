Ext.define('Security.view.tenant.TenantAppsList', {
    extend:'Ext.grid.Panel',
    alias:'widget.tenantappslist',
    store:'TenantAppsListStore',
    hideHeaders:true,
    border:false,
    bodyBorder:false,
    rowLines:false,
    initComponent :  function() {
        var me = this;

        this.viewConfig = {
            listeners : {
                'expandbody':function(rowNode, record, expandRow, eOpts) {
                    if (!me.disableAppAdd) {
                        var btn = expandRow.getElementsByClassName('uninstallButton')[0];
                        var buttonAdded = expandRow.getElementsByClassName('buttonAdded')[0];

                        if (buttonAdded == undefined) {
                            Ext.widget({
                                xtype: 'button',
                                renderTo: btn,
                                text:'Uninstall',
                                cls:'buttonAdded',
                                ui:'greenbutton',
                                listeners: {
                                    click: function () {
                                        alert('Uninstalling...');
                                    }
                                }
                            });
                        }
                    }
                },

                afterrender : function(grid) {
                    var store = grid.getStore();
                    var rec = store.getAt(0);
                    if (rec) {
                        grid.getSelectionModel().select(rec);
                    }
                }
            }};

        this.callParent(arguments);
    },

    plugins: [
        {
            ptype: 'rowexpander',
            selectRowOnExpand :true,
            rowBodyTpl : new Ext.XTemplate(
                '<div class="appDetailContainer">',
                '<p>{description}</p>',
                '<p>Version : <b>{version}</b></p>',
                '<p>Release Date : <b>{releaseDate}</b></p>',
                '<p>Published By : <b>{publishedBy}</b></p>',
                '<p>Rating: {rating:this.getRatings}</p>',
                '<div  style="float:right; margin: 10px;" class="uninstallButton"></div>',
                '</div>',
                {
                	getRatings : function(v){
            		if(v==0)
            			return 'No Rating Found';
            		else{
                		var ratingStr='';
                		for(var i=0;i<v;i++)
                			ratingStr += '<img src="resources/images/rating.png" style="margin-right:5px; padding-top:" />';
                		return ratingStr;
            		}
                	},
                    addButton: function(v) {
                        return Ext.widget({xtype:'button',text:'Uninstall'});
                    }
                })
        }
    ],
    animCollapse: true,
    columns : [
        {
            header:'Name',
            dataIndex:'name',
            menuDisabled:true,
            flex:1,
            renderer : function(v) {
                return '<div class="appName">' + v + '</div>';
            }
        }
    ]
});

