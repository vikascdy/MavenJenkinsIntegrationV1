// ABSTRACT VIEW: Generic Page
// Provides a header, padding, and a copyright notice for full-screen pages
// such as the New Config page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.abstract.GenericPage', {
    extend : 'Ext.container.Container',
    alias : 'widget.genericpage',

    header : 'Page Title',

    layout : 'border',
    cls : 'generic-page',

    minHeight : 600,
    autoScroll : false,

    initComponent : function(config) {
        var oldItems = this.items;
        if (SM.testMode) {
            this.footer = '<p><span style="color:red; font-weight:bold;">TEST MODE ENABLED&nbsp;&nbsp;&nbsp;&nbsp;</span>' + this.footer + '</p>';
        }

        this.items = [ {
            xtype : 'container',
            border : false,
            region : 'north',
            items : [ {
                xtype : 'allconfigslink',
                hidden : true,
                listeners : {
                    'afterrender' : function() {
                        this.getEl().on('click', function(e, t) {
                            e.stopEvent();
                            location.hash = '#!/config';
                        }, null, {
                            delegate : '.all-config-link'
                        });

                    }
                }
            }, {
                xtype : 'button',
                itemId : 'back',
                hidden : true
            }, {
                xtype : 'component',
                cls : 'generic-page-header',
                height : 45,
                padding : '0 0 25 20',
                html : '<h1>' + this.header + '</h1>'
            } ]
        }, {
            xtype : 'container',
            border : false,
            region : 'center',
            minWidth : 960,
            layout : 'fit',
            cls : 'generic-page-content',
            padding : '0 20 0 20',
            items : oldItems
        }, {
            xtype : 'container',
            border : false,
            region : 'south',
            height : 40,
            padding : '14 20 0 20',
            cls : 'generic-page-footer',
            layout : {
                type : 'hbox'
            },
            items : [ {
                xtype : 'component',
                html : '<p>Environment : ' + SM.environmentName + '</p>'
            }, {
                xtype : 'tbspacer',
                flex : 1
            }, {
                xtype : 'component',
                html : '<p>Copyright &copy; 2013, Edifecs Inc</p>'
            } ]

        } ];

        this.callParent(arguments);
    }
});
