// VIEW: Content Repository Page
// A file browser that allows the user to navigate a JCR content repository and
// to upload, download, move, and delete files.
// ----------------------------------------------------------------------------

Ext.define('SM.view.content.ContentRepositoryPage', {
    extend: 'Ext.container.Container',
    alias : 'widget.contentrepositorypage',

    layout: 'border',

    initComponent: function() {
    	
        this.items = [{
            xtype: 'container',
            border: false,
            region: 'north',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype : 'container',
                height: 75,
                layout: {
                    type : 'hbox',
                    align: 'stretch'
                },
                items: [{
                    xtype:'component',
                    padding:'30 0 0 20',
                    html  : "<span id='header-config-name'>Content Repository Manager</span>",
                    border: false
                }]
            }, {
                xtype: 'repositorytoolbar',
                margin:'0 20 0 20',
                border:true
            }]
        }, {
            xtype : 'repositorytree',
            region: 'center',
            flex  : 3,
            padding:'0 0 0 20'
        }, {
            xtype : 'fileinfobar',
            region: 'east',
            split : true,
            flex  : 1,
            padding:'0 20 0 0'
        }, {
            xtype: 'container',
            border: false,
            region: 'south',
            height:40,
            padding:'14 20 0 20',
            cls: 'generic-page-footer',
            layout:{
                 type:'hbox'
            },
            items:[{
                 xtype:'component',
                 html: '<p>Environment : ' + SM.environmentName + '</p>'
            },{
                 xtype:'tbspacer',
                 flex:1
            },{
                 xtype:'component',
                 id:'contentRepositoryFooter',
                 html: '<p>Copyright &copy; 2013, Edifecs Inc</p>'
            }]
        }];
    	 
        this.callParent(arguments);
        
        JCRLocale.initializeLabelMap();
        JCRLocale.initializeValueMap();
        
        if (SM.testMode) {
            Ext.getCmp('contentRepositoryFooter').html = '<p><span style="color:red; font-weight:bold;">TEST MODE ENABLED&nbsp;&nbsp;&nbsp;&nbsp;</span>Copyright &copy; 2013, Edifecs Inc</p>';
        }
    }
});

